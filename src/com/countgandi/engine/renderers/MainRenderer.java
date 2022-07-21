package com.countgandi.engine.renderers;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.Display;
import com.countgandi.engine.WorldManager;
import com.countgandi.engine.fbos.FrameBuffer;
import com.countgandi.engine.fbos.deferred.DeferredFrameBuffer;
import com.countgandi.engine.fbos.deferred.DeferredShader;
import com.countgandi.engine.fbos.gaussianblur.GaussianBlurFrameBuffer;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.objects.Camera;
import com.countgandi.engine.objects.Light;
import com.countgandi.engine.objects.Terrain;
import com.countgandi.engine.shaders.ShaderProgram;
import com.countgandi.engine.shaders.UniformBufferObject;
import com.countgandi.engine.shaders.fixed.StaticShader;
import com.countgandi.engine.shaders.sky.SkyShader;
import com.countgandi.engine.shaders.terrain.TerrainShader;
import com.countgandi.engine.utils.Maths;
import com.countgandi.game.Main;

public class MainRenderer {

	public static final Vector3f SKY_COLOR = new Vector3f(0.61f, 0.75f, 1f);
	public static final int MAX_LIGHTS = 1024;

	public static boolean WIREFRAME_MODE = false;

	public static Model quad;

	private Vector4f[] frustum = new Vector4f[6];

	private SkyShader skyShader;
	private StaticShader shader;
	private TerrainShader tshader;
	private EntityRenderer renderer;
	private TerrainRenderer trenderer;

	private Camera camera;

	private FrameBuffer screenFrameBuffer;
	private DeferredFrameBuffer dFrameBuffer;
	private GaussianBlurFrameBuffer gbFrameBuffer;
	private Display display;
	
	public MainRenderer(Display d, Camera camera) {
		this.display = d;
		this.camera = camera;

		GL45.glLineWidth(3);

		float[] positions = { -1, 1, /**/ -1, -1, /**/ 1, 1, /**/ 1, -1 };
		quad = Main.LOADER.loadToVAO(positions, 2);
		FrameBuffer.quad = quad;

		ShaderProgram.ubos[0] = new UniformBufferObject(0, 52);
		ShaderProgram.ubos[1] = new UniformBufferObject(1, MAX_LIGHTS * 16);

		skyShader = new SkyShader();
		shader = new StaticShader();
		tshader = new TerrainShader();

		renderer = new EntityRenderer(shader);
		trenderer = new TerrainRenderer(tshader);

		screenFrameBuffer = new FrameBuffer((int)display.getWidth(), (int)display.getHeight(), true, 3,
				new int[] { GL45.GL_RGBA, GL45.GL_RGBA16F, GL45.GL_RGBA32F }, null) {
		};
		dFrameBuffer = new DeferredFrameBuffer((int)display.getWidth(), (int)display.getHeight());
		gbFrameBuffer = new GaussianBlurFrameBuffer((int)display.getWidth(), (int)display.getHeight());

		FloatBuffer buffer = BufferUtils.createFloatBuffer(52);
		camera.getPerspectiveMatrix().store(buffer);
		camera.getViewMatrix().store(buffer);
		camera.getPerspectiveViewMatrix().store(buffer);
		buffer.flip();
		ShaderProgram.ubos[0].update(buffer);

		for (int i = 0; i < 6; i++)
			frustum[i] = new Vector4f();
	}

	public void preprocess() {
		frustum = Maths.createFrustumPlanes(frustum, camera.getPerspectiveViewMatrix());
	}

	public void renderScene(ArrayList<Light> lights, Terrain t, WorldManager m) {
		updateCameraData();
		updateLights(lights);
		((DeferredShader) dFrameBuffer.getShaderProgram()).start();
		((DeferredShader) dFrameBuffer.getShaderProgram()).updateShader(new Vector3f(0.4f, 0.4f, 0.4f), 0.02f);
		((DeferredShader) dFrameBuffer.getShaderProgram()).stop();
		if (WIREFRAME_MODE) {
			GL46.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
		} else {
			GL46.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
		}
		screenFrameBuffer.bind();

		renderSky();

		shader.start();
		shader.loadClipPlane(new Vector4f(0, -1, 0, 100000));
		renderer.render(m.entities, frustum);
		shader.stop();

		tshader.start();
		trenderer.render(t);
		tshader.stop();

		//multisampledScreenFrameBuffer.resolveToFbo(screenFrameBuffer);

		//gbFrameBuffer.bind();
		//gbFrameBuffer.render(dFrameBuffer);
		
		FrameBuffer.unbind();
		dFrameBuffer.render(screenFrameBuffer);

	}

	private void renderSky() {
		GL11.glDisable(GL45.GL_DEPTH_TEST);
		skyShader.start();
		skyShader.loadSkyColor(SKY_COLOR);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		skyShader.stop();
		GL11.glEnable(GL45.GL_DEPTH_TEST);
	}

	private FloatBuffer cameraBuffer = BufferUtils.createFloatBuffer(52);
	private void updateCameraData() {
		
		camera.getPerspectiveMatrix().store(cameraBuffer);
		camera.getViewMatrix().store(cameraBuffer);
		camera.getPerspectiveViewMatrix().store(cameraBuffer);
		camera.getPosition().store(cameraBuffer);
		cameraBuffer.put(1.0f);
		cameraBuffer.flip();
		ShaderProgram.ubos[0].update(cameraBuffer);
	}

	private FloatBuffer lightsBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS * 16);
	private void updateLights(ArrayList<Light> lights) {
		lightsBuffer.limit(MAX_LIGHTS * 16);
		
		/*ArrayList<Light> lights = new ArrayList<Light>();
		for(int i = 0; i < originalLights.size(); i++) {
			if(originalLights.get(i).getType() == Light.LIGHT_DIRECTIONAL || Maths.sphereFrustumIntersection(originalLights.get(i), frustum)) {
				lights.add(originalLights.get(i));
				
			}
		}
		System.out.println("Light count: " + lights.size());*/

		for (int i = 0; i < lights.size(); i++) {
			Light l = lights.get(i);
			new Vector4f(l.getPosition().x, l.getPosition().y, l.getPosition().z, l.getBrightness()).store(lightsBuffer);
		}
		lightsBuffer.position(MAX_LIGHTS * 4);
		for (int i = 0; i < lights.size(); i++) {
			Light l = lights.get(i);
			new Vector4f(l.getColor().x, l.getColor().y, l.getColor().z, l.getMinLight()).store(lightsBuffer);
		}
		lightsBuffer.position(MAX_LIGHTS * 8);
		for (int i = 0; i < lights.size(); i++) {
			Light l = lights.get(i);
			new Vector4f(l.getAttenuation().x, l.getAttenuation().y, l.getAttenuation().z, l.getRadius()).store(lightsBuffer);
		}

		lightsBuffer.position(MAX_LIGHTS * 12);
		new Vector4f(lights.size(), 0, 0, 0).store(lightsBuffer);

		lightsBuffer.flip();
		ShaderProgram.ubos[1].update(lightsBuffer);
		lightsBuffer.clear();
	}

	public void cleanUp() {
		this.screenFrameBuffer.cleanUp();
		this.dFrameBuffer.cleanUp();
		this.gbFrameBuffer.cleanUp();
		this.shader.cleanUp();
		this.skyShader.cleanUp();
		this.tshader.cleanUp();
	}

}

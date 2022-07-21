package com.countgandi.engine.renderers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL45;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.WorldManager;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.objects.Entity;
import com.countgandi.engine.objects.Light;
import com.countgandi.engine.shaders.shadow.ShadowShader;
import com.countgandi.engine.utils.Maths;

public class ShadowRenderer {

	private ShadowShader shader;

	private Matrix4f projectionMatrix, viewMatrix;

	public ShadowRenderer() {
		shader = new ShadowShader();
	}

	public void render(ArrayList<Light> lights, WorldManager m) {
		shader.start();

		Light light = lights.get(0);

		GL45.glCullFace(GL11.GL_BACK);
		GL40.glEnable(GL40.GL_CULL_FACE);

		for (Model model : m.entities.keySet()) {
			GL30.glBindVertexArray(model.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			List<Entity> batch = m.entities.get(model);

			for (Entity entity : batch) {
				shader.loadTransformationMatrix(entity.getTransformationMatrix());
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
		}
		GL30.glBindVertexArray(0);

		GL45.glCullFace(GL11.GL_FRONT);
		GL45.glDisable(GL11.GL_CULL_FACE);

		shader.stop();
	}

	private void updateLightProjectionMatrix() {
		projectionMatrix.setIdentity();
	}

	private void updateOrthographicMatrix() {

	}

}

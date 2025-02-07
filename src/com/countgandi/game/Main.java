package com.countgandi.game;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;
import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.Display;
import com.countgandi.engine.Loader;
import com.countgandi.engine.WorldManager;
import com.countgandi.engine.model.LandscapeMaterial;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.model.loaders.OBJLoader;
import com.countgandi.engine.model.textures.Texture;
import com.countgandi.engine.objects.Camera;
import com.countgandi.engine.objects.Entity;
import com.countgandi.engine.objects.Light;
import com.countgandi.engine.objects.Terrain;
import com.countgandi.engine.renderers.MainRenderer;

public class Main {

	// public static final int WIDTH = 1280, HEIGHT = 720;

	// This loader is for things that are needed all game long
	public static Loader LOADER = new Loader();

	private Display window;
	private MainRenderer renderer;

	private Camera camera;

	private LandscapeMaterial lMaterial;
	private ArrayList<Light> lights = new ArrayList<Light>();

	private WorldManager worldManager = new WorldManager();

	private Terrain terrain;

	public static boolean polygonLine = false, cursorDisabled = false;

	public Main() {
		window = new Display(1280, 720, "Buttercup");
		window.turnOffVysync();

		camera = new Camera(window);

		camera.updateViewMatrix();

		renderer = new MainRenderer(window, camera);

		lMaterial = new LandscapeMaterial(Texture.create().normalMipMap().anisotropic()
				.create("/Wall_CobblestoneMixed_albedo.png"));
		lMaterial.setHeightMap(Texture.create().normalMipMap().anisotropic().create("/heightmap.png"));

		lights.add(new Light(new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1, 1, 1), Light.LIGHT_DIRECTIONAL));

		for (int j = 0; j < 5; j++) {
			Light l = new Light(new Vector3f(25 + -j * 10, 1.0f, j * 10),
					new Vector3f((float) Math.random(), (float) Math.random(),
							(float) Math.random()),
					Light.LIGHT_LAMP);
			lights.add(l);
		}

		GLFW.glfwSetKeyCallback(window.getWindow(),
				GLFWKeyCallback.create((window, key, scancode, action, mods) -> {
					if (key == GLFW.GLFW_KEY_G && action == GLFW.GLFW_PRESS) {
						MainRenderer.WIREFRAME_MODE = !MainRenderer.WIREFRAME_MODE;
					}
					if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
						if (cursorDisabled) {
							GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR,
									GLFW.GLFW_CURSOR_NORMAL);
						} else {
							GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR,
									GLFW.GLFW_CURSOR_DISABLED);
						}
						cursorDisabled = !cursorDisabled;
					}
				}));

		Model m = OBJLoader.loadObjModelWithMaterials("pirateShip", LOADER);

		// for(int i = -10; i < 10; i++) {
		// for(int j = -10; j < 10; j++) {
		Entity e = new Entity(m) {
			@Override
			protected void tick() {

			}
		};
		e.setPosition(new Vector3f(0, 2.0f, 20));
		e.updateTransformation();
		worldManager.addEntity(e);
		// }//}
		terrain = new Terrain(lMaterial, LOADER);

		run();
	}

	private void tick() {
		camera.move();
		camera.updateViewMatrix();
	}

	private void physics() {

	}

	private void render() {
		renderer.preprocess();

		renderer.renderScene(lights, terrain, worldManager);
	}

	public void run() {
		while (!window.shouldClose()) {
			GL46.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

			tick();
			// physics();
			render();

			window.update();
		}

		renderer.cleanUp();
		window.destroy();
		GLFW.glfwTerminate();
	}

	public static void main(String[] args) {
		new Main();
	}

}

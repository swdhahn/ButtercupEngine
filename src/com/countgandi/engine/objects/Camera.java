package com.countgandi.engine.objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.Display;
import com.countgandi.engine.utils.Maths;
import com.countgandi.game.Main;

public class Camera {

	private static final float mouseSpeed = 0.3f;
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.2f;
	private static final float FAR_PLANE = 2000;
	protected static final float runSpeed = 20, sprintSpeed = 100, JumpPower = 30;

	protected Matrix4f projectionMatrix;
	protected Matrix4f perspectiveMatrix;
	protected Matrix4f orthographicMatrix;
	protected Matrix4f perspectiveViewMatrix;
	protected Matrix4f viewMatrix;
	protected Vector3f position = new Vector3f(0, 0, 0), rotation = new Vector3f(0, 0, 0);

	protected Vector3f velocity = new Vector3f();

	private Display window;

	public Camera(Display display) {
		this.window = display;
		projectionMatrix = Maths.createProjectionMatrix(NEAR_PLANE, FAR_PLANE, FOV, window);
		orthographicMatrix = Maths.createOrthographicMatrix(NEAR_PLANE, FAR_PLANE, window);
		perspectiveMatrix = projectionMatrix;
		viewMatrix = new Matrix4f();
		perspectiveViewMatrix = new Matrix4f();

		Main.cursorDisabled = true;
		GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		GLFW.glfwSetCursorPos(window.getWindow(), 0, 0);
	}

	public void move() {
		checkInputs();

		updateViewMatrix();
	}

	protected void checkInputs() {
		if (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
			this.velocity.z = -runSpeed;
		} else if (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
			this.velocity.z = runSpeed;
		} else {
			velocity.z = 0;
		}

		if (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
			this.velocity.x = runSpeed;
		} else if (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
			this.velocity.x = -runSpeed;
		} else {
			this.velocity.x = 0;
		}

		if (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
			this.velocity.y = -runSpeed;
		} else if (GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
			this.velocity.y = runSpeed;
		} else {
			velocity.y = 0;
		}

		double[] mousex = new double[1], mousey = new double[1];
		GLFW.glfwGetCursorPos(window.getWindow(), mousex, mousey);
		double mx = mousex[0], my = mousey[0];
		if (Main.cursorDisabled) {
			/*
			 * if (Mouse.getX() >= Main.WIDTH - 20) {
			 * Mouse.setCursorPosition(30, Mouse.getY());
			 * preX = Mouse.getX();
			 * }
			 * if (Mouse.getX() <= 20) {
			 * Mouse.setCursorPosition(Main.WIDTH - 30, Mouse.getY());
			 * preX = Mouse.getX();
			 * }
			 */

			rotation.x += my * 0.01f * mouseSpeed;
			rotation.y += mx * 0.01f * mouseSpeed;

			rotation.x = Maths.clamp(rotation.x, 2 * Math.PI - 1, 2 * Math.PI + 1.5);

			GLFW.glfwSetCursorPos(window.getWindow(), 0, 0);
		}

		float dx = (float) ((velocity.z * Display.getDelta()) * Math.sin(-rotation.y));
		float dx2 = (float) ((velocity.x * Display.getDelta()) * Math.cos(rotation.y));

		float dz = (float) ((velocity.z * Display.getDelta()) * Math.cos(-rotation.y));
		float dz2 = (float) ((velocity.x * Display.getDelta()) * Math.sin(rotation.y));

		position.x += dx + dx2;
		position.z += dz + dz2;
		position.y += velocity.y * Display.getDelta();

		// upwardsSpeed += Constants.GRAVITY * DisplayManager.getFrameTimeSeconds();
		// position.y += upwardsSpeed * DisplayManager.getFrameTimeSeconds();
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);

		Matrix4f.mul(perspectiveMatrix, viewMatrix, perspectiveViewMatrix);
	}

	public void updateProjectionMatrix() {
		perspectiveMatrix = Maths.createProjectionMatrix(NEAR_PLANE, FAR_PLANE, FOV, window);
		orthographicMatrix = Maths.createOrthographicMatrix(NEAR_PLANE, FAR_PLANE, window);
	}

	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}

	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	public Matrix4f getPerspectiveMatrix() {
		return this.perspectiveMatrix;
	}

	public Matrix4f getOrthographicMatrix() {
		return this.orthographicMatrix;
	}

	public Matrix4f getPerspectiveViewMatrix() {
		return this.perspectiveViewMatrix;
	}

}

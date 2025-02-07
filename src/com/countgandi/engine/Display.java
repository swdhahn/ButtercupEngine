package com.countgandi.engine;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

import com.countgandi.engine.shaders.ShaderProgram;

public class Display {

	public static Display DISPLAY;
	private static final long startTimeNano = System.nanoTime();

	private static long lastFrameTime;
	private static double delta;
	private static double prev = 0;

	private int width, height;
	private long window;
	private String title;
	public int FPS = 0;

	public Display(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		create(0);
	}

	public Display(int width, int height, String title, int samples) {
		this.width = width;
		this.height = height;
		this.title = title;
		create(samples);
	}

	private void create(int samples) {
		GLFW.glfwInit();

		if (samples > 0) {
			GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, samples);
		}
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		window = GLFW.glfwCreateWindow(width, height, title, 0, 0);

		GLFW.glfwMakeContextCurrent(window);

		GL.createCapabilities();

		IntBuffer bufferedWidth = BufferUtils.createIntBuffer(1);
		IntBuffer bufferedHeight = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(window, bufferedWidth, bufferedHeight);
		this.width = bufferedWidth.get(0);
		this.height = bufferedHeight.get(0);

		GL11.glViewport(0, 0, width, height);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL45.GL_MULTISAMPLE);
		ShaderProgram.bootUpData();

		DISPLAY = this;
	}

	public void showCursor() {
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
	}

	public void hideCursor() {
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}

	public void turnOnVysync() {
		GLFW.glfwSwapInterval(1);
	}

	public void turnOffVysync() {
		GLFW.glfwSwapInterval(0);
	}

	private int frames = 0;
	private long startTime = getCurrentTime();

	public void update() {
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(window);

		long currentFrameTime = getCurrentTime();

		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		frames++;
		if (delta - prev > 1) {
			prev = delta;
		}
		if (startTime + 1000 < getCurrentTime()) {
			FPS = frames;
			frames = 0;
			System.out.println("FPS: " + FPS);
			GLFW.glfwSetWindowTitle(window, title + " - Fps: " + FPS);
			startTime = getCurrentTime();
		}
	}

	public static double getDelta() {
		return delta;
	}

	public static long getCurrentTime() {
		return -(startTimeNano - System.nanoTime()) / 1000000;
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(window);
	}

	public void destroy() {
		GLFW.glfwDestroyWindow(window);
	}

	public long getWindow() {
		return window;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setTitle(String title) {
		this.title = title;
		GLFW.glfwSetWindowTitle(window, this.title);
	}
}

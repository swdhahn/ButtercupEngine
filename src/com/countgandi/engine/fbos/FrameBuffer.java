package com.countgandi.engine.fbos;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;

import com.countgandi.engine.Display;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.shaders.ShaderProgram;
import com.countgandi.game.Main;

public abstract class FrameBuffer {

	public static Model quad;

	private int[] textures, buffersForDrawing;
	private int fbo, rbo, textureCount;
	protected int width, height;
	protected ShaderProgram shader;

	private boolean multisample = false;

	public FrameBuffer(int width, int height, boolean hasDepthAttatchment, int textureCount, int[] textureTypes,
			int samples, ShaderProgram shader) {
		this.width = width;
		this.height = height;
		this.shader = shader;
		this.textureCount = textureCount;
		this.multisample = true;
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

		textures = new int[textureCount];
		buffersForDrawing = new int[textureCount];
		for (int i = 0; i < textureCount; i++) {
			buffersForDrawing[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
			textures[i] = GL45.glGenRenderbuffers();
			createMultisampleColorAttachment(i, textureTypes, samples);
		}
		if (hasDepthAttatchment) {
			createDepthBuffer(samples);
		}
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Framebuffer not complete!");
		}
		unbind();
	}

	public FrameBuffer(int width, int height, boolean hasDepthAttatchment, int textureCount, int[] textureTypes,
			ShaderProgram shader) {
		this.width = width;
		this.height = height;
		this.shader = shader;
		this.textureCount = textureCount;
		this.multisample = false;
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

		textures = new int[textureCount];
		buffersForDrawing = new int[textureCount];
		for (int i = 0; i < textureCount; i++) {
			buffersForDrawing[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
			textures[i] = GL11.glGenTextures();
			createColorAttachment(i, textureTypes);
		}
		if (hasDepthAttatchment) {
			createDepthBuffer(0);
		}
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Framebuffer not complete!");
		}
		unbind();
	}

	private void createColorAttachment(int index, int[] textureTypes) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[index]);
		GL45.glTexImage2D(GL11.GL_TEXTURE_2D, 0, textureTypes[index], width, height, 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, buffersForDrawing[index], GL11.GL_TEXTURE_2D, textures[index],
				0);
	}

	private void createMultisampleColorAttachment(int index, int[] textureTypes, int samples) {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, textures[index]);

		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, textureTypes[index], width, height);

		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, buffersForDrawing[index], GL30.GL_RENDERBUFFER,
				textures[index]);
	}

	private void createDepthBuffer(int samples) {
		rbo = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
		if (!multisample) {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
		} else {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL30.GL_DEPTH24_STENCIL8, width,
					height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
				rbo);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
	}

	public void resolveToFbo(FrameBuffer frameBufferToBeDrawnTo) {
		GL45.glBindFramebuffer(GL45.GL_DRAW_FRAMEBUFFER, frameBufferToBeDrawnTo.fbo);
		GL45.glBindFramebuffer(GL45.GL_READ_FRAMEBUFFER, fbo);
		for (int i = 0; i < textureCount; i++) {
			GL45.glReadBuffer(buffersForDrawing[i]);
			GL45.glDrawBuffer(buffersForDrawing[i]);
			GL45.glBlitFramebuffer(0, 0, width, height, 0, 0, frameBufferToBeDrawnTo.width,
					frameBufferToBeDrawnTo.height, GL45.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		}
		GL45.glBlitFramebuffer(0, 0, width, height, 0, 0, frameBufferToBeDrawnTo.width, frameBufferToBeDrawnTo.height,
				GL45.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
	}

	public void resolveToScreen(Display display) {
		GL45.glBindFramebuffer(GL45.GL_DRAW_FRAMEBUFFER, 0);
		GL45.glBindFramebuffer(GL45.GL_READ_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL45.glBlitFramebuffer(0, 0, width, height, 0, 0, (int) display.getWidth(), (int) display.getHeight(),
				GL45.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	}

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glViewport(0, 0, width, height);
		GL45.glDrawBuffers(this.buffersForDrawing);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public static void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Main.WIDTH, Main.HEIGHT);
	}

	public void render(FrameBuffer previousBuffer) {
		GL46.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
		GL45.glDisable(GL45.GL_DEPTH_TEST);
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		for (int i = 0; i < previousBuffer.getTextureCount(); i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousBuffer.getTexture(i));
		}

		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
		GL45.glEnable(GL45.GL_DEPTH_TEST);
	}

	public void cleanUp() {
		if (multisample) {
			GL45.glDeleteRenderbuffers(textures);
		} else {
			GL11.glDeleteTextures(textures);
		}
		GL30.glDeleteFramebuffers(fbo);
		GL30.glDeleteRenderbuffers(rbo);
	}

	public int getTexture(int index) {
		return textures[index];
	}

	public int getRbo() {
		return rbo;
	}

	public int getTextureCount() {
		return this.textureCount;
	}

	public ShaderProgram getShaderProgram() {
		return shader;
	}

}

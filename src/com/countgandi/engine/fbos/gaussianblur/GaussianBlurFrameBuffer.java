package com.countgandi.engine.fbos.gaussianblur;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;
import org.lwjgl.util.vector.Vector2f;

import com.countgandi.engine.fbos.FrameBuffer;

public class GaussianBlurFrameBuffer extends FrameBuffer {

	public GaussianBlurFrameBuffer(int width, int height) {
		super(width, height, false, 1, new int[] {GL45.GL_RGBA}, new GaussianBlurShader());
	}
	
	@Override
	public void render(FrameBuffer previousBuffer) {
		GL46.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
		GL45.glDisable(GL45.GL_DEPTH_TEST);
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		for(int i = 0; i < previousBuffer.getTextureCount(); i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousBuffer.getTexture(i));
		}
		((GaussianBlurShader)shader).update(new Vector2f(1, 0), width, height);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		((GaussianBlurShader)shader).update(new Vector2f(0, 1), width, height);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
		GL45.glEnable(GL45.GL_DEPTH_TEST);
	}

}

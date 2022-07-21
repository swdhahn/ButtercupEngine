package com.countgandi.engine.fbos.deferred;

import org.lwjgl.opengl.GL45;

import com.countgandi.engine.fbos.FrameBuffer;

public class DeferredFrameBuffer extends FrameBuffer {

	public DeferredFrameBuffer(int width, int height) {
		super(width, height, false, 1, new int[] {GL45.GL_RGBA}, new DeferredShader());
	}

}

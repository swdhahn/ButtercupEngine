package com.countgandi.engine.fbos.gaussianblur;

import org.lwjgl.util.vector.Vector2f;

import com.countgandi.engine.shaders.ShaderProgram;

public class GaussianBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/countgandi/engine/fbos/gaussianblur/vertex.glsl";
	private static final String FRAGMENT_FILE = "src/com/countgandi/engine/fbos/gaussianblur/fragment.glsl";
	
	private int location_tex;
	private int location_imgWidthHeight;
	private int location_mask;
	
	public GaussianBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		super.loadInt(location_tex, 0);
		stop();
	}

	@Override
	protected void getAllUniformLocations() {
		location_tex = super.getUniformLocation("tex");
		location_imgWidthHeight = super.getUniformLocation("imgWidthHeight");
		location_mask = super.getUniformLocation("mask");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "positions");
	}
	
	public void update(Vector2f mask, float imageWidth, float imageHeight) {
		super.load2DVector(location_mask, mask);
		super.loadFloat(location_imgWidthHeight, imageWidth * mask.x + imageHeight * mask.y);
	}

}

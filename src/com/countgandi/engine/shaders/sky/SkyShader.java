package com.countgandi.engine.shaders.sky;

import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.shaders.ShaderProgram;

public class SkyShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/countgandi/engine/shaders/sky/vertex.glsl";
	private static final String FRAGMENT_FILE = "src/com/countgandi/engine/shaders/sky/fragment.glsl";

	private int location_skyColor;

	public SkyShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "positions");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_skyColor = super.getUniformLocation("skyColor");
		
		super.getUniformBufferLocation("CameraData", 0);
	}
	
	public void loadSkyColor(Vector3f skyColor) {
		super.loadVector(location_skyColor, skyColor);
	}

}

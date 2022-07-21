package com.countgandi.engine.shaders.shadow;

import org.lwjgl.util.vector.Matrix4f;

import com.countgandi.engine.shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/com/countgandi/engine/shaders/shadow/vertex.glsl";
	private static final String FRAGMENT_FILE = "src/com/countgandi/engine/shaders/shadow/fragment.glsl";

	private int location_projectionViewMatrix;
	private int location_transformationMatrix;
	private int location_texture;
	
	public ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		super.loadInt(location_texture, 0);
		stop();
	}

	@Override
	protected void getAllUniformLocations() {
		super.bindAttribute(location_projectionViewMatrix, "projectionViewMatrix");
		super.bindAttribute(location_transformationMatrix, "transformationMatrix");
		super.bindAttribute(location_texture, "modelTexture");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "positions");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadProjectionViewMatrix(Matrix4f projectionViewMatrix) {
		super.loadMatrix(location_projectionViewMatrix, projectionViewMatrix);
	}
	
	public void loadTransformationMatrix(Matrix4f transformationMatrix) {
		super.loadMatrix(location_transformationMatrix, transformationMatrix);
	}

}

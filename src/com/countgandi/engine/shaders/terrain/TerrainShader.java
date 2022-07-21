package com.countgandi.engine.shaders.terrain;

import org.lwjgl.util.vector.Matrix4f;

import com.countgandi.engine.shaders.ShaderProgram;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/countgandi/engine/shaders/terrain/vertex.glsl";
	private static final String TESS_CS_FILE = "src/com/countgandi/engine/shaders/terrain/tessCS.glsl";
	private static final String TESS_ES_FILE = "src/com/countgandi/engine/shaders/terrain/tessES.glsl";
	private static final String FRAGMENT_FILE = "src/com/countgandi/engine/shaders/terrain/fragment.glsl";

	private int location_transformationMatrix;
	private int location_shineDamper;
	private int location_reflectivity;
	
	private int location_diffuse0;
	private int location_heightMap;
	
	private int location_uv;
	private int location_flatNormals;
	
	
	public TerrainShader() {
		super(VERTEX_FILE, TESS_CS_FILE, TESS_ES_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		
		location_diffuse0 = super.getUniformLocation("diffuse0");
		location_heightMap = super.getUniformLocation("heightMap");
		
		location_uv = super.getUniformLocation("UV");
		location_flatNormals = super.getUniformLocation("flatNormals");
		
		super.getUniformBufferLocation("CameraData", 0);
		
	}

	public void loadTextures() {
		super.loadInt(location_diffuse0, 0);
		super.loadInt(location_heightMap, 1);
	}
	
	public void loadShineVariable(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f transformationMatrix) {
		super.loadMatrix(location_transformationMatrix, transformationMatrix);
	}

	public void loadUv(float uv) {
		super.loadFloat(location_uv, uv);
	}

	public void loadFlatNormals(boolean flatNormals) {
		super.loadBoolean(location_flatNormals, flatNormals);
	}

}

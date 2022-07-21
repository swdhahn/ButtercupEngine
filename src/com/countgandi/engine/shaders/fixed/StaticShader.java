package com.countgandi.engine.shaders.fixed;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.shaders.ShaderProgram;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/countgandi/engine/shaders/fixed/vertex.glsl";
	private static final String FRAGMENT_FILE = "src/com/countgandi/engine/shaders/fixed/fragment.glsl";

	private int location_transformationMatrix;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	
	private int location_diffuse;
	private int location_normal;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		super.loadInt(location_diffuse, 0);
		super.loadInt(location_normal, 1);
		stop();
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "materialIndex");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		
		location_diffuse = super.getUniformLocation("diffuseTex");
		location_normal = super.getUniformLocation("normalTex");
		
		super.getUniformBufferLocation("CameraData", 0);
		
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadVector(location_plane, plane);
	}
	
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void loadOffset(float x, float y) {
		super.load2DVector(location_offset, new Vector2f(x, y));
	}
	
	public void loadFakeLightingVariable(boolean useFakeLighting) {
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}
	
	public void loadShineVariable(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f transformationMatrix) {
		super.loadMatrix(location_transformationMatrix, transformationMatrix);
	}

}

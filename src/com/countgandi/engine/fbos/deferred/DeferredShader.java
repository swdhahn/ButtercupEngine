package com.countgandi.engine.fbos.deferred;

import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.Display;
import com.countgandi.engine.shaders.ShaderProgram;

public class DeferredShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/countgandi/engine/fbos/deferred/vertex.glsl";
	private static final String FRAGMENT_FILE = "src/com/countgandi/engine/fbos/deferred/fragment.glsl";
	
	private int location_diffuse;
	private int location_normals;
	private int location_position;
	
	private int location_fogColor;
	private int location_fogDensity;
	
	private int location_time;
	
	private float time;
	
	public DeferredShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		super.loadInt(location_diffuse, 0);
		super.loadInt(location_normals, 1);
		super.loadInt(location_position, 2);
		stop();
	}

	@Override
	protected void getAllUniformLocations() {
		location_diffuse = super.getUniformLocation("diffuseTexture");
		location_normals = super.getUniformLocation("normalsTexture");
		location_position = super.getUniformLocation("positionTexture");
		
		location_fogColor = super.getUniformLocation("fogColor");
		location_fogDensity = super.getUniformLocation("fogDensity");
		
		location_time = super.getUniformLocation("time");
		
		super.getUniformBufferLocation("CameraData", 0);
		super.getUniformBufferLocation("LightData", 1);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "positions");
	}
	
	public void updateShader(Vector3f fogColor, float density) {
		time += Display.getDelta();
		super.loadFloat(location_time, time);
		super.loadVector(location_fogColor, fogColor);
		super.loadFloat(location_fogDensity, density);
	}

}

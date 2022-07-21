package com.countgandi.engine.model;

import org.lwjgl.util.vector.Vector3f;

public class Material {

	private String name;

	private Vector3f ambientLight = new Vector3f(1.0f, 1.0f, 1.0f);
	
	private float shineDamper = 1;
	private float reflectivity = 0f;

	private boolean useFakeLighting = false;
	private boolean culling = true;

	private int numberOfRows = 1;

	public Material(String name) {
		this.name = name;
	}
	
	public int getNumberOfRows() {
		return numberOfRows;
	}

	public Material setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
		return this;
	}

	public boolean hasCulling() {
		return culling;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public Material setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
		return this;
	}

	public Material setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
		return this;
	}

	public Material setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
		return this;
	}

	public Material setCulling(boolean culling) {
		this.culling = culling;
		return this;
	}

	public Material setName(String name) {
		this.name = name;
		return this;
	}
	
	public Material setAmbientLight(Vector3f ambient) {
		this.ambientLight = ambient;
		return this;
	}
	
	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

}

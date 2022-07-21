package com.countgandi.engine.model;

import com.countgandi.engine.model.textures.Texture;

public class LandscapeMaterial {
	
	private Texture diffuse;
	private Texture heightMap;

	private float shineDamper = 1;
	private float reflectivity = 0f;


	public LandscapeMaterial(Texture diffuse) {
		this.diffuse = diffuse;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}
	
	public Texture getDiffuse() {
		return diffuse;
	}
	
	public Texture getHeightMap() {
		return heightMap;
	}
	
	public void setDiffuse(Texture diffuse) {
		this.diffuse = diffuse;
	}
	
	public void setHeightMap(Texture heightMap) {
		this.heightMap = heightMap;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

}

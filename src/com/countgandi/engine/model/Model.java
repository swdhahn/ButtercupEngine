package com.countgandi.engine.model;

import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.model.textures.Texture;

public class Model {
	//Model stored in memory
	
	private int vaoID;
	private int vertexCount;
	private float radius;
	private Vector3f centerPoint;
	private Material[] materials;
	private Texture diffuse;
	private Texture normal;
	private Texture ambientOcclusion;
	//private Texture bumpMa
	
	public Model(int vaoID, int vertexCount, Vector3f centerPoint, float radius) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.radius = radius;
		this.centerPoint = centerPoint;
	}
	
	public void setMaterial(int index, Material material) {
		this.materials[index] = material;
	}
	
	public void setMaterialsArray(Material[] materials) {
		this.materials = materials;
	}
	
	public Material[] getMaterials() {
		return materials;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Vector3f getCenterPoint() {
		return centerPoint;
	}

	public void setCenterPoint(Vector3f centerPoint) {
		this.centerPoint = centerPoint;
	}

	public Texture getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Texture diffuse) {
		this.diffuse = diffuse;
	}

	public Texture getNormal() {
		return normal;
	}

	public void setNormal(Texture normal) {
		this.normal = normal;
	}

	public Texture getAmbientOcclusion() {
		return ambientOcclusion;
	}

	public void setAmbientOcclusion(Texture ambientOcclusion) {
		this.ambientOcclusion = ambientOcclusion;
	}

}

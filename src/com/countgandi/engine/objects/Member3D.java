package com.countgandi.engine.objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.utils.Maths;

public abstract class Member3D {

	protected Vector3f position = new Vector3f(), rotation = new Vector3f();
	protected float scale = 1;
	
	private Matrix4f transformationMatrix = Maths.createTransformationMatrix(this.getPosition(), this.getRotation(), this.getScale());
	
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}
	
	public void updateTransformation() {
		transformationMatrix = this.createCustomTransformationMatrix();
	}
	
	protected Matrix4f createCustomTransformationMatrix() {
		return Maths.createTransformationMatrix(this.getPosition(), this.getRotation(), this.getScale());
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
}

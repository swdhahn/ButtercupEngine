package com.countgandi.engine.objects;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	public static final int LIGHT_DIRECTIONAL = 0;
	public static final int LIGHT_LAMP = 1;
	public static final int LIGHT_OTHER = 2;

	private Vector3f position;
	private Vector3f color;
	private Vector3f attenuation = new Vector3f(1, 0, 0);
	private float radius = 1, brightness = 1, minLight = 0.3f;
	private int lightType;

	public Light(Vector3f position, Vector3f color, int lightType) {
		this.position = position;
		this.color = color;
		this.lightType = lightType;
		if (lightType == LIGHT_DIRECTIONAL) {
			attenuation = new Vector3f(1, 0, 0);
		} else if (lightType == LIGHT_LAMP) {
			attenuation = new Vector3f(0.5f, 0.2f, 0.05f);
		}
		float lightMax = color.x;
		lightMax = (color.x < color.y) ? color.y : color.x;
		lightMax = (color.x < color.z) ? color.z : color.x;

		radius = (float) ((-attenuation.y + Math.sqrt(attenuation.y * attenuation.y - 4 * attenuation.z * (attenuation.x - (256.0 / 5.0) * lightMax))) / (2 * attenuation.z));
	}

	public Light(Vector3f position, Vector3f color, Vector3f attenuation, int lightType) {
		this(position, color, lightType);
		this.attenuation = attenuation;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRadius() {
		return radius;
	}

	public float getBrightness() {
		return brightness;
	}

	public float getMinLight() {
		return minLight;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

	public void setMinLight(float minLight) {
		this.minLight = minLight;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setAttenuation(Vector3f attenuation) {
		this.attenuation = attenuation;
	}

	public String toString() {
		if (lightType == LIGHT_DIRECTIONAL) {
			return "Directional Light";
		} else if (lightType == LIGHT_LAMP) {
			return "Lamp";
		} else if (lightType == LIGHT_OTHER) {
			return "none";
		}
		return "none";
	}

	public void move(Vector3f pos) {
		position.x += pos.x;
		position.y += pos.y;
		position.z += pos.z;
	}

	public int getType() {
		return lightType;
	}
}

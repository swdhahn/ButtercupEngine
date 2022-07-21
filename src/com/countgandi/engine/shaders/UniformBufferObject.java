package com.countgandi.engine.shaders;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

import com.countgandi.engine.Loader;

/**
 * 
 * @author Count
 *
 *THIS MUST BE ADDED TO UBO SHADERPROGRAM ARRAY BEFORE ANY SHADERS ARE CREATED
 *
 */

public class UniformBufferObject {
	private static ArrayList<Integer> ubos = new ArrayList<Integer>();
	private int ubo = 0;
	
	private int index;
	
	public UniformBufferObject(int indexx, int size) {
		this.index = indexx + 1;
		ubo = GL45.glGenBuffers();
		GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, ubo);
		FloatBuffer buffer = Loader.storeDataInFloatBuffer(new float[size]);
		GL45.glBufferData(GL45.GL_UNIFORM_BUFFER, buffer, GL45.GL_DYNAMIC_DRAW);
		GL45.glBindBufferBase(GL45.GL_UNIFORM_BUFFER, index, ubo);
		GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, 0);
		ubos.add(ubo);
	}
	
	public void update(FloatBuffer buffer) {
		GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, ubo);
		GL15.glBufferData(GL45.GL_UNIFORM_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
		GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, 0);
	}
	
	public void update(int offset, FloatBuffer buffer) {
		GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, ubo);
		GL15.glBufferSubData(GL45.GL_UNIFORM_BUFFER, offset, buffer);
		GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, 0);
	}
	
	public int getIndex() {
		return index;
	}
	
	public static void cleanUp() {
		for(int i : ubos) {
			GL45.glDeleteBuffers(i);
		}
	}
	
}

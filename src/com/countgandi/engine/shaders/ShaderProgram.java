package com.countgandi.engine.shaders;

import java.awt.datatransfer.SystemFlavorMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.io.File;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL45;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public abstract class ShaderProgram {

	public static UniformBufferObject[] ubos;

	private int programID;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(String vertexFile, String fragmentFile) {
		int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		bindAttributes();

		GL20.glLinkProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);

		getAllUniformLocations();
		GL20.glValidateProgram(programID);

	}

	public ShaderProgram(String vertexFile, String geometryFile, String fragmentFile) {
		int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int geometryShaderID = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, geometryShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		bindAttributes();

		GL20.glLinkProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);

		getAllUniformLocations();
		GL20.glValidateProgram(programID);
	}

	public ShaderProgram(String vertexFile, String tessellationControlFile, String tessellationEvaluationFile,
			String fragmentFile) {
		int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int tessellationControlShaderID = loadShader(tessellationControlFile, GL45.GL_TESS_CONTROL_SHADER);
		int tessellationEvaluationShaderID = loadShader(tessellationEvaluationFile,
				GL45.GL_TESS_EVALUATION_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, tessellationControlShaderID);
		GL20.glAttachShader(programID, tessellationEvaluationShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		bindAttributes();

		GL20.glLinkProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, tessellationControlShaderID);
		GL20.glDetachShader(programID, tessellationEvaluationShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(tessellationControlShaderID);
		GL20.glDeleteShader(tessellationEvaluationShaderID);
		GL20.glDeleteShader(fragmentShaderID);

		getAllUniformLocations();
		GL20.glValidateProgram(programID);
	}

	public ShaderProgram(String vertexFile, String tessellationControlFile, String tessellationEvaluationFile,
			String geometryFile, String fragmentFile) {
		int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int tessellationControlShaderID = loadShader(tessellationControlFile, GL45.GL_TESS_CONTROL_SHADER);
		int tessellationEvaluationShaderID = loadShader(tessellationEvaluationFile,
				GL45.GL_TESS_EVALUATION_SHADER);
		int geometryShaderID = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, tessellationControlShaderID);
		GL20.glAttachShader(programID, tessellationEvaluationShaderID);
		GL20.glAttachShader(programID, geometryShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		bindAttributes();

		GL20.glLinkProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, tessellationControlShaderID);
		GL20.glDetachShader(programID, tessellationEvaluationShaderID);
		GL20.glDetachShader(programID, geometryShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);

		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(tessellationControlShaderID);
		GL20.glDeleteShader(tessellationEvaluationShaderID);
		GL20.glDeleteShader(geometryShaderID);
		GL20.glDeleteShader(fragmentShaderID);

		getAllUniformLocations();
		GL20.glValidateProgram(programID);
	}

	protected abstract void getAllUniformLocations();

	protected int getUniformLocation(String uniformName) {
		int loc = GL20.glGetUniformLocation(programID, uniformName);
		if (loc == GL11.GL_INVALID_VALUE) {
			System.err.println("Uniform variable \"" + uniformName
					+ "\" has an invalid value, can't be created by OpenGL.");
		} else if (loc == GL11.GL_INVALID_OPERATION) {
			System.err.println(
					"The program entered is not a valid, usable program, or it has not been linked successfully.");
		} else if (loc == -1) {
			System.err.println("Uniform location \"" + uniformName + "\" is not used.");
		}
		return loc;
	}

	protected void getUniformBufferLocation(String uniformBufferName, int blockBinding) {
		int uniformIndex = GL45.glGetUniformBlockIndex(programID, uniformBufferName);
		if (uniformIndex == GL11.GL_INVALID_VALUE) {
			System.err.println("Uniform buffer object \"" + uniformBufferName
					+ "\" has an invalid value, can't be created by OpenGL.");
		} else if (uniformIndex == GL11.GL_INVALID_OPERATION) {
			System.err.println(
					"The program entered is not a valid, usable program, or it has not been linked successfully.");
		} else if (uniformIndex == -1) {
			System.err.println("Uniform Buffer location \"" + uniformBufferName + "\" is not used.");
		}
		GL45.glUniformBlockBinding(programID, uniformIndex, ShaderProgram.ubos[blockBinding].getIndex());
	}

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void load2DVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		UniformBufferObject.cleanUp();
		GL20.glDeleteProgram(programID);
	}

	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	protected void bindUniformBlockAttribute() {
		GL45.glGetUniformBlockIndex(programID, "name");
	}

	protected abstract void bindAttributes();

	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			InputStream in = ShaderProgram.class.getResourceAsStream(file.substring(3));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader!");
			System.exit(-1);
		}
		return shaderID;
	}

	public static void bootUpData() {
		int[] uboMax = new int[1];
		GL45.glGetIntegerv(GL45.GL_MAX_UNIFORM_BUFFER_BINDINGS, uboMax);
		System.out.println("Max Uniform Buffer Bindings: " + uboMax[0]);
		ubos = new UniformBufferObject[uboMax[0]];
	}

}

package com.countgandi.engine.objects;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.Loader;
import com.countgandi.engine.model.LandscapeMaterial;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.utils.Maths;
import com.countgandi.engine.utils.PerlinNoise;

public class Terrain extends Member3D {

	/*
	 * Needs to be changed before any Terrain are created...
	 */
	public static int SIZE = 200;
	private LandscapeMaterial material;
	private float[][] heights;
	public Model model;
	private PerlinNoise noise;
	public boolean flatNormals = false;

	public Terrain(LandscapeMaterial material, Loader loader) {
		this.material = material;
		model = this.generateTerrain(SIZE / 100, SIZE / 100, loader);
	}
	
	public Terrain(Vector3f position, LandscapeMaterial material, Loader loader) {
		this.material = material;
		this.position = position;
		model = this.generateTerrain(SIZE / 100, SIZE / 100, loader);
		super.updateTransformation();
	}
	
	public Terrain(Vector3f position, Vector3f rotation, LandscapeMaterial material, Loader loader) {
		this.material = material;
		this.position = position;
		this.rotation = rotation;
		model = this.generateTerrain(SIZE / 100, SIZE / 100, loader);
		super.updateTransformation();
	}
	
	public void tick() {
		
	}
	
	public LandscapeMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(LandscapeMaterial material) {
		this.material = material;
	}
	
	protected float getHeight(float x, float z) {
		return noise.generateHeight(x, z);
	}
	
	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - position.x * SIZE;
		float terrainZ = worldZ - position.z * SIZE;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX + 1 >= heights.length || gridZ + 1 >= heights.length || gridX < 0 || gridZ < 0) {
			return 0;
		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;

		if (xCoord <= (1 - zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}
	
	protected Vector3f calcNormal(int x, int z) {
		float heightL = getHeight(x - 1, z);
		float heightR = getHeight(x + 1, z);
		float heightD = getHeight(x, z - 1);
		float heightU = getHeight(x, z + 1);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
	@SuppressWarnings("unused")
	private Model[] generateLODTerrain(int maxVertexCount, int levelCount, Loader loader) {
		Model[] ts = new Model[levelCount];
		for(int i = 0; i < ts.length; i++) {
			ts[i] = generateTerrain(maxVertexCount / (i + 1), maxVertexCount, null);
		}
		return ts;
	}
	
	private Model generateTerrain(int VERTEX_COUNT, int NoiseVertexCount, Loader loader) {
		noise = new PerlinNoise(position.x, position.z, NoiseVertexCount, 50f, 6, 0.35f, 0);
		
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];

		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = ((float) j / ((float) VERTEX_COUNT - 1) - 0.5f) * SIZE;
				float height = getHeight(j, i);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = ((float) i / ((float) VERTEX_COUNT - 1) - 0.5f) * SIZE;
				Vector3f normal = calcNormal(j, i);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT + 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
}

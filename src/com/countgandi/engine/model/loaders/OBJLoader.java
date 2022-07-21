package com.countgandi.engine.model.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.Loader;
import com.countgandi.engine.model.Material;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.model.textures.Texture;

public class OBJLoader {

	public static Model loadObjModel(String objFileName, Loader loader) {
		if (!objFileName.endsWith(".obj")) {
			objFileName += ".obj";
		}
		if (!objFileName.startsWith("/")) {
			objFileName = "/" + objFileName;
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(OBJLoader.class.getResourceAsStream(objFileName)));
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices, "");
				processVertex(vertex2, vertices, indices, "");
				processVertex(vertex3, vertices, indices, "");
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		Vector4f furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray,
				1);
		int[] indicesArray = convertIndicesListToArray(indices);
		Model model = loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
		model.setCenterPoint(new Vector3f(furthest.x, furthest.y, furthest.z));
		model.setRadius(furthest.w);
		return model;
	}

	public static Model loadObjModel(String objFileName, float scale, Loader loader) {
		if (!objFileName.endsWith(".obj")) {
			objFileName += ".obj";
		}
		if (!objFileName.startsWith("/")) {
			objFileName = "/" + objFileName;
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(OBJLoader.class.getResourceAsStream(objFileName)));
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices, "");
				processVertex(vertex2, vertices, indices, "");
				processVertex(vertex3, vertices, indices, "");
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		Vector4f furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray,
				scale);
		int[] indicesArray = convertIndicesListToArray(indices);
		Model model = loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
		model.setCenterPoint(new Vector3f(furthest.x, furthest.y, furthest.z));
		model.setRadius(furthest.w);
		return model;
	}

	public static Model loadObjModelWithMaterials(String objFileName, Loader loader) {
		if (!objFileName.endsWith(".obj")) {
			objFileName += ".obj";
		}
		if (!objFileName.startsWith("/")) {
			objFileName = "/" + objFileName;
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(OBJLoader.class.getResourceAsStream(objFileName)));
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		ArrayList<String> materials = new ArrayList<String>();
		String materialFileName = "none";
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if(line.startsWith("mtllib ")) {
					materialFileName = line.split(" ")[1];
				} if (line.startsWith("usemtl ")) {
					break;
				}
			}
			String material = "none";
			while (line != null) {
				if(line.startsWith("usemtl ")) {
					material = line.split(" ")[1];
					materials.add(material);
				}
				if(line.startsWith("f ")) {
					String[] currentLine = line.split(" ");
					String[] vertex1 = currentLine[1].split("/");
					String[] vertex2 = currentLine[2].split("/");
					String[] vertex3 = currentLine[3].split("/");
					processVertex(vertex1, vertices, indices, material);
					processVertex(vertex2, vertices, indices, material);
					processVertex(vertex3, vertices, indices, material);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		int[] materialsArray = new int[vertices.size()]; // Material array for vbo
		Vector4f furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray,
				1);
		
		Texture[] diffuseTexture = new Texture[1], normalTexture  = new Texture[1];
		Material[] newMaterialArray = loadMaterials(materialFileName, vertices, materials, materialsArray, diffuseTexture, normalTexture);
		int[] indicesArray = convertIndicesListToArray(indices);
		Model model = loader.loadToVAO(verticesArray, texturesArray, normalsArray, materialsArray, indicesArray);
		model.setCenterPoint(new Vector3f(furthest.x, furthest.y, furthest.z));
		model.setRadius(furthest.w);
		model.setMaterialsArray(newMaterialArray);
		model.setDiffuse(diffuseTexture[0]);
		model.setNormal(normalTexture[0]);
		return model;
	}
	
	private static Material[] loadMaterials(String materialFileName, List<Vertex> vertices, ArrayList<String> materials, int[] materialsArray, Texture[] diffuse, Texture[] normal) {
		Material[] newMaterials = new Material[materials.size()];
		String[] diffuseArray = new String[materials.size()];
		String[] normalArray = new String[materials.size()];
		ArrayList<Material> alMaterials = MtlLoader.loadMaterial(materialFileName, diffuseArray, normalArray);
		int j = 0;
		for(Material m: alMaterials) {
			if(materials.contains(m.getName())) {
				newMaterials[j] = m;
				j++;
			}
		}
		for(int i = 0; i < vertices.size(); i++) {
			materialsArray[i] = materials.indexOf(vertices.get(i).getMaterial());
		}
		for(int i = 0; i < diffuseArray.length; i++) {
			System.out.println(diffuseArray[i]);
		}
		diffuse[0] = Texture.create().anisotropic().normalMipMap().createArray(diffuseArray);
		normal[0] = Texture.create().anisotropic().normalMipMap().createArray(normalArray);
		return newMaterials;
	}

	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices, String material) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		currentVertex.setMaterial(material);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
			List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static Vector4f convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
			float[] verticesArray, float[] texturesArray, float[] normalsArray, float scale) {
		Vector3f maxPosition = new Vector3f(), minPosition = new Vector3f();
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			currentVertex.position.x *= scale;
			currentVertex.position.y *= scale;
			currentVertex.position.z *= scale;

			if (currentVertex.getLength() > maxPosition.length()) {
				maxPosition = currentVertex.getPosition();
			}

			if (currentVertex.getLength() < minPosition.length()) {
				minPosition = currentVertex.getPosition();
			}

			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}

		Vector3f centerPoint = new Vector3f((maxPosition.x + minPosition.x) / 2.0f,
				(maxPosition.y + minPosition.y) / 2.0f, (maxPosition.z + minPosition.z) / 2.0f);
		return new Vector4f(centerPoint.x, centerPoint.y, centerPoint.z, centerPoint.length());
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

	private static class Vertex {

		private static final int NO_INDEX = -1;

		private Vector3f position;
		private String material;
		private int textureIndex = NO_INDEX;
		private int normalIndex = NO_INDEX;
		private Vertex duplicateVertex = null;
		private int index;
		private float length;

		public Vertex(int index, Vector3f position) {
			this.index = index;
			this.position = position;
			this.length = position.length();
		}

		public void setMaterial(String material) {
			this.material = material;
		}

		public int getIndex() {
			return index;
		}

		public float getLength() {
			return length;
		}
		
		public String getMaterial() {
			return material;
		}

		public boolean isSet() {
			return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
		}

		public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
			return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
		}

		public void setTextureIndex(int textureIndex) {
			this.textureIndex = textureIndex;
		}

		public void setNormalIndex(int normalIndex) {
			this.normalIndex = normalIndex;
		}

		public Vector3f getPosition() {
			return position;
		}

		public int getTextureIndex() {
			return textureIndex;
		}

		public int getNormalIndex() {
			return normalIndex;
		}

		public Vertex getDuplicateVertex() {
			return duplicateVertex;
		}

		public void setDuplicateVertex(Vertex duplicateVertex) {
			this.duplicateVertex = duplicateVertex;
		}
	}
}

package com.countgandi.engine.model.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.countgandi.engine.model.Material;

public class MtlLoader {

	public static ArrayList<Material> loadMaterial(String fileName, String[] diffuseArray, String[] normalArray) {
		ArrayList<Material> materials = new ArrayList<Material>();
		if (!fileName.startsWith("/")) {
			fileName = "/" + fileName;
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(OBJLoader.class.getResourceAsStream(fileName)));
		String line;
		try {
			int materialIndex = -1;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("newmtl ")) {
					materials.add(new Material(line.split(" ")[1]));
					materialIndex ++;
				} else if(line.startsWith("Ka ")) {
					String[] lines = line.split(" ");
					materials.get(materialIndex).setAmbientLight(new Vector3f(Float.parseFloat(lines[1]), Float.parseFloat(lines[2]), Float.parseFloat(lines[3])));
				} else if (line.startsWith("map_Kd ")) {
					String[] s = line.split(" ");
					diffuseArray[materialIndex] = s[s.length - 1];
				} else if (line.startsWith("norm ")) {
					String[] s = line.split(" ");
					normalArray[materialIndex] = s[s.length - 1];
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		
		for(Material m1: materials) {
			for(Material m2: materials) {
				if(!m1.equals(m2) && m1.getName().equals(m2.getName())) {
					materials.remove(m2);
				}
			}
		}
		
		return materials;
	}
	
}

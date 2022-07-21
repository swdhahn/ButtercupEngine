package com.countgandi.engine.renderers;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL45;

import com.countgandi.engine.model.LandscapeMaterial;
import com.countgandi.engine.objects.Terrain;
import com.countgandi.engine.shaders.terrain.TerrainShader;

public class TerrainRenderer {

	private TerrainShader shader;

	public TerrainRenderer(TerrainShader shader) {
		this.shader = shader;
		shader.start();
		shader.loadTextures();
		shader.stop();
	}

	public void render(Terrain terrain) {
		shader.loadUv(Terrain.SIZE);
		prepareTexturedModel(terrain);
		prepareInstance(terrain);
		GL45.glPatchParameteri(GL45.GL_PATCH_VERTICES, 3);
		GL11.glDrawElements(GL45.GL_PATCHES, terrain.model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		unbindTexturedModel();
	}

	public void render(ArrayList<Terrain> terrains) {
		shader.loadUv(Terrain.SIZE);
		for (Terrain terrain : terrains) {
			prepareTexturedModel(terrain);
			prepareInstance(terrain);
			GL45.glPatchParameteri(GL45.GL_PATCH_VERTICES, 3);
			GL11.glDrawElements(GL45.GL_PATCHES, terrain.model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		unbindTexturedModel();

	}

	private void prepareTexturedModel(Terrain t) {
		GL30.glBindVertexArray(t.model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		LandscapeMaterial material = t.getMaterial();

		GL40.glEnable(GL40.GL_CULL_FACE);

		shader.loadFlatNormals(t.flatNormals);
		shader.loadShineVariable(material.getShineDamper(), material.getReflectivity());

		material.getDiffuse().bindToUnit(0);
		material.getHeightMap().bindToUnit(1);
	}

	private void unbindTexturedModel() {
		GL40.glDisable(GL40.GL_CULL_FACE);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Terrain t) {
		shader.loadTransformationMatrix(t.getTransformationMatrix());
		// shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}

}

package com.countgandi.engine.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.util.vector.Vector4f;

import com.countgandi.engine.model.Material;
import com.countgandi.engine.model.Model;
import com.countgandi.engine.objects.Entity;
import com.countgandi.engine.shaders.fixed.StaticShader;
import com.countgandi.engine.utils.Maths;

public class EntityRenderer {
	
	private StaticShader shader;
	
	
	public EntityRenderer(StaticShader shader) {
		this.shader = shader;
	}

	public void render(HashMap<Model, ArrayList<Entity>> entities, Vector4f[] frustum) {
		for(Model model:entities.keySet()) {
			GL30.glBindVertexArray(model.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			model.getDiffuse().bindToUnit(0);
			model.getNormal().bindToUnit(1);
			List<Entity> batch = entities.get(model);
			prepareMaterial(model.getMaterials()[0]);
			for(Entity entity:batch) {
				if(!Maths.sphereFrustumIntersection(entity, model, frustum)) 
					continue;
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	public void render(Model model, Entity entity, Vector4f[] frustum) {
		if(!Maths.sphereFrustumIntersection(entity, model, frustum)) 
			return;
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		model.getDiffuse().bindToUnit(0);
		model.getNormal().bindToUnit(1);
		for(int i = 0; i < model.getMaterials().length; i++) {
			GL30.glBindVertexArray(model.getVaoID());
			prepareMaterial(model.getMaterials()[i]);
			prepareInstance(entity);
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		unbindTexturedModel();
	}
	
	public void render(Model model, ArrayList<Entity> batch, Vector4f[] frustum) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		model.getDiffuse().bindToUnit(0);
		for(int i = 0; i < model.getMaterials().length; i++) {
			for(Entity entity:batch) {
				if(!Maths.sphereFrustumIntersection(entity, model, frustum)) 
					continue;
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
		}
		unbindTexturedModel();
	}
	
	private void prepareMaterial(Material material) {
		shader.loadNumberOfRows(material.getNumberOfRows());
		
		if(material.hasCulling()) {
			GL40.glEnable(GL40.GL_CULL_FACE);
		}
		
		shader.loadFakeLightingVariable(material.isUseFakeLighting());
		shader.loadShineVariable(material.getShineDamper(), material.getReflectivity());
	}
	
	private void unbindTexturedModel() {
		GL40.glDisable(GL40.GL_CULL_FACE);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		shader.loadTransformationMatrix(entity.getTransformationMatrix());
		//shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
}

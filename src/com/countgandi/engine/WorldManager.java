package com.countgandi.engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.countgandi.engine.model.Model;
import com.countgandi.engine.objects.Entity;

public class WorldManager {
	
	public HashMap<Model, ArrayList<Entity>> entities = new HashMap<Model, ArrayList<Entity>>();
	
	public void addEntity(Entity e) {
		ArrayList<Entity> batch = entities.get(e.getModel());
		if (batch != null) {
			batch.add(e);
		} else {
			ArrayList<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(e);
			entities.put(e.getModel(), newBatch);
		}
	}
	
	public void removeEntity(Entity e) {
		entities.get(e.getModel()).remove(e);
	}

}

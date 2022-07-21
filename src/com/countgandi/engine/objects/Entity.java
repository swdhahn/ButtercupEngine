package com.countgandi.engine.objects;

import com.countgandi.engine.model.Model;

public abstract class Entity extends Member3D {
	
	protected Model model;
	
	public Entity(Model model) {
		this.model = model;
	}
	
	public void update() {
		tick();
		super.updateTransformation();
	}
	
	protected abstract void tick();
	
	public Model getModel() {
		return model;
	}
}

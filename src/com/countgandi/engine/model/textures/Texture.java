package com.countgandi.engine.model.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Texture {

	public static final Texture DEFAULT_TEXTURE = Texture.create().anisotropic()
			.nearestFiltering().normalMipMap().create("/com/countgandi/engine/model/textures/default.png");
	
	public final int textureId;
	public final int size;
	private final int type;

	protected Texture(int textureId, int type, int size) {
		this.textureId = textureId;
		this.size = size;
		this.type = type;
	}

	public void bindToUnit(int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(type, textureId);
	}

	public void delete() {
		GL11.glDeleteTextures(textureId);
	}

	public static TextureBuilder create() {
		return new TextureBuilder();
	}

}

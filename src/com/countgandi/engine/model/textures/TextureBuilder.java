package com.countgandi.engine.model.textures;

import org.lwjgl.opengl.GL45;

public class TextureBuilder {
	
	private boolean clampEdges = false;
	private boolean mipmap = false;
	private boolean anisotropic = true;
	private boolean nearest = false;
	
	public Texture create(String fileName){
		TextureData textureData = TextureUtils.loadTextureFile(fileName);
		int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
		return new Texture(textureId, GL45.GL_TEXTURE_2D, textureData.getWidth());
	}
	
	public Texture createAtlas(String[] fileNames) {
		TextureData textureData = TextureAtlasser.atlasTextures(fileNames);
		int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
		return new Texture(textureId, GL45.GL_TEXTURE_2D, textureData.getWidth());
	}
	
	public Texture createArray(String[] fileNames) {
		TextureData textureData = TextureUtils.loadTextureArrayFiles(fileNames);
		int textureId = TextureUtils.loadTextureArrayToOpenGL(textureData, this);
		return new Texture(textureId, GL45.GL_TEXTURE_2D_ARRAY, textureData.getWidth());
	}
	
	public TextureBuilder clampEdges(){
		this.clampEdges = true;
		return this;
	}
	
	public TextureBuilder normalMipMap(){
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}
	
	public TextureBuilder nearestFiltering(){
		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
		return this;
	}
	
	public TextureBuilder anisotropic(){
		this.mipmap = true;
		this.anisotropic = true;
		return this;
	}
	
	protected boolean isClampEdges() {
		return clampEdges;
	}

	protected boolean isMipmap() {
		return mipmap;
	}

	protected boolean isAnisotropic() {
		return anisotropic;
	}

	protected boolean isNearest() {
		return nearest;
	}

}

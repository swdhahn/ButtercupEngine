package com.countgandi.engine.model.textures;

import java.nio.ByteBuffer;

public class TextureData {
	
	private int width;
	private int height;
	private ByteBuffer[] buffer;
	
	public TextureData(ByteBuffer buffer, int width, int height){
		this.buffer = new ByteBuffer[1];
		this.buffer[0] = buffer;
		this.width = width;
		this.height = height;
	}
	
	public TextureData(ByteBuffer[] buffer, int width, int height){
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public ByteBuffer getBuffer(){
		return buffer[0];
	}
	
	public ByteBuffer[] getBufferArray(){
		return buffer;
	}

}

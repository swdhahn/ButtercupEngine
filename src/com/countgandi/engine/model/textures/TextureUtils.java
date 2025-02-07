package com.countgandi.engine.model.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

public class TextureUtils {

	public static TextureData loadTextureFile(String path) {
		BufferedImage img = loadBufferedImage(path);
		return new TextureData(imageToBuffer(img), img.getWidth(), img.getHeight());
	}

	public static BufferedImage loadBufferedImage(String path) {
		BufferedImage img = null;
		if (path == null) {
			img = defaultTexture();
		} else {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			path = path.replaceAll("\\\\", "/");
			InputStream in = TextureUtils.class.getResourceAsStream(path);
			try {
				img = ImageIO.read(in);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Tried to load texture " + path + ", didn't work");
				img = defaultTexture();
			}
		}
		return img;
	}

	public static ByteBuffer imageToBuffer(BufferedImage image) {
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4);
		for (int pixel : pixels) {
			buffer.put((byte) ((pixel >> 16) & 0xFF));
			buffer.put((byte) ((pixel >> 8) & 0xFF));
			buffer.put((byte) (pixel & 0xFF));
			buffer.put((byte) (pixel >> 24));
		}
		buffer.flip();
		return buffer;
	}

	private static BufferedImage defaultTexture() {
		return new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
	}

	protected static int loadTextureToOpenGL(TextureData data, TextureBuilder builder) {
		int texID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0,
				GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());

		loadBuilderData(GL11.GL_TEXTURE_2D, builder);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return texID;
	}

	public static TextureData loadTextureArrayFiles(String[] fileNames) {
		ByteBuffer[] buffers = new ByteBuffer[fileNames.length];
		int width = 0, height = 0;
		boolean first = true;
		for (int i = 0; i < fileNames.length; i++) {
			BufferedImage img = loadBufferedImage(fileNames[i]);
			if (first) {
				width = img.getWidth();
				height = img.getHeight();
			}
			buffers[i] = imageToBuffer(img);
		}
		return new TextureData(buffers, width, height);
	}

	public static int loadTextureArrayToOpenGL(TextureData textureData, TextureBuilder builder) {
		int texID = GL11.glGenTextures();
		int width = textureData.getWidth(), height = textureData.getHeight();

		GL11.glBindTexture(GL45.GL_TEXTURE_2D_ARRAY, texID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		// For textureAtlas to Array: GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
		// GL11.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0,
		// GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, textureData.getBuffer());
		GL45.glTexStorage3D(GL45.GL_TEXTURE_2D_ARRAY, getMipMapCount(width), GL45.GL_RGBA8, width, height,
				textureData.getBufferArray().length);

		for (int i = 0; i < textureData.getBufferArray().length; i++) {
			GL45.glTexSubImage3D(GL45.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width, height, 1, GL45.GL_RGBA,
					GL45.GL_UNSIGNED_BYTE, textureData.getBufferArray()[i]);
		}

		loadBuilderData(GL45.GL_TEXTURE_2D_ARRAY, builder);

		GL45.glGenerateMipmap(GL45.GL_TEXTURE_2D_ARRAY);

		return texID;
	}

	private static int getMipMapCount(int textureSize) {
		return (int) (Math.log(textureSize));
	}

	private static void loadBuilderData(int type, TextureBuilder builder) {
		if (builder.isMipmap()) {
			GL30.glGenerateMipmap(type);
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			if (builder.isAnisotropic() && GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				GL11.glTexParameterf(type, GL14.GL_TEXTURE_LOD_BIAS, 0);
				GL11.glTexParameterf(type, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
						4.0f);
			}
		} else if (builder.isNearest()) {
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		} else {
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
		if (builder.isClampEdges()) {
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}
	}

}

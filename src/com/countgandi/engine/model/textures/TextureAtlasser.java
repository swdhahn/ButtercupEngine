package com.countgandi.engine.model.textures;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TextureAtlasser {

	public static TextureData atlasTextures(String[] fileNames) {
		BufferedImage[] images = new BufferedImage[fileNames.length];
		images[0] = TextureUtils.loadBufferedImage(fileNames[0]);
		int smallestSize = images[0].getWidth();
		for (int i = 1; i < images.length; i++) {
			images[i] = TextureUtils.loadBufferedImage(fileNames[i]);
			if (smallestSize > images[i].getWidth()) {
				smallestSize = images[i].getWidth();
			}
		}
		int size = (int) Math.ceil(Math.sqrt(images.length));
		int width = smallestSize * size, height = width;
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		int imageDrawn = 0;
		for (int y = 0; y < height; y += smallestSize) {
			for (int x = 0; x < width; x += smallestSize) {
				if (imageDrawn >= images.length)
					break;
				g.drawImage(images[imageDrawn], x, y, smallestSize, smallestSize, null);
				imageDrawn++;
			}
		}
		g.dispose();
		return new TextureData(TextureUtils.imageToBuffer(newImage), width, height);
	}

}

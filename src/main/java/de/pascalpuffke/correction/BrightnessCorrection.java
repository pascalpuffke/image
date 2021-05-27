package de.pascalpuffke.correction;

import de.pascalpuffke.util.BufferedImageUtil;

import java.awt.image.BufferedImage;

public class BrightnessCorrection {

	public BrightnessCorrection() {

	}

	public BufferedImage brighten(BufferedImage input, int value) {
		var width = input.getWidth();
		var height = input.getHeight();
		var matrix = BufferedImageUtil.getMatrix(input);
		var result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for(var x = 0; x < width; x++) {
			for(var y = 0; y < height; y++) {
				var oldPixel = matrix[x][y];
				var newPixel = value >= 0 ? oldPixel.addClamped(value) : oldPixel.subClamped(Math.abs(value));
				result.setRGB(x, y, newPixel.getRgbInt());
			}
		}

		return result;
	}

	public BufferedImage darken(BufferedImage input, int value) {
		return brighten(input, -value);
	}

}

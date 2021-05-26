package de.pascalpuffke.util;

import de.pascalpuffke.color.RGBColor;

import java.awt.image.BufferedImage;

public class BufferedImageUtil {

	public static int[] getPixels(BufferedImage input) {
		return input.getRGB(0, 0, input.getWidth(), input.getHeight(), null, 0, input.getWidth());
	}

	public static RGBColor[][] getMatrix(BufferedImage input) {
		var w = input.getWidth();
		var h = input.getHeight();
		var a = new RGBColor[w][h];

		for (var x = 0; x < w; x++)
			for (var y = 0; y < h; y++)
				a[x][y] = new RGBColor(input.getRGB(x, y));

		return a;
	}

}

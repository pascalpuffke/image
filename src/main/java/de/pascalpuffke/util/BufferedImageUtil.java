package de.pascalpuffke.util;

import java.awt.image.BufferedImage;

public class BufferedImageUtil {

	public static int[] getPixels(BufferedImage input) {
		return input.getRGB(0, 0, input.getWidth(), input.getHeight(), null, 0, input.getWidth());
	}

	public static int[][] getMatrix(BufferedImage input) {
		var w = input.getWidth();
		var h = input.getHeight();
		var a = new int[w][h];

		for(var x = 0; x < w; x++)
			for(var y = 0; y < h; y++)
				a[x][y] = input.getRGB(x, y);

		return a;
	}
}

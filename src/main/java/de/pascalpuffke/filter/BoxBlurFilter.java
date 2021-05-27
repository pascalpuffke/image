package de.pascalpuffke.filter;

import de.pascalpuffke.util.BufferedImageUtil;

import java.awt.image.BufferedImage;

public class BoxBlurFilter {

	public BoxBlurFilter() {

	}

	public BufferedImage apply(BufferedImage input, int radius, int iterations) {
		var width = input.getWidth();
		var height = input.getHeight();
		var inPixels = BufferedImageUtil.getPixels(input);
		var outPixels = new int[inPixels.length];
		var result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (var i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, radius);
			blur(outPixels, inPixels, height, width, radius);
		}

		result.setRGB(0, 0, width, height, inPixels, 0, width);

		return result;
	}

	private void blur(int[] in, int[] out, int w, int h, int radius) {
		int tableSize = 2 * radius + 1;
		int[] divide = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++) {
			divide[i] = i / tableSize;
		}

		int inIndex = 0;

		for (int y = 0; y < h; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -radius; i <= radius; i++) {
				int rgb = in[inIndex + clamp(i, 0, w - 1)];

				ta += (rgb >> 24) & 0xFF;
				tr += (rgb >> 16) & 0xFF;
				tg += (rgb >> 8) & 0xFF;
				tb += rgb & 0xFF;
			}

			for (int x = 0; x < w; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];

				int rgb1 = in[inIndex + Math.min(x + radius + 1, w - 1)];
				int rgb2 = in[inIndex + Math.max(x - radius, 0)];

				ta += ((rgb1 >> 24) & 0xFF) - ((rgb2 >> 24) & 0xFF);
				tr += ((rgb1 & 0xFF0000) - (rgb2 & 0xFF0000)) >> 16;
				tg += ((rgb1 & 0xFF00) - (rgb2 & 0xFF00)) >> 8;
				tb += (rgb1 & 0xFF) - (rgb2 & 0xFF);
				outIndex += h;
			}

			inIndex += w;
		}
	}

	private int clamp(int x, int a, int b) {
		return (x < a) ? a : Math.min(x, b);
	}
}

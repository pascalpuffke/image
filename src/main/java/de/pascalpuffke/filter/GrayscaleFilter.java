package de.pascalpuffke.filter;

import de.pascalpuffke.util.BufferedImageUtil;

import java.awt.image.BufferedImage;

import static de.pascalpuffke.util.ColorUtil.gammaToLin;
import static de.pascalpuffke.util.ColorUtil.sRGBtoLin;

public class GrayscaleFilter {

	// sRGB standard Y chromaticity values
	private final double WEIGHT_R = 0.2126;
	private final double WEIGHT_G = 0.7152;
	private final double WEIGHT_B = 0.0722;

	public enum Method {
		AVERAGE,
		MIN,
		MAX,
		PERCEPTUAL
	}

	public GrayscaleFilter() {

	}

	public BufferedImage apply(BufferedImage input, Method method) {
		var width = input.getWidth();
		var height = input.getHeight();
		var matrix = BufferedImageUtil.getMatrix(input);
		var result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		for (var x = 0; x < width; x++) {
			for (var y = 0; y < height; y++) {
				// TODO make gammaToLin RGBColor-compatible
				var pixel = gammaToLin(matrix[x][y].getRgbInt());
				var r = pixel[0];
				var g = pixel[1];
				var b = pixel[2];
				var newPixel = repeatBits(switch (method) {
					case AVERAGE -> (int)
							(((r + g + b) / 3) * 255);
					case MAX -> (int)
							(Math.max(r, Math.max(g, b)) * 255);
					case MIN -> (int)
							(Math.min(r, Math.min(g, b)) * 255);
					case PERCEPTUAL -> (int)
							((WEIGHT_R * sRGBtoLin(r) + WEIGHT_G * sRGBtoLin(g) + WEIGHT_B * sRGBtoLin(b)) * 255);
				});

				result.setRGB(x, y, newPixel);
			}
		}

		return result;
	}

	private int repeatBits(int base) {
		return base << 16 | base << 8 | base;
	}

}
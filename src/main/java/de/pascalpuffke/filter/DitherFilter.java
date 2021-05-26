package de.pascalpuffke.filter;

import de.pascalpuffke.color.RGBColor;
import de.pascalpuffke.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

import static de.pascalpuffke.util.ColorUtil.getColorChannel;

public class DitherFilter {

	public enum Method {
		FLOYD_STEINBERG,
		NEAREST_COLOR
	}

	public DitherFilter() {

	}

	public BufferedImage apply(BufferedImage input, Method method, int[] palette) {
		var width = input.getWidth();
		var height = input.getHeight();
		var indexColorModel = new IndexColorModel(
				8,
				palette.length,
				getColorChannel(palette, 16),
				getColorChannel(palette, 8),
				getColorChannel(palette, 0)
		);
		var result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);

		switch (method) {
			case NEAREST_COLOR -> result.setRGB(0, 0, width, height, BufferedImageUtil.getPixels(input), 0, width);
			case FLOYD_STEINBERG -> {
				var raster = getColorRaster(input);
				for (var y = 0; y < height; y++) {
					for (var x = 0; x < width; x++) {
						var oldColor = raster[y][x];
						var newColor = pickClosestColor(oldColor, palette);
						var quantError = oldColor.sub(newColor);

						result.setRGB(x, y, newColor.getRgbInt());

						if (x + 1 < width)
							raster[y][x + 1] = raster[y][x + 1].add(quantError.mul(7. / 16));
						if (x - 1 >= 0 && y + 1 < height)
							raster[y + 1][x - 1] = raster[y + 1][x - 1].add(quantError.mul(3. / 16));
						if (y + 1 < height)
							raster[y + 1][x] = raster[y + 1][x].add(quantError.mul(5. / 16));
						if (x + 1 < width && y + 1 < height)
							raster[y + 1][x + 1] = raster[y + 1][x + 1].add(quantError.mul(1. / 16));
					}
				}
			}
		}

		return result;
	}

	private RGBColor[][] getColorRaster(BufferedImage image) {
		var width = image.getWidth();
		var height = image.getHeight();
		var a = new RGBColor[height][width];

		for (var y = 0; y < height; y++) {
			for (var x = 0; x < width; x++) {
				a[y][x] = new RGBColor(image.getRGB(x, y));
			}
		}

		return a;
	}

	private RGBColor pickClosestColor(RGBColor target, int[] palette) {
		var closest = new RGBColor(palette[0]);

		for (var c : palette) {
			var current = new RGBColor(c);
			if (current.diff(target) < closest.diff(target))
				closest = current;
		}

		return closest;
	}

}

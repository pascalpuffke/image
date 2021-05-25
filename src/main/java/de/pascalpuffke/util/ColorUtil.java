package de.pascalpuffke.util;

public class ColorUtil {

	/**
	 * I have no idea. Ask Stackoverflow.
	 * @param color
	 * @return Funny numbers
	 */
	public static double sRGBtoLin(double color) {
		if (color <= 0.04045)
			return (color / 12.92);
		return Math.pow((color + 0.055) / 1.055, 2.4);
	}

	/**
	 * Converts a [0; 255] gamma integer to a [0; 1] linear decimal.
	 * The result is split by color channel and stored in an array.
	 * @param gamma Input gamma integer
	 * @return Array containing color channels as linear decimal numbers
	 */
	public static double[] gammaToLin(int gamma) {
		var r = (double) ((gamma >> 16) & 0xFF) / 255;
		var g = (double) ((gamma >> 8) & 0xFF) / 255;
		var b = (double) ((gamma) & 0xFF) / 255;
		return new double[]{ r, g, b };
	}
}

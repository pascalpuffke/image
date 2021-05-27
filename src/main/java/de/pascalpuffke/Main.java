package de.pascalpuffke;

import de.pascalpuffke.correction.BrightnessCorrection;
import de.pascalpuffke.correction.ResizeCorrection;
import de.pascalpuffke.filter.BoxBlurFilter;
import de.pascalpuffke.filter.DitherFilter;
import de.pascalpuffke.filter.GrayscaleFilter;
import de.pascalpuffke.logging.Logger;
import org.apache.commons.cli.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is stupid and no one should ever actually use this
 */
public class Main {

	/*
	 * Operation order:
	 * 1. Resize image.
	 * 2. Apply brightness corrections.
	 * 3. Apply filters.
	 * 4. Save edited image.
	 */

	private static final Logger logger = new Logger("Main");

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length == 0)
			Options.printHelp();

		Options.parseOptions(args);

		logger.debugln("Version " + Version.VERSION);

		var input = ImageIO.read(Options.input.toFile());
		var processTimeA = System.currentTimeMillis();
		BufferedImage image = null;

		// Apply corrections to image first and any filters after that

		// Resizing
		if (Options.width != 0) {
			var ratio = (double) input.getWidth() / input.getHeight();
			var width = Options.width;
			// Height is optional, calculate aspect ratio-preserving height if it is missing
			var height = (int) (Options.height == 0 ? width * ratio : Options.height);
			var resizeCorrection = new ResizeCorrection();

			image = resizeCorrection.resize(input, width, height, Options.resizeQuality);
			logger.debugf("Resize image to %dx%d\n", width, height);
		}

		// Brightness correction
		if (Options.brighten != 0) {
			var temp = image == null ? input : image;
			var value = Options.brighten;
			var brightnessCorrection = new BrightnessCorrection();

			image = brightnessCorrection.brighten(temp, value);
			logger.debugln("Change brightness to +" + value);
		}

		if (Options.darken != 0) {
			var temp = image == null ? input : image;
			var value = Options.darken;
			var brightnessCorrection = new BrightnessCorrection();

			image = brightnessCorrection.darken(temp, value);
			logger.debugln("Change brightness to -" + value);
		}

		// Applying filters
		for(var filter : Options.filters) {
			var temp = image == null ? input : image;

			image = switch (filter) {
				case GRAYSCALE_PERCEPTUAL, GRAYSCALE -> new GrayscaleFilter().apply(temp, GrayscaleFilter.Method.PERCEPTUAL);
				case GRAYSCALE_MAX -> new GrayscaleFilter().apply(temp, GrayscaleFilter.Method.MAX);
				case GRAYSCALE_MIN -> new GrayscaleFilter().apply(temp, GrayscaleFilter.Method.MIN);
				case GRAYSCALE_AVERAGE -> new GrayscaleFilter().apply(temp, GrayscaleFilter.Method.AVERAGE);
				case FLOYD_STEINBERG -> new DitherFilter().apply(temp, DitherFilter.Method.FLOYD_STEINBERG, readPalette(Options.palette));
				case NEAREST_COLOR -> new DitherFilter().apply(temp, DitherFilter.Method.NEAREST_COLOR, readPalette(Options.palette));
				case BOX_BLUR -> new BoxBlurFilter().apply(temp, Options.radius, Options.iterations);
			};
		}

		var processTimeB = System.currentTimeMillis();

		logger.debugf("Processed image in %dms\n", (processTimeB - processTimeA));

		var outString = Options.output.toString();
		var outType = outString.substring(outString.lastIndexOf('.') + 1);
		var file = Options.output.toFile();
		var saveTimeA = System.currentTimeMillis();

		if (image == null || !ImageIO.write(image, outType, file))
			System.err.println("Invalid image, try executing with '--debug' argument for details");
		else {
			var saveTimeB = System.currentTimeMillis();

			logger.debugf("Wrote file %s in %dms\n", file.getAbsolutePath(), (saveTimeB - saveTimeA));
		}
	}

	private static int[] readPalette(String s) {
		var result = new ArrayList<Integer>();
		if (s.contains("\n")) {
			for (var line : s.split("\n")) {
				result.add(parseIntFromPaletteEntry(line));
			}
		} else {
			for (var entry : s.split(" ")) {
				result.add(parseIntFromPaletteEntry(entry));
			}
		}
		logger.debugf("Parsed palette with %d colors\n", result.size());
		return result.stream().mapToInt(i -> i).toArray();
	}

	private static int parseIntFromPaletteEntry(String s) {
		if (s.startsWith("#"))
			return Integer.parseInt(s.substring(1), 16);
		return Integer.parseInt(s, 16);
	}
}

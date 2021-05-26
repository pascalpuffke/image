package de.pascalpuffke;

import de.pascalpuffke.filter.DitherFilter;
import de.pascalpuffke.filter.GrayscaleFilter;
import de.pascalpuffke.logging.Logger;
import org.apache.commons.cli.ParseException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

	private static final Logger logger = new Logger("Main");

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length == 0)
			Options.printHelp();

		Options.parseOptions(args);

		logger.debugln("Version " + Version.VERSION);

		var input = ImageIO.read(Options.input.toFile());
		var processTimeA = System.currentTimeMillis();
		var image = switch (Options.filter) {
			case GRAYSCALE_PERCEPTUAL, GRAYSCALE -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.PERCEPTUAL);
			case GRAYSCALE_MAX -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.MAX);
			case GRAYSCALE_MIN -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.MIN);
			case GRAYSCALE_AVERAGE -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.AVERAGE);
			case FLOYD_STEINBERG -> new DitherFilter().apply(input, DitherFilter.Method.FLOYD_STEINBERG, readPalette(Options.palette));
			case NEAREST_COLOR -> new DitherFilter().apply(input, DitherFilter.Method.NEAREST_COLOR, readPalette(Options.palette));
			default -> null;
		};
		var processTimeB = System.currentTimeMillis();

		logger.debugf("Processed image in %dms\n", (processTimeB - processTimeA));

		var outString = Options.output.toString();
		var outType = outString.substring(outString.lastIndexOf('.') + 1);
		var file = Options.output.toFile();
		var saveTimeA = System.currentTimeMillis();

		if (image == null || !ImageIO.write(image, outType, file))
			System.err.println("Some error happened");
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

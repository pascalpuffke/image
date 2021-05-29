package de.pascalpuffke;

import de.pascalpuffke.filter.FilterCategory;
import de.pascalpuffke.filter.Filters;
import de.pascalpuffke.logging.Logger;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * nightmare
 */
public class Options {

	private static final Logger logger = new Logger("Options");
	private static final Option[] optionArray = {
			// Would've loved to use 'required()' instead of manually checking arg presence later, but don't like
			// the way this library tells the user that an argument is missing. (It just throws an exception without
			// much useful information.)
			// Apache Commons CLI is horrible. Maybe I'll write a replacement later.
			Option.builder("i").argName("path").longOpt("input").hasArg()
					.desc("Path to input image file [required]")
					.build(),
			Option.builder("o").argName("path").longOpt("output").hasArg()
					.desc("Path to output image file [required]")
					.build(),
			Option.builder("f").argName("filter").longOpt("filter").hasArg()
					.desc("Filter to apply; use '--list-filters' argument for more info.")
					.build(),
			Option.builder("p").argName("path|text").longOpt("palette").hasArg()
					.desc("Colour palette used for palette conversion filters. Can also be a path to a text file; see README.md for formatting details")
					.build(),
			Option.builder("b").argName("num").longOpt("brighten").hasArg()
					.desc("Brighten an image. Num ranges from 0 (no change) to 255 (clip everything white).")
					.build(),
			Option.builder("d").argName("num").longOpt("darken").hasArg()
					.desc("Darken an image. Num ranges from 0 (no change) to 255 (clip everything black).")
					.build(),
			Option.builder("r").argName("WxH").longOpt("resize").hasArg()
					.desc("Resize an image.")
					.build(),
			Option.builder("rq").argName("WxH").longOpt("resize-quick").hasArg()
					.desc("Resize an image, using a faster nearest-neighbor algorithm.")
					.build(),
			Option.builder().argName("pixels").longOpt("radius").hasArg()
					.desc("Pixel radius used for blurring filters [default: 10]")
					.build(),
			Option.builder().argName("num").longOpt("iterations").hasArg()
					.desc("Number of iterations used for blurring filters [default: 1]")
					.build(),
			Option.builder().longOpt("debug")
					.desc("Annihilates stdout with cool looking, but useless information")
					.build(),
			Option.builder("l").longOpt("list-filters")
					.desc("Lists all available filters")
					.build(),
			Option.builder("h").longOpt("help")
					.desc("Show help screen")
					.build(),
			Option.builder("V").longOpt("version")
					.desc("Show version information")
					.build(),
	};
	private static final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

	public static List<Filters> filters = new ArrayList<>();
	public static Path input, output;
	public static String palette;
	public static boolean debug, resizeQuality;
	public static int radius, iterations, brighten, darken, width, height;

	static {
		Arrays.stream(optionArray).forEach(options::addOption);
	}

	public static void parseOptions(String... args) throws ParseException, IOException {
		var parser = new DefaultParser();
		var commandLine = parser.parse(options, args);

		debug = commandLine.hasOption("debug");

		if (commandLine.hasOption("help"))
			printHelp();

		if (commandLine.hasOption("version")) {
			System.out.println(Version.VERSION);

			System.exit(0);
		}

		if (commandLine.hasOption("list-filters")) {
			System.out.println("Available filters: \n");

			for (var category : FilterCategory.values()) {
				var categoryName = category.toString().replace("_", " ").toLowerCase();
				System.out.println("\t" + Character.toUpperCase(categoryName.charAt(0)) + categoryName.substring(1) + ": ");

				for (var filter : category.getFiltersFromCategory()) {
					System.out.printf("\t\t%s: %s\n", filter.toString().toLowerCase(), filter.description);
				}
				System.out.println("\n\t\t" + category.description + "\n");
			}

			System.out.println("Use them by running the program with the '--filter' argument.");
			System.out.println("Note that some filters might require additional parameters");
			System.exit(0);
		}

		if (!commandLine.hasOption("input"))
			System.err.println("No input image specified; invoke with argument '--input <path>' or use '--help' for additional information");
		if (!commandLine.hasOption("output"))
			System.err.println("No output image specified; invoke with argument '--output <path>' or use '--help' for additional information");

		try {
			input = Path.of(commandLine.getOptionValue("input"));
			logger.debugln("input = " + input.toAbsolutePath());

			output = Path.of(commandLine.getOptionValue("output"));
			logger.debugln("output = " + output.toAbsolutePath());
		} catch (NullPointerException e) {
			// We have already notified the user of missing args; ignore the exception and exit.
			System.exit(1);
		}

		if(commandLine.getOptionValues("filter") != null) {
			for(var value : commandLine.getOptionValues("filter")) {
				for(var filter : Filters.values()) {
					if (value.toUpperCase().equals(filter.toString())) {
						filters.add(filter);

						logger.debugln("filter = " + filter);
						break;
					}
				}
			}
		}

		radius = Integer.parseInt(commandLine.getOptionValue("radius", "10"));
		iterations = Integer.parseInt(commandLine.getOptionValue("iterations", "1"));
		brighten = Integer.parseInt(commandLine.getOptionValue("brighten", "0"));
		darken = Integer.parseInt(commandLine.getOptionValue("darken", "0"));
		resizeQuality = commandLine.hasOption("resize");

		logger.debugln("radius = " + radius);
		logger.debugln("iterations = " + iterations);
		logger.debugln("brighten = " + brighten);
		logger.debugln("darken = " + darken);
		logger.debugln("resizeQuality = " + resizeQuality);

		var size = commandLine.getOptionValue("resize") == null ?
				commandLine.getOptionValue("resize-quick") : commandLine.getOptionValue("resize");
		if(size != null) {
			if (size.contains("x")) {
				var temp = size.split("x");

				width = Integer.parseInt(temp[0]);
				height = Integer.parseInt(temp[1]);
			} else {
				width = Integer.parseInt(size);
			}
		}

		logger.debugln("width = " + width);
		logger.debugln("height = " + height);

		for(var filter : filters) {
			if(filter.category.equals(FilterCategory.PALETTE_CONVERSION_FILTERS)) {
				if (commandLine.hasOption("palette")) {
					var input = commandLine.getOptionValue("palette");
					var path = Path.of(input);
					if (Files.exists(path))
						palette = Files.readString(path, StandardCharsets.UTF_8);
					else
						palette = input;
				} else {
					System.err.println("This filter requires a target palette.");
					System.exit(1);
				}
				break;
			}
		}
	}

	public static void printHelp() {
		var help = new HelpFormatter();
		// Rust has this neat crate called 'term-size', right?
		// Well, it turns out that there is no such solution for Java, especially not one that is reliable.
		// That means we'll just force a max. of 80 characters and ignore potentially wasted space, or smaller terminals.
		help.setWidth(80);
		help.setLeftPadding(4);
		help.setLongOptPrefix(" --"); // why isn't this the default???
		help.printHelp(
				"image [options]",
				"CLI image manipulation utility\n\n",
				options,
				"\nFound a bug? Got ideas? Head over to the GitHub repo: https://github.com/pascalpuffke/image"
		);

		System.exit(0);
	}
}
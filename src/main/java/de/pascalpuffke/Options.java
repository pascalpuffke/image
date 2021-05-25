package de.pascalpuffke;

import de.pascalpuffke.filter.FilterCategory;
import de.pascalpuffke.filter.Filters;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.nio.file.Path;
import java.util.Arrays;

public class Options {

	private static final Option[] optionArray = {
			// Would've loved to use 'required()' instead of manually checking arg presence later, but don't like
			// the way this library tells the user that an argument is missing. (It just throws an exception without
			// much useful information.)
			// Apache Commons CLI is horrible. Maybe I'll write a replacement later.
			Option.builder("i").argName("path").longOpt("input").hasArg().desc("Path to input image file [required]").build(),
			Option.builder("o").argName("path").longOpt("output").hasArg().desc("Path to output image file [required]").build(),
			Option.builder("f").argName("filter").longOpt("filter").hasArg().desc("Filter to apply [required]").build(),
			Option.builder("r").argName("pixels").longOpt("radius").hasArg().desc("Pixel radius used for e.g. blurring filters [default: 10]").build(),
			Option.builder("p").argName("path|text").longOpt("palette").hasArg().desc("Colour palette used for palette conversion filters").build(),
			Option.builder().argName("num").longOpt("iterations").hasArg().desc("Number of iterations used for e.g. blurring filters [default: 1]").build(),
			Option.builder("d").longOpt("debug").desc("Annihilates stdout with useless information, but it looks cool").build(),
			Option.builder("l").longOpt("list-filters").desc("Lists all available filters").build(),
			Option.builder("h").longOpt("help").desc("Show help screen").build(),
			Option.builder("V").longOpt("version").desc("Show version information").build(),
	};
	private static final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

	public static Filters filter;
	public static Path input, output;

	static {
		Arrays.stream(optionArray).forEach(options::addOption);
	}

	public static void parseOptions(String... args) throws ParseException {
		var parser = new DefaultParser();
		var commandLine = parser.parse(options, args);

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
		if (!commandLine.hasOption("filter"))
			System.err.println("No filter specified; invoke with argument '--filter <filter>' or use '--help' for additional information");

		try {
			input = Path.of(commandLine.getOptionValue("input"));
			output = Path.of(commandLine.getOptionValue("output"));
			filter = switch (commandLine.getOptionValue("filter").toLowerCase()) {
				case "grayscale", "grayscale_perceptual" -> Filters.GRAYSCALE_PERCEPTUAL;
				case "grayscale_average" -> Filters.GRAYSCALE_AVERAGE;
				case "grayscale_min" -> Filters.GRAYSCALE_MIN;
				case "grayscale_max" -> Filters.GRAYSCALE_MAX;
				default -> null;
			};
		} catch (NullPointerException e) {
			// We have already notified the user of missing args; ignore the exception and exit.
			System.exit(0);
		} catch (Exception e) {
			// Something else went wrong; print the exception.
			e.printStackTrace();
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
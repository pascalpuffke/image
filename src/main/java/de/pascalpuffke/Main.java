package de.pascalpuffke;

import de.pascalpuffke.filter.GrayscaleFilter;
import org.apache.commons.cli.ParseException;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        /*
        var file = ImageIO.read(new File("input.png"));
        var filter = new MonochromeFilter();
        for(var method : MonochromeFilter.Method.values()) {
            var a = System.currentTimeMillis();
            ImageIO.write(filter.apply(file, method), "png", new File(method.toString() + ".png"));
            var b = System.currentTimeMillis();
            System.out.println(method.toString() + ": " + (b - a) + "ms");
        }

         */
        if(args.length == 0)
            Options.printHelp();

        Options.parseOptions(args);

        var input = ImageIO.read(Options.input.toFile());
        var image = switch(Options.filter) {
            case GRAYSCALE_PERCEPTUAL, GRAYSCALE -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.PERCEPTUAL);
            case GRAYSCALE_MAX -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.MAX);
            case GRAYSCALE_MIN -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.MIN);
            case GRAYSCALE_AVERAGE -> new GrayscaleFilter().apply(input, GrayscaleFilter.Method.AVERAGE);
            default -> null;
        };

        var outString = Options.output.toString();
        var outType = outString.substring(outString.lastIndexOf('.') + 1);

        if(!ImageIO.write(image, outType, Options.output.toFile()))
            System.err.println("Some error happened");
        else
            System.out.println("Wrote file " + Options.output.toFile().getAbsolutePath());
    }
}

package de.pascalpuffke.filter;

import static de.pascalpuffke.filter.FilterCategory.*;

public enum Filters {
	GRAYSCALE(GRAYSCALE_FILTERS,
			"Grayscale conversion. Macro for grayscale_perceptual."),
	GRAYSCALE_AVERAGE(GRAYSCALE_FILTERS,
			"Grayscale conversion. Takes subpixel averages."),
	GRAYSCALE_MIN(GRAYSCALE_FILTERS,
			"Grayscale conversion. Takes lowest subpixel value."),
	GRAYSCALE_MAX(GRAYSCALE_FILTERS,
			"Grayscale conversion. Takes highest subpixel value."),
	GRAYSCALE_PERCEPTUAL(GRAYSCALE_FILTERS,
			"Grayscale conversion. Computes the weighted perceptual luminosity."),
	FLOYD_STEINBERG(PALETTE_CONVERSION_FILTERS,
			"Converts an image to another palette, applying Floyd-Steinberg error diffusion dithering."),
	NEAREST_COLOR(PALETTE_CONVERSION_FILTERS,
			"Converts an image to another palette, without applying any error diffusion."),
	BOX_BLUR(BLUR_FILTERS,
			"Applies a box blur. To approximate a Gaussian blur, use more iterations."),
	RESIZE(RESIZING_FILTERS,
			"Resizes an image. Macro for resize_quality."),
	RESIZE_FAST(RESIZING_FILTERS,
			"Resizes an image. Uses a faster nearest-neighbor algorithm."),
	RESIZE_QUALITY(RESIZING_FILTERS,
			"Resizes an image. Tries to preserve quality.");

	public final FilterCategory category;
	public final String description;

	Filters(FilterCategory category, String desc) {
		this.category = category;
		this.description = desc;
	}
}

package de.pascalpuffke.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FilterCategory {
	GRAYSCALE_FILTERS(
			"Filters used to turn images into greyscale ('black and white') colors."),
	PALETTE_CONVERSION_FILTERS(
			"Convert images to another color palette. These filters *require* specifying a palette."),
	BLUR_FILTERS(
			"Blurring filters. Optional arguments include '--radius' and '--iterations'."),
	RESIZING_FILTERS(
			"Resizing (scaling) images. Requires target dimensions 'WIDTHxHEIGHT' or size of longest side.");

	public String description;

	FilterCategory(String desc) {
		this.description = desc;
	}

	public List<Filters> getFiltersFromCategory() {
		return Arrays.stream(Filters.values())
				.filter(f -> f.category.equals(this))
				.collect(Collectors.toUnmodifiableList());
	}
}

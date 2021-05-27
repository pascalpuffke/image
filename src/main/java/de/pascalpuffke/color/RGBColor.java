package de.pascalpuffke.color;

import static de.pascalpuffke.util.MathUtil.maxClamp;
import static de.pascalpuffke.util.MathUtil.minClamp;

public class RGBColor {

	private int r, g, b;

	public RGBColor(int rgb) {
		this.r = (rgb >> 16) & 0xFF;
		this.g = (rgb >> 8) & 0xFF;
		this.b = (rgb) & 0xFF;
	}

	public RGBColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public RGBColor() {
		this.r = 0;
		this.g = 0;
		this.b = 0;
	}

	public RGBColor add(RGBColor other) {
		var a = new RGBColor();
		a.r = this.r + other.r;
		a.g = this.g + other.g;
		a.b = this.b + other.b;
		return a;
	}

	public RGBColor addClamped(int num) {
		var a = new RGBColor();
		a.r = maxClamp(255, this.r + num);
		a.g = maxClamp(255, this.g + num);
		a.b = maxClamp(255, this.b + num);
		return a;
	}

	public RGBColor sub(RGBColor other) {
		var a = new RGBColor();
		a.r = this.r - other.r;
		a.g = this.g - other.g;
		a.b = this.b - other.b;
		return a;
	}

	public RGBColor subClamped(int num) {
		var a = new RGBColor();
		a.r = minClamp(0, this.r - num);
		a.g = minClamp(0, this.g - num);
		a.b = minClamp(0, this.b - num);
		return a;
	}

	public RGBColor mul(double multiplier) {
		this.r *= multiplier;
		this.g *= multiplier;
		this.b *= multiplier;
		return this;
	}

	public int diff(RGBColor other) {
		var rDiff = other.r - this.r;
		var gDiff = other.g - this.g;
		var bDiff = other.b - this.b;
		return rDiff * rDiff + gDiff * gDiff + bDiff * bDiff;
	}

	public int getRgbInt() {
		return (this.r & 0xFF) << 16 | (this.g & 0xFF) << 8 | (this.b & 0xFF);
	}
}

package de.pascalpuffke.correction;

import org.imgscalr.Scalr;
import java.awt.image.BufferedImage;

public class ResizeCorrection {

	public ResizeCorrection() {

	}

	public BufferedImage resize(BufferedImage input, int width, int height, boolean quality) {
		return Scalr.resize(input, quality ? Scalr.Method.ULTRA_QUALITY : Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, width, height);
	}
}

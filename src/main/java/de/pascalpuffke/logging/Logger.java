package de.pascalpuffke.logging;

import de.pascalpuffke.Options;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

	private final String name;
	private final SimpleDateFormat sdf;

	public Logger(String name) {
		this.name = name;
		this.sdf = new SimpleDateFormat("HH:mm:ss");
	}

	public void debug(String s) {
		if (Options.debug)
			System.out.print(getTimestamp() + String.format("[%s] ", name) + s);
	}

	public void debugln(String s) {
		if (Options.debug)
			System.out.println(getTimestamp() + String.format("[%s] ", name) + s);
	}

	public void debugf(String format, Object... args) {
		if (Options.debug)
			System.out.printf(getTimestamp() + String.format("[%s] ", name) + format, args);
	}

	private String getTimestamp() {
		var builder = new StringBuilder();

		builder.append('[');
		builder.append(sdf.format(Calendar.getInstance().getTime()));
		builder.append(']');
		builder.append(' ');

		return builder.toString();
	}

}

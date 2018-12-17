package org.artorg.tools.phantomData.client.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

import huma.logging.Level;

public class Logger {
	public static final huma.logging.PrintStream debug;
	public static final huma.logging.PrintStream info;
	public static final huma.logging.PrintStream warn;
	public static final huma.logging.PrintStream error;
	public static final huma.logging.PrintStream fatal;
	private static final Level level;
	
	static {
		debug = new huma.logging.PrintStream(System.out, createPrefixSupplier("DEBUG"), Level.DEBUG);
		info = new huma.logging.PrintStream(System.out, createPrefixSupplier("INFO "), Level.INFO );
		warn = new huma.logging.PrintStream(System.out, createPrefixSupplier("WARN "), Level.WARN);
		error = new huma.logging.PrintStream(System.err, createPrefixSupplier("ERROR"), Level.ERROR);
		fatal = new huma.logging.PrintStream(System.err, createPrefixSupplier("FATAL"), Level.FATAL);
		level = Level.DEBUG;
	}
	
	private static Supplier<String> createPrefixSupplier(String logType) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss, SSS");
		String suffix = "ms, " +logType +": ";
		return () -> dateFormat.format(new Date()) +suffix;
	}
	
	public static Level getLevel() {
		return level;
	}

}

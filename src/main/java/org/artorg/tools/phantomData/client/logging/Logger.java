package org.artorg.tools.phantomData.client.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

import huma.logging.Level;

public class Logger {
	public static huma.logging.PrintStream debug;
	public static huma.logging.PrintStream info;
	public static huma.logging.PrintStream warn;
	public static huma.logging.PrintStream error;
	public static huma.logging.PrintStream fatal;
	private static Level level;
	
	static {
		setDefaultOut(System.out);
		setDefaultErr(System.err);
		setLevel(Level.DEBUG);
	}
	
	private static Supplier<String> createPrefixSupplier(String logType) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss, SSS");
		String suffix = "ms, " +logType +": ";
		return () -> dateFormat.format(new Date()) +suffix;
	}
	
	public static Level getLevel() {
		return level;
	}
	
	public static void setLevel(Level level) {
		Logger.level = level;
	}
	
	public static void setDefaultOut(java.io.PrintStream printStream) {
		debug = new huma.logging.PrintStream(System.out, createPrefixSupplier("DEBUG"), Level.DEBUG, () -> getLevel());
		info = new huma.logging.PrintStream(System.out, createPrefixSupplier("INFO "), Level.INFO, () -> getLevel());
		warn = new huma.logging.PrintStream(System.out, createPrefixSupplier("WARN "), Level.WARN, () -> getLevel());
		
		debug.setPrintStackTrace(true);
		warn.setPrintStackTrace(true);
	}
	
	public static void setDefaultErr(java.io.PrintStream printStream) {
		error = new huma.logging.PrintStream(System.err, createPrefixSupplier("ERROR"), Level.ERROR, () -> getLevel());
		fatal = new huma.logging.PrintStream(System.err, createPrefixSupplier("FATAL"), Level.FATAL, () -> getLevel());
		
		error.setPrintStackTrace(true);
		fatal.setPrintStackTrace(true);
	}

}

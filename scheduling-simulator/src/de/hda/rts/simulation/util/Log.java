package de.hda.rts.simulation.util;

import java.io.PrintStream;
import java.text.MessageFormat;

import com.google.common.base.Strings;

public final class Log {

	private static final PrintStream OUT = System.out;
	private static final PrintStream ERR = System.err;
	
	private Log() {
		throw new UnsupportedOperationException(Log.class.getName() + " can not be instantiated");
	}
	
	public static void d(String tag, String format, Object... args) {
		log(OUT, tag, format, args);
	}
	
	public static void e(String tag, String format, Object... args) {
		log(ERR, tag, format, args);
	}
	
	private static void log(PrintStream out, String TAG, String format, Object... args) {
		if (!Strings.isNullOrEmpty(format)) {
			String message = format;
			
			try {
				message = MessageFormat.format(format, args);
			}
			catch (Exception e) {}
			
			out.println("[" + TAG + "] " + message);
		}
	}
}

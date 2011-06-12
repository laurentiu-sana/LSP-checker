package lsp.test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
	public static final Level LOG_LEVEL = Level.WARNING;
	private static Logger logger = Logger.getLogger(Util.class.getName());
	private static final String HEADER = "[LSP Checker] ";

	public static void log(Class<?> className, String methodName) {
		logger.log(LOG_LEVEL, HEADER + "[" + className + "][" + methodName
				+ "] failed");
	}
}

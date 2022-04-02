package business.loghandler;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHandler {

	private static final LogHandler INSTANCE = new LogHandler();
	private static final Logger LOGGER = Logger.getLogger(LogHandler.class.getName());

	private LogHandler() {
		LOGGER.addHandler(new ConsoleHandler());
	}

	public static LogHandler getInstance() {
		return INSTANCE;
	}

	public void log(Level level, String sourceClass, String sourceMethod, String msg) {
		LOGGER.logp(level, sourceClass, sourceMethod, msg);
	}

	public void log(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
		LOGGER.logp(level, sourceClass, sourceMethod, msg, thrown);
	}

}

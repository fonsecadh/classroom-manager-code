package business.loghandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import business.errorhandler.exceptions.PersistenceException;
import business.errorhandler.logic.ErrorHandler;
import business.errorhandler.model.ErrorType;

public class LogHandler {

	private static final LogHandler INSTANCE = new LogHandler();
	private static final Logger LOGGER = Logger.getLogger(LogHandler.class.getName());
	private static final String FOLDERPATH = "files/log/";

	private LogHandler() {
		Handler handler = getHandler();
		Logger.getLogger(LogHandler.class.getName()).addHandler(handler);
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

	private Handler getHandler() {

		Handler h = null;

		try {

			h = new FileHandler(FOLDERPATH + getFileName() + ".txt", true);
			h.setFormatter(new SimpleFormatter());

		} catch (SecurityException e) {

			LogHandler.getInstance().log(Level.SEVERE, LogHandler.class.getName(), "getHandler",
					e.getLocalizedMessage(), e);
			PersistenceException pe = new PersistenceException("LOG csv file not found");
			ErrorHandler.getInstance().addError(new ErrorType(pe));

		} catch (IOException e) {

			LogHandler.getInstance().log(Level.SEVERE, LogHandler.class.getName(), "getHandler",
					e.getLocalizedMessage(), e);
			PersistenceException pe = new PersistenceException("Error encountered while loading the CONFIG file.");
			ErrorHandler.getInstance().addError(new ErrorType(pe));

		}

		return h;

	}

	private String getFileName() {

		LocalDate ld = LocalDate.now();
		int year = ld.getYear();
		int month = ld.getMonthValue();
		int day = ld.getDayOfMonth();

		String msg = String.valueOf(year) + String.valueOf(month) + String.valueOf(day);

		return msg;

	}

}

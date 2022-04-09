package business.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import business.errorhandler.logic.ErrorHandler;
import business.errorhandler.model.ErrorType;
import business.loghandler.LogHandler;

public class Config {

	private static final Config INSTANCE = new Config();

	private Properties prop;

	private Config() {
		this.prop = new Properties();
	}

	public static Config getInstance() {
		return INSTANCE;
	}

	public void load(String filename) {
		try {
			this.prop.load(new BufferedReader(new FileReader(filename)));
		} catch (FileNotFoundException e) {
			LogHandler.getInstance().log(Level.SEVERE, Config.class.getName(), "load", e.getLocalizedMessage(), e);
			ErrorHandler.getInstance().addError(new ErrorType("CONFIG file not found.", e));
		} catch (IOException e) {
			LogHandler.getInstance().log(Level.SEVERE, Config.class.getName(), "load", e.getLocalizedMessage(), e);
			ErrorHandler.getInstance().addError(new ErrorType("Error encountered while loading the CONFIG file.", e));
		}
	}

	public void addProperty(String key, String value) {
		this.prop.put(key, value);
	}

	public String getProperty(String key) {
		return this.prop.getProperty(key);
	}

}

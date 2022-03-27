package persistence.config;

import business.config.Config;

public interface ConfigDataAccess {
	
	Config loadConfig(String filename);

}

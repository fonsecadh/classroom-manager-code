package persistence.problem;

import java.util.List;

import business.classfinder.model.ClassfinderQuery;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import persistence.filemanager.FileManager;

public interface ClassfinderDataAccess {
	List<ClassfinderQuery> loadQueries(String filename,
			FileManager fileManager)
			throws PersistenceException, InputValidationException;
}

package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Subject;
import persistence.filemanager.FileManager;

public interface SubjectDataAccess {

	Map<String, Subject> loadSubjects(String filename, FileManager fileManager)
			throws InputValidationException, PersistenceException;

}

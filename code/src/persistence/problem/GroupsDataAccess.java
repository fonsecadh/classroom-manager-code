package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Group;
import business.problem.Subject;
import persistence.filemanager.FileManager;

public interface GroupsDataAccess {

	Map<String, Group> loadGroups(String filename, Map<String, Subject> subjects, FileManager fileManager)
			throws PersistenceException, InputValidationException;

}

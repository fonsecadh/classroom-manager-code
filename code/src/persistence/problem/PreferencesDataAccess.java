package persistence.problem;

import java.util.List;
import java.util.Map;

import business.alg.gen.model.Preference;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.Subject;
import persistence.filemanager.FileManager;

public interface PreferencesDataAccess {
	Map<String, List<Preference>> loadPreferences(String filename,
			Map<String, Classroom> classrooms,
			Map<String, Group> groups,
			Map<String, Subject> subjects, FileManager fileManager)
			throws PersistenceException, InputValidationException;
}

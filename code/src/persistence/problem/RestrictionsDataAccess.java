package persistence.problem;

import java.util.List;
import java.util.Map;

import business.alg.greed.model.Restriction;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.Subject;
import persistence.filemanager.FileManager;

public interface RestrictionsDataAccess {

	Map<String, List<Restriction>> loadRestrictions(String filename, Map<String, Classroom> classrooms,
			Map<String, Group> groups, Map<String, Subject> subjects, FileManager fileManager)
			throws PersistenceException, InputValidationException;

}

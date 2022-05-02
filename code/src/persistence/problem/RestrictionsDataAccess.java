package persistence.problem;

import java.util.List;
import java.util.Map;

import business.alg.greed.model.Restriction;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;
import business.problem.Group;
import persistence.filemanager.FileManager;

public interface RestrictionsDataAccess {

	Map<String, List<Restriction>> loadRestrictions(String filename, Map<String, Classroom> classrooms,
			Map<String, Group> groups, FileManager fileManager) throws PersistenceException, InputValidationException;

}

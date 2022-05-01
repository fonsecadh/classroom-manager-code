package persistence.problem;

import java.util.Map;

import business.alg.greed.model.Assignment;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;
import business.problem.Group;
import persistence.filemanager.FileManager;

public interface AssignmentsDataAccess {

	Map<String, Assignment> loadGroupSchedule(String filename, Map<String, Group> groups,
			Map<String, Classroom> classrooms, FileManager fileManager)
			throws PersistenceException, InputValidationException;

}

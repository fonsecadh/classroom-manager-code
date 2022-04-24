package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;

public interface ClassroomsDataAccess {

	Map<String, Classroom> loadClassrooms(String filename) throws InputValidationException, PersistenceException;

}

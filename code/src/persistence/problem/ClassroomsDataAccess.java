package persistence.problem;

import java.util.List;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;

public interface ClassroomsDataAccess {

	List<Classroom> loadClassrooms(String filename) throws InputValidationException, PersistenceException;

}

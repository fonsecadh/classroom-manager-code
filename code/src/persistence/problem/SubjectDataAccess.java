package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Subject;

public interface SubjectDataAccess {

	Map<String, Subject> loadSubjects(String filename) throws InputValidationException, PersistenceException;

}

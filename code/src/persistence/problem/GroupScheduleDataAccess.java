package persistence.problem;

import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Group;
import business.problem.schedule.GroupSchedule;
import persistence.filemanager.FileManager;

public interface GroupScheduleDataAccess {

	List<GroupSchedule> loadGroupSchedule(String filename, Map<String, Group> groups, FileManager fileManager)
			throws PersistenceException, InputValidationException;

}

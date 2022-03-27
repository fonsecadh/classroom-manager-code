package persistence.problem;

import java.util.List;

import business.problem.Group;

public interface GroupsDataAccess {
	
	List<Group> loadGroups(String filename);

}

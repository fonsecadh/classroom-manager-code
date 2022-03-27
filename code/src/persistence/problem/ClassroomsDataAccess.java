package persistence.problem;

import java.util.List;

import business.problem.Classroom;

public interface ClassroomsDataAccess {
	
	List<Classroom> loadClassrooms(String filename);

}

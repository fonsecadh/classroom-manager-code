package business.alg.greed.logic.filters;

import java.util.List;

import business.problem.Classroom;
import business.problem.Group;

public interface ClassroomFilter {
	
	List<Classroom> filterByGroup(Group group, List<Classroom> classrooms);

}

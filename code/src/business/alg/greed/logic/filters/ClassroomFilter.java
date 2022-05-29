package business.alg.greed.logic.filters;

import java.util.List;

import business.problem.model.Classroom;
import business.problem.model.Group;

public interface ClassroomFilter {

	List<Classroom> filterByGroup(Group group, List<Classroom> classrooms);

}

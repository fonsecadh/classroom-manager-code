package business.alg.greed.logic.filters;

import java.util.List;
import java.util.stream.Collectors;

import business.problem.Classroom;
import business.problem.Group;

public class CapacityFilter implements ClassroomFilter {

	@Override
	public List<Classroom> filterByGroup(Group group, List<Classroom> classrooms) {
		return classrooms
				.stream()
				.filter(c -> c.getNumberOfSeats() >= group.getNumberOfStudents())
				.collect(Collectors.toList());
	}

}

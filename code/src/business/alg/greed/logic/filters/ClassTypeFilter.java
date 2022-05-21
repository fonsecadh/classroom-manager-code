package business.alg.greed.logic.filters;

import java.util.List;
import java.util.stream.Collectors;

import business.problem.model.Classroom;
import business.problem.model.Group;

public class ClassTypeFilter implements ClassroomFilter {

	@Override
	public List<Classroom> filterByGroup(Group group, List<Classroom> classrooms) {
		return classrooms
				.stream()
				.filter(c -> c.getType().equals(group.getClassroomType()))
				.collect(Collectors.toList());
	}

}

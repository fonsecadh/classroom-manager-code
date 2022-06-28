package business.alg.greed.logic.filters;

import java.util.List;

import business.problem.model.Classroom;
import business.problem.model.Group;

public interface ClassroomFilter {
	/**
	 * Filters the classrooms ({@link Classroom}) valid for a given group
	 * ({@link Group}).
	 * 
	 * @param group      The group.
	 * @param classrooms The list of classrooms to be filtered.
	 * @return The list of filtered classrooms.
	 */
	List<Classroom> filterByGroup(Group group, List<Classroom> classrooms);
}

package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.List;

import business.alg.greed.logic.filters.ClassroomFilter;
import business.alg.greed.model.Assignment;
import business.problem.Classroom;

public class GreedyAlgorithm {
	
	List<ClassroomFilter> classroomFilters;
	List<Classroom> classrooms;
	
	public GreedyAlgorithm(
			List<ClassroomFilter> classroomFilters,
			List<Classroom> classrooms
	) {
		this.classroomFilters = new ArrayList<ClassroomFilter>(classroomFilters);
		this.classrooms = new ArrayList<Classroom>(classrooms);
	}

	public List<Assignment> greedyAlgorithm(List<Assignment> assignments) {
		List<Assignment> result, repairs;
		result = preprocess(assignments);
		repairs = new ArrayList<Assignment>();
		for (Assignment a : result) {
			if (!a.isAssigned()) {
				Classroom c = bestClassroomFor(a);
				if (c != null) {
					a.setClassroom(c);
				} else {
					repairs.add(a);
				}
			}
		}
		// TODO: Repair assignments
		return result;
	}

	private List<Assignment> preprocess(List<Assignment> assignments) {
		return new ArrayList<Assignment>(assignments);
	}

	private Classroom bestClassroomFor(Assignment a) {
		return null;
	}

}

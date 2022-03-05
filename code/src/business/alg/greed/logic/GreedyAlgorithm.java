package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.alg.greed.logic.filters.ClassroomFilter;
import business.alg.greed.model.Assignment;
import business.problem.Classroom;
import business.problem.Group;

public class GreedyAlgorithm {
	
	private List<ClassroomFilter> classroomFilters;
	private List<Classroom> classrooms;
	private Map<String, Classroom> assignedClassrooms;
	
	public GreedyAlgorithm(
			List<ClassroomFilter> classroomFilters,
			List<Classroom> classrooms
	) {
		this.classroomFilters = new ArrayList<ClassroomFilter>(classroomFilters);
		this.classrooms = new ArrayList<Classroom>(classrooms);
		this.assignedClassrooms = new HashMap<String, Classroom>();
	}

	public List<Assignment> greedyAlgorithm(List<Assignment> assignments) {
		List<Assignment> result, repairs;
		result = preprocess(assignments);
		repairs = new ArrayList<Assignment>();
		for (Assignment a : result) {
			List<Assignment> currAssign = new ArrayList<Assignment>(result);
			if (!a.isAssigned()) {
				Classroom c = bestClassroomFor(a, currAssign);
				if (c != null) {
					a.setClassroom(c);
					assignedClassrooms.put(c.getCode(), c);
				} else {
					repairs.add(a);
				}
			}
		}
		// TODO: Repair assignments
		return result;
	}

	private List<Assignment> preprocess(List<Assignment> assignments) {
		assignedClassrooms.clear();
		return new ArrayList<Assignment>(assignments);
	}

	private Classroom bestClassroomFor(Assignment a, List<Assignment> currAssign) {
		List<Classroom> filteredClassrooms = new ArrayList<Classroom>(classrooms);
		Classroom selected = null;
		
		for (ClassroomFilter cf : classroomFilters) {
			filteredClassrooms = cf.filterByGroup(a.getGroup(), filteredClassrooms);
		}
		
		boolean stop = false;
		while (filteredClassrooms.size() > 0 || stop) {
			selected = filteredClassrooms.get(0);
			filteredClassrooms.remove(selected);
			if (!assignedClassrooms.containsKey(selected.getCode())) {
				stop = true;
			} else {
				if (!collisionsExistFor(a.getGroup(), currAssign, selected))
					stop = true;
			}
		}

		return selected;
	}

	private boolean collisionsExistFor(Group g, List<Assignment> currAssign, Classroom c) {
		List<Group> filteredGroups = currAssign
				.stream()
				.filter(a -> a.getClassroom().getCode().equals(c.getCode()))
				.map(a -> a.getGroup())
				.collect(Collectors.toList());
		
		return filteredGroups.stream().anyMatch(other -> g.collidesWith(other));
	}

}

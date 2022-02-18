package business.alg;

import java.util.ArrayList;
import java.util.List;

import business.entities.Classroom;
import business.entities.alg.Assignment;

public class GreedyAlgorithm {

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

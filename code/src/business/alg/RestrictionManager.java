package business.alg;

import java.util.ArrayList;
import java.util.List;

import business.entities.Classroom;
import business.entities.Group;
import business.entities.alg.Assignment;
import business.entities.alg.restrictions.post.FitnessRestriction;
import business.entities.alg.restrictions.pre.AssignmentRestriction;

public class RestrictionManager {

	private List<FitnessRestriction> fitnessRestrictions;
	private List<AssignmentRestriction> assignmentRestrictions;

	public RestrictionManager() {
		this.fitnessRestrictions = new ArrayList<FitnessRestriction>();
		this.assignmentRestrictions = new ArrayList<AssignmentRestriction>();
	}

	public double formula(List<Assignment> assignments) {
		double result = 0.0;
		for (FitnessRestriction r : fitnessRestrictions) {
			result += r.getWeightedValue(assignments);
		}
		return result;
	}

	public List<Classroom> availableClassroomsFor(Group group, List<Classroom> classrooms) {
		return null;
	}

	public List<FitnessRestriction> getFitnessRestrictions() {
		return new ArrayList<FitnessRestriction>(fitnessRestrictions);
	}

	public List<AssignmentRestriction> getAssignmentRestrictions() {
		return new ArrayList<AssignmentRestriction>(assignmentRestrictions);
	}

	public void addFitnessRestriction(FitnessRestriction restriction) {
		this.fitnessRestrictions.add(restriction);
	}

	public void addAssignmentRestriction(AssignmentRestriction restriction) {
		this.assignmentRestrictions.add(restriction);
	}

}

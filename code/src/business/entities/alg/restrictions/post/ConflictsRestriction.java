package business.entities.alg.restrictions.post;

import java.util.List;

import business.entities.alg.Assignment;

public class ConflictsRestriction extends AbstractFitnessRestriction {

	public ConflictsRestriction(double weight) {
		super(weight);
	}

	public double getWeightedValue(List<Assignment> assignments) {
		return this.getWeight() * getNumberOfConflicts(assignments);
	}

	private int getNumberOfConflicts(List<Assignment> assignments) {
		return 0;
	}

}

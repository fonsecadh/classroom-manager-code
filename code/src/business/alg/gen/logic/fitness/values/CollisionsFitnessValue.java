package business.alg.gen.logic.fitness.values;

import java.util.List;

import business.alg.greed.model.Assignment;

public class CollisionsFitnessValue extends AbstractFitnessValue {

	public CollisionsFitnessValue(double weight) {
		super(weight);
	}

	@Override
	public double getValue(List<Assignment> assignments) {
		double nCollisions = assignments.stream().filter(a -> !a.isAssigned()).count();
		double nAssignments = assignments.size();
		return 100 - (nCollisions * 100 / nAssignments);
	}

}

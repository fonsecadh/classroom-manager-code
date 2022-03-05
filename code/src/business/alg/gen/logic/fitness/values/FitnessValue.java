package business.alg.gen.logic.fitness.values;

import java.util.List;

import business.alg.greed.model.Assignment;

public interface FitnessValue {

	double getWeight();
	double getValue(List<Assignment> assignments);

}

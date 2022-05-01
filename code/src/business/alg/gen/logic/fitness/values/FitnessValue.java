package business.alg.gen.logic.fitness.values;

import java.util.Map;

import business.alg.greed.model.Assignment;

public interface FitnessValue {

	double getWeight();

	double getValue(Map<String, Assignment> assignments);

}

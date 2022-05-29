package business.alg.gen.logic.fitness.values;

import java.util.Map;

import business.alg.greed.model.Assignment;

public abstract class AbstractFitnessValue implements FitnessValue {

	private double weight;

	public AbstractFitnessValue(double weight) {
		this.weight = weight;
	}

	@Override
	public double getWeight()
	{
		return weight;
	}

	@Override
	public abstract double getValue(Map<String, Assignment> assignments);

}

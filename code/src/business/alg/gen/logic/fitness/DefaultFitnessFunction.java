package business.alg.gen.logic.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import business.alg.gen.logic.fitness.values.FitnessValue;
import business.alg.gen.model.Individual;
import business.alg.greed.logic.Decoder;
import business.alg.greed.logic.GreedyAlgorithm;
import business.alg.greed.model.Assignment;

public class DefaultFitnessFunction implements FitnessFunction {
	private Decoder decoder;
	private GreedyAlgorithm greedyAlgo;
	private List<FitnessValue> fnValues;

	public DefaultFitnessFunction(Decoder decoder,
			GreedyAlgorithm greedyAlgorithm,
			List<FitnessValue> fitnessValues) {
		this.decoder = decoder;
		this.greedyAlgo = greedyAlgorithm;
		this.fnValues = new ArrayList<FitnessValue>(fitnessValues);
	}

	@Override
	public double fitnessFunction(Individual individual)
	{
		List<Assignment> decoded = decoder.decode(individual);
		Map<String, Assignment> resulting = greedyAlgo
				.greedyAlgorithm(decoded);
		return formula(resulting);
	}

	private double formula(Map<String, Assignment> resulting)
	{
		double result = 0.0;
		for (FitnessValue fnVal : fnValues) {
			if (fnVal.getWeight() > 0)
				result += fnVal.getWeight()
						* fnVal.getValue(resulting);
		}
		return result;
	}
}

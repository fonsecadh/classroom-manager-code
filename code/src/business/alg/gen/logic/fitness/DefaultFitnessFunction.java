package business.alg.gen.logic.fitness;

import java.util.List;

import business.alg.gen.model.Individual;
import business.alg.greed.logic.Decoder;
import business.alg.greed.logic.GreedyAlgorithm;
import business.alg.greed.model.Assignment;

public class DefaultFitnessFunction implements FitnessFunction {

	private Decoder decoder;
	private GreedyAlgorithm greedyAlgo;
	
	public DefaultFitnessFunction(Decoder decoder, GreedyAlgorithm greedyAlgorithm) {
		this.decoder = decoder;
		this.greedyAlgo = greedyAlgorithm;
	}

	@Override
	public double fitnessFunction(Individual individual) {
		List<Assignment> decoded, resulting;  
		decoded = decoder.decode(individual);
		resulting = greedyAlgo.greedyAlgorithm(decoded);
		return formula(resulting);
	}

	private double formula(List<Assignment> resulting) {
		return 0;
	}

}

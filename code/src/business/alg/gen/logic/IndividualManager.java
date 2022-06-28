package business.alg.gen.logic;

import java.util.ArrayList;
import java.util.List;

import business.alg.gen.model.Individual;
import business.alg.gen.utils.GenAlgoUtils;

public class IndividualManager {
	private List<String> groupCodes;

	public IndividualManager(List<String> groupCodes) {
		this.groupCodes = new ArrayList<String>(groupCodes);
	}

	/**
	 * Creates an individual with a random representation chromosome.
	 * 
	 * @return The randomly generated individual.
	 */
	public Individual generateRandomIndividual()
	{
		return GenAlgoUtils.generateRandomIndividual(groupCodes);
	}
}

package business.alg.gen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import business.alg.gen.model.Individual;

public class GenAlgoUtils {
	public static Individual generateRandomIndividual(
			List<String> groupCodes)
	{
		List<String> individualRep = new ArrayList<String>(groupCodes);
		Collections.shuffle(individualRep);
		return new Individual(individualRep);
	}
}

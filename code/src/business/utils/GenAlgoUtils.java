package business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import business.entities.alg.Individual;

public class GenAlgoUtils {
	public static Individual generateRandomIndividual(int nElements, int nValues) {
		List<Integer> individualRep = new ArrayList<Integer>();
		for (int i = 0; i < nElements; i++) {
			individualRep.add(new Random().nextInt(nValues));
		}
		return new Individual(individualRep);
	}
}

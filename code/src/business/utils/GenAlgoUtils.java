package business.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import business.entities.alg.Individual;

public class GenAlgoUtils {

	public static Individual generateRandomIndividual(int individualLength) {
		List<Integer> individualRep = new ArrayList<Integer>();
		for (int i = 0; i < individualLength; i++) {
			individualRep.add(i);
		}
		Collections.shuffle(individualRep);
		return new Individual(individualRep);
	}

}

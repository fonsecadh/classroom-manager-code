package business.alg.gen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import business.alg.gen.model.Individual;

public class GenAlgoUtils {

	public static Individual generateRandomIndividual(int individualLength) {
		List<String> individualRep = new ArrayList<String>();
		for (int i = 0; i < individualLength; i++) {
			individualRep.add(String.valueOf(i));
		}
		Collections.shuffle(individualRep);
		return new Individual(individualRep);
	}

}

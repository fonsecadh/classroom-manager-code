package business.entities.alg;

import java.util.ArrayList;
import java.util.List;

public class Individual {
	/**
	 * The representation of the individual.
	 * 
	 * Each position of the list represents a group identifier.
	 */
	private List<Integer> representation;

	public Individual(List<Integer> representation) {
		this.representation = representation;
	}

	public List<Integer> getRepresentation() {
		return new ArrayList<Integer>(representation);
	}
}

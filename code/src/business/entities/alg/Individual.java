package business.entities.alg;

import java.util.ArrayList;
import java.util.List;

public class Individual {

	private List<Integer> representation;

	/**
	 * Creates an individual given its representation.
	 * 
	 * @param representation
	 * 
	 *                       The representation of the individual. An individual is
	 *                       a list of integers acting as the group identifiers in a
	 *                       specific order.
	 */
	public Individual(List<Integer> representation) {
		this.representation = representation;
	}

	public List<Integer> getRepresentation() {
		return new ArrayList<Integer>(representation);
	}
}

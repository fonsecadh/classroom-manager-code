package business.alg.gen.model;

import java.util.ArrayList;
import java.util.List;

import business.problem.Group;

/**
 * Models an individual. An individual is a list of integers, each integer
 * corresponding to the identifier of a group ({@link Group}).
 * 
 * @author Hugo Fonseca Díaz
 * 
 */
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

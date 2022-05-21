package business.alg.gen.model;

import java.util.ArrayList;
import java.util.List;

import business.problem.model.Group;

/**
 * Models an individual. An individual is a list of strings, each strings
 * corresponding to the identifier of a group ({@link Group}).
 * 
 * @author Hugo Fonseca Díaz
 * 
 */
public class Individual {

	private List<String> representation;

	/**
	 * Creates an individual given its representation.
	 * 
	 * @param representation
	 * 
	 *                       The representation of the individual. An individual is
	 *                       a list of strings acting as the group identifiers in a
	 *                       specific order.
	 */
	public Individual(List<String> representation) {
		this.representation = representation;
	}

	public List<String> getRepresentation() {
		return new ArrayList<String>(representation);
	}

}

package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.gen.model.Individual;
import business.alg.greed.model.Assignment;

/**
 * Models the decoder for the individual ({@link Individual}) chromosomes.
 * 
 * @author Hugo Fonseca Díaz
 *
 */
public class Decoder {
	private Map<String, Assignment> masterAssignments;

	public Decoder() {
		this.masterAssignments = new HashMap<String, Assignment>();
	}

	public void putMasterAssignment(String groupId, Assignment a)
	{
		this.masterAssignments.put(groupId, a);
	}

	/**
	 * Retuns a list of assignments ({@link Assignment}) in the order
	 * specified by the individual's ({@link Individual}) vector chromosome.
	 * 
	 * @param i The given individual.
	 * @return A list of assignment in the order specified by its
	 *         chromosome.
	 */
	public List<Assignment> decode(Individual i)
	{
		List<Assignment> decodedRep = new ArrayList<Assignment>();
		List<String> rep = i.getRepresentation();
		for (String groupId : rep) {
			decodedRep.add(new Assignment(
					masterAssignments.get(groupId)));
		}
		return decodedRep;
	}
}

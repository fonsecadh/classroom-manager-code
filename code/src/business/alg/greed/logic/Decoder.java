package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.gen.model.Individual;
import business.alg.greed.model.Assignment;

public class Decoder {

	private Map<Integer, Assignment> masterAssignments;

	public Decoder() {
		this.masterAssignments = new HashMap<Integer, Assignment>();
	}

	public void putMasterAssignment(int groupId, Assignment a) {
		this.masterAssignments.put(groupId, a);
	}

	public List<Assignment> decode(Individual i) {
		List<Assignment> decodedRep = new ArrayList<Assignment>();
		List<Integer> rep = i.getRepresentation();
		for (Integer groupId : rep) {
			decodedRep.add(masterAssignments.get(groupId));
		}
		return decodedRep;
	}

	public Map<Integer, Assignment> decodeAsMap(Individual i) {
		List<Assignment> l = decode(i);
		Map<Integer, Assignment> m = new HashMap<Integer, Assignment>();
		for (Assignment a : l) {
			m.put(a.getGroup().getId(), a);
		}
		return m;
	}

}

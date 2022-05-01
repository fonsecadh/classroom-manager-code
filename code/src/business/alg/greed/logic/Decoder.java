package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.gen.model.Individual;
import business.alg.greed.model.Assignment;

public class Decoder {

	private Map<String, Assignment> masterAssignments;

	public Decoder() {
		this.masterAssignments = new HashMap<String, Assignment>();
	}

	public void putMasterAssignment(String groupId, Assignment a) {
		this.masterAssignments.put(groupId, a);
	}

	public List<Assignment> decode(Individual i) {
		List<Assignment> decodedRep = new ArrayList<Assignment>();
		List<String> rep = i.getRepresentation();
		for (String groupId : rep) {
			decodedRep.add(new Assignment(masterAssignments.get(groupId)));
		}
		return decodedRep;
	}

	public Map<String, Assignment> decodeAsMap(Individual i) {
		List<Assignment> l = decode(i);
		Map<String, Assignment> m = new HashMap<String, Assignment>();
		for (Assignment a : l) {
			m.put(a.getGroup().getCode(), new Assignment(a));
		}
		return m;
	}

}

package business.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.entities.alg.Assignment;
import business.entities.alg.Individual;

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

}

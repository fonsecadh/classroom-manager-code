package business.entities.alg;

import java.util.List;

public class Individual {
	/**
	 * The representation of the individual. 
	 * 
	 * In our problem, each position of the list represents 
	 * the group id and each value in that position represents 
	 * the classroom associated to that group.
	 * 
	 * representation[groupId] == classroomId
	 */
	private List<Integer> representation;

	public Individual(List<Integer> representation) {
		this.representation = representation;
	}
}

package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import business.alg.greed.logic.filters.ClassroomFilterManager;
import business.alg.greed.model.Assignment;
import business.problem.Classroom;
import business.problem.Group;

/**
 * Models a greedy algorithm and its functions.
 * 
 * @author Hugo Fonseca Díaz
 *
 */
public class GreedyAlgorithm {

	/**
	 * Map with the groups assigned to each classroom. <br>
	 * <br>
	 * Key: Classroom ID. <br>
	 * Value: Set of groups assigned to that classroom.
	 */
	private Map<Integer, Set<Group>> assignedGroupsToClassrooms;
	private ClassroomFilterManager cfm;

	/**
	 * Creates a Greedy Algorithm.
	 * 
	 * @param classroomFilterManager
	 * 
	 *                               The manager ({@link ClassroomFilterManager})
	 *                               for filtering the classrooms for each group
	 *                               ({@link Group})
	 */
	public GreedyAlgorithm(ClassroomFilterManager classroomFilterManager) {
		this.cfm = classroomFilterManager;
		this.assignedGroupsToClassrooms = new HashMap<Integer, Set<Group>>();
	}

	/**
	 * Given a list of assignments ({@link Assignment}), the greedy algorithm
	 * assigns the best classroom ({@link Classroom}) to each group ({@link Group})
	 * such that the classroom fulfills the filters and there are no scheduling
	 * collisions.
	 * 
	 * @param assignments Initial list of assignments.
	 * @return Final list with the calculated assignments of classrooms to each
	 *         group.
	 */
	public List<Assignment> greedyAlgorithm(List<Assignment> assignments) {
		List<Assignment> result, repairs;
		result = preprocess(assignments);
		repairs = new ArrayList<Assignment>();

		for (Assignment a : result) {
			if (!a.isAssigned()) {
				Classroom c = bestClassroomFor(a);
				if (c != null) {
					a.setClassroom(c);
					Set<Group> gSet = assignedGroupsToClassrooms.get(c.getId());
					if (gSet == null)
						gSet = new HashSet<Group>();
					gSet.add(a.getGroup());
					assignedGroupsToClassrooms.put(c.getId(), gSet);
				} else {
					repairs.add(a);
				}
			}
		}

		// TODO: Repair assignments

		return result;
	}

	private List<Assignment> preprocess(List<Assignment> assignments) {
		assignedGroupsToClassrooms.clear();
		return new ArrayList<Assignment>(assignments);
	}

	private Classroom bestClassroomFor(Assignment a) {
		Classroom selected = null;
		List<Classroom> filteredClassrooms = cfm.filterClassroomsFor(a.getGroup());

		boolean stop = false;
		while (filteredClassrooms.size() > 0 || stop) {
			selected = filteredClassrooms.get(0);
			filteredClassrooms.remove(selected);
			if (!collisionsExistFor(a.getGroup(), selected))
				stop = true;
		}

		return selected;
	}

	private boolean collisionsExistFor(Group g, Classroom c) {
		Set<Group> gSet = assignedGroupsToClassrooms.get(c.getId());

		if (gSet == null || gSet.size() == 0)
			return false;

		return gSet.stream().anyMatch(other -> g.collidesWith(other));
	}

}

package business.alg.greed.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import business.alg.greed.logic.collisions.CollisionManager;
import business.alg.greed.logic.filters.ClassroomFilterManager;
import business.alg.greed.model.Assignment;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.Subject;
import business.problem.utils.ProblemUtils;

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
	 * Value: Set of groups ({@link Group}) assigned to that classroom
	 * ({@link Classroom}).
	 */
	private Map<String, Set<Group>> assignedGroupsToClassrooms;
	/**
	 * Map with the subject for each group. <br>
	 * <br>
	 * Key: Group ID. <br>
	 * Value: Subject ({@link Subject}).
	 */
	private Map<String, Subject> groupSubjectMap;
	/**
	 * Map with the assignments related to each subject. <br>
	 * <br>
	 * Key: Subject ID. <br>
	 * Value: Set of assignments ({@link Assignment}) assigned to that
	 * subject ({@link Subject}).
	 */
	private Map<String, List<Assignment>> subjectAssignmentsMap;
	/**
	 * Map with the assignments related to each academic course. <br>
	 * <br>
	 * Key: Course. <br>
	 * Value: Set of assignments ({@link Assignment}) related to that
	 * course.
	 */
	private Map<String, List<Assignment>> courseAssignmentsMap;
	private List<Subject> subjectList;
	private ClassroomFilterManager cfm;
	private CollisionManager cm;
	private boolean performRepairs;
	private boolean sameClassroomBias;

	/**
	 * Creates a Greedy Algorithm.
	 * 
	 * @param classroomFilterManager
	 * 
	 *                               The manager
	 *                               ({@link ClassroomFilterManager}) for
	 *                               filtering the classrooms for each group
	 *                               ({@link Group})
	 * @param collisionManager
	 * 
	 *                               The manager ({@link CollisionManager})
	 *                               for checking if two groups
	 *                               ({@link Group}) collide.
	 * @param groupSubjectMap
	 * 
	 *                               The map that for each group
	 *                               ({@link Group}}) indicates which
	 *                               subject ({@link Subject}) it belongs
	 *                               to.
	 * @param subjectList
	 * 
	 *                               List of all the subjects
	 *                               ({@link Subject}).
	 * @param performRepairs
	 * 
	 *                               If the greedy algorithm should execute
	 *                               the repairing process.
	 * 
	 * @param sameClassroomBias
	 * 
	 *                               If the greedy algorithm should assign
	 *                               the same classroom to the lab groups of
	 *                               a subject or to the theory groups of a
	 *                               course with the same name.
	 */
	public GreedyAlgorithm(ClassroomFilterManager classroomFilterManager,
			CollisionManager collisionManager,
			Map<String, Subject> groupSubjectMap,
			List<Subject> subjectList, boolean performRepairs,
			boolean sameClassroomBias) {
		this.cfm = classroomFilterManager;
		this.cm = collisionManager;
		this.groupSubjectMap = new HashMap<String, Subject>(
				groupSubjectMap);
		this.subjectList = new ArrayList<Subject>(subjectList);

		this.subjectAssignmentsMap = new HashMap<String, List<Assignment>>();
		this.courseAssignmentsMap = new HashMap<String, List<Assignment>>();
		this.assignedGroupsToClassrooms = new HashMap<String, Set<Group>>();
		this.performRepairs = performRepairs;
		this.sameClassroomBias = sameClassroomBias;
	}

	/**
	 * Given a list of assignments ({@link Assignment}), the greedy
	 * algorithm assigns the best classroom ({@link Classroom}) to each
	 * group ({@link Group}) such that the classroom fulfills the filters
	 * and there are no scheduling collisions.
	 * 
	 * @param assignments Initial list of assignments.
	 * @return Final map with the calculated assignments of classrooms to
	 *         each group.
	 */
	public Map<String, Assignment> greedyAlgorithm(
			List<Assignment> assignments)
	{
		clearMaps();

		Map<String, Assignment> result = assignments.stream()
				.collect(Collectors.toMap(
						a -> a.getGroup().getCode(),
						a -> a));

		List<Assignment> repairs = new ArrayList<Assignment>();
		preprocess(result, assignments);
		createMaps(result);

		// The assignments are made
		for (Assignment aux : assignments) {
			Assignment a = result.get(aux.getGroup().getCode());
			if (!a.isAssigned()) {
				Classroom c = bestClassroomFor(a);
				if (c != null) {
					assignClassroomToGroup(c, a, result);
					if (sameClassroomBias) {
						assignClassroomToOtherGroups(c,
								a, result);
					}
				} else {
					repairs.add(a);
				}
			}
		}
		if (!performRepairs) {
			// The repairing process is not executed
			return new HashMap<String, Assignment>(result);
		}
		// Repairing process
		for (Assignment a : repairs) {
			List<Classroom> filteredClassrooms = cfm
					.filterClassroomsFor(a.getGroup());

			classroomloop: for (Classroom c : filteredClassrooms) {
				List<Group> collisions = collisionsFor(
						a.getGroup(), c);
				if (collisions.size() > 1) {
					// Assignment could not be repaired
					break classroomloop;
				}
				if (collisions.size() == 0) {
					// TODO: BUG - IGNORE UNTIL FIXING
					break classroomloop;
				}
				Group g = collisions.get(0);

				Assignment a2 = result.get(g.getCode());

				removeClassroomFromGroup(c, a2, result);
				assignClassroomToGroup(c, a, result);

				Classroom c2 = bestClassroomFor(a2);
				if (c2 != null) {
					// Assignment repaired
					assignClassroomToGroup(c2, a2, result);
				} else {
					// Assignment could not be repaired
					removeClassroomFromGroup(c, a, result);
					assignClassroomToGroup(c, a2, result);
				}
			}
		}
		return new HashMap<String, Assignment>(result);
	}

	private void preprocess(Map<String, Assignment> result,
			List<Assignment> assignments)
	{
		for (Assignment aux : assignments) {
			Assignment a = result.get(aux.getGroup().getCode());
			if (!a.isAssigned()) {
				List<Classroom> filteredClassrooms = cfm
						.filterClassroomsFor(
								a.getGroup());
				if (filteredClassrooms.size() == 1) {
					Classroom c = bestClassroomFor(a);
					if (c != null) {
						assignClassroomToGroup(c, a,
								result);
					}
				}
			} else {
				Classroom c = a.getClassroom();
				Set<Group> gSet = assignedGroupsToClassrooms
						.get(c.getCode());
				if (gSet == null)
					gSet = new HashSet<Group>();
				gSet.add(a.getGroup());
				assignedGroupsToClassrooms.put(c.getCode(),
						gSet);
			}
		}
	}

	private void clearMaps()
	{
		subjectAssignmentsMap.clear();
		courseAssignmentsMap.clear();
		assignedGroupsToClassrooms.clear();
	}

	private void createMaps(Map<String, Assignment> aMap)
	{
		for (Subject s : subjectList) {
			// (Subject - List of assignments) map
			List<Assignment> saList = new ArrayList<Assignment>();
			for (Group g : s.getGroups()) {
				saList.add(aMap.get(g.getCode()));

				// (Course - List of assignments) map
				List<Assignment> caList = this.courseAssignmentsMap
						.get(s.getCourse());
				if (caList == null)
					caList = new ArrayList<Assignment>();
				caList.add(aMap.get(g.getCode()));
				this.courseAssignmentsMap.put(s.getCourse(),
						caList);
			}
			this.subjectAssignmentsMap.put(s.getCode(), saList);
		}
	}

	/**
	 * Obtains the best classroom for the given assignment, or {@code null}
	 * if no classroom is found.
	 * 
	 * @param a The assignment.
	 * @return The best classroom for the given assignment.
	 */
	private Classroom bestClassroomFor(Assignment a)
	{
		Classroom selected = null;
		List<Classroom> filteredClassrooms = cfm
				.filterClassroomsFor(a.getGroup());

		selectionloop: for (int i = 0; i < filteredClassrooms
				.size(); i++) {
			selected = filteredClassrooms.get(i);
			if (!collisionsExistFor(a.getGroup(), selected))
				break selectionloop;
			selected = null;
		}

		return selected;
	}

	private boolean collisionsExistFor(Group g, Classroom c)
	{
		Set<Group> gSet = assignedGroupsToClassrooms.get(c.getCode());

		if (gSet == null || gSet.size() == 0)
			return false;

		return gSet.stream()
				.anyMatch(other -> cm.groupsCollide(g, other));
	}

	private List<Group> collisionsFor(Group g, Classroom c)
	{
		Set<Group> gSet = assignedGroupsToClassrooms.get(c.getCode());

		if (gSet == null || gSet.size() == 0)
			return new ArrayList<Group>();

		return gSet.stream().filter(other -> cm.groupsCollide(g, other))
				.collect(Collectors.toList());
	}

	private void assignClassroomToGroup(Classroom c, Assignment a,
			Map<String, Assignment> result)
	{
		List<Classroom> filteredClassrooms = cfm
				.filterClassroomsFor(a.getGroup());

		boolean validClassroom = filteredClassrooms.contains(c);
		boolean noCollisions = !collisionsExistFor(a.getGroup(), c);
		if (validClassroom && noCollisions) {
			a.setClassroom(c);
			result.put(a.getGroup().getCode(), a);
			Set<Group> gSet = assignedGroupsToClassrooms
					.get(c.getCode());
			if (gSet == null)
				gSet = new HashSet<Group>();
			gSet.add(a.getGroup());
			assignedGroupsToClassrooms.put(c.getCode(), gSet);
		}
	}

	private void removeClassroomFromGroup(Classroom c, Assignment a,
			Map<String, Assignment> result)
	{
		a.setClassroom(null);
		result.put(a.getGroup().getCode(), a);
		Set<Group> gSet = assignedGroupsToClassrooms.get(c.getCode());
		if (gSet == null)
			gSet = new HashSet<Group>();
		gSet.remove(a.getGroup());
		assignedGroupsToClassrooms.put(c.getCode(), gSet);
	}

	private void assignClassroomToOtherGroups(Classroom c, Assignment a,
			Map<String, Assignment> result)
	{
		if (ProblemUtils.isLabGroup(a.getGroup())) {
			/*
			 * If the group is a lab group, try to assign the same
			 * classroom to the rest of the labs for that subject
			 */
			assignClassroomToOtherLabs(c, a, result);
		} else {
			/*
			 * If the group is a theory group, try to assign the
			 * same classroom for the rest of the subjects that
			 * share that group number.
			 */
			assignClassroomToOtherTheories(c, a, result);
		}
	}

	private void assignClassroomToOtherLabs(Classroom c, Assignment a,
			Map<String, Assignment> result)
	{
		Subject subject = this.groupSubjectMap
				.get(a.getGroup().getCode());
		List<Assignment> aList = this.subjectAssignmentsMap
				.get(subject.getCode());
		for (Assignment assignment : aList) {
			if (!assignment.isAssigned() && ProblemUtils
					.isLabGroup(assignment.getGroup())) {
				assignClassroomToGroup(c, assignment, result);
			}
		}
	}

	private void assignClassroomToOtherTheories(Classroom c, Assignment a,
			Map<String, Assignment> result)
	{
		Subject subject = this.groupSubjectMap
				.get(a.getGroup().getCode());
		List<Assignment> aList = this.courseAssignmentsMap
				.get(subject.getCourse());
		for (Assignment assignment : aList) {
			if (!assignment.isAssigned()
					&& ProblemUtils.isTheoryGroup(
							assignment.getGroup())
					&& assignment.getGroup()
							.sameTypeAndGroupNameAs(
									a.getGroup())) {
				assignClassroomToGroup(c, assignment, result);
			}
		}
	}
}

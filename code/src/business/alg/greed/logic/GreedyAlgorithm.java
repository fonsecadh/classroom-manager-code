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
	 * Value: Set of assignments ({@link Assignment}) assigned to that subject
	 * ({@link Subject}).
	 */
	private Map<String, List<Assignment>> subjectAssignmentsMap;

	/**
	 * Map with the assignments related to each academic course. <br>
	 * <br>
	 * Key: Course. <br>
	 * Value: Set of assignments ({@link Assignment}) related to that course.
	 */
	private Map<String, List<Assignment>> courseAssignmentsMap;

	private List<Subject> subjectList;

	private ClassroomFilterManager cfm;
	private CollisionManager cm;

	/**
	 * Creates a Greedy Algorithm.
	 * 
	 * @param classroomFilterManager
	 * 
	 *                               The manager ({@link ClassroomFilterManager})
	 *                               for filtering the classrooms for each group
	 *                               ({@link Group})
	 * @param collisionManager
	 * 
	 *                               The manager ({@link CollisionManager}) for
	 *                               checking if two groups ({@link Group}) collide.
	 * @param groupSubjectMap
	 * 
	 *                               The map that for each group ({@link Group}})
	 *                               indicates which subject ({@link Subject}) it
	 *                               belongs to.
	 * @param subjectList
	 * 
	 *                               List of all the subjects ({@link Subject}).
	 */
	public GreedyAlgorithm(ClassroomFilterManager classroomFilterManager, CollisionManager collisionManager,
			Map<String, Subject> groupSubjectMap, List<Subject> subjectList) {

		this.cfm = classroomFilterManager;
		this.cm = collisionManager;
		this.groupSubjectMap = new HashMap<String, Subject>(groupSubjectMap);
		this.subjectList = new ArrayList<Subject>(subjectList);

		this.subjectAssignmentsMap = new HashMap<String, List<Assignment>>();
		this.courseAssignmentsMap = new HashMap<String, List<Assignment>>();
		this.assignedGroupsToClassrooms = new HashMap<String, Set<Group>>();

	}

	/**
	 * Given a list of assignments ({@link Assignment}), the greedy algorithm
	 * assigns the best classroom ({@link Classroom}) to each group ({@link Group})
	 * such that the classroom fulfills the filters and there are no scheduling
	 * collisions.
	 * 
	 * @param assignments Initial list of assignments.
	 * @return Final map with the calculated assignments of classrooms to each
	 *         group.
	 */
	public Map<String, Assignment> greedyAlgorithm(List<Assignment> assignments) {

		List<Assignment> preproc, repairs;
		preproc = preprocess(assignments);
		repairs = new ArrayList<Assignment>();

		Map<String, Assignment> result = preproc.stream()
				.collect(Collectors.toMap(a -> a.getGroup().getCode(), a -> a));

		createMaps(result);

		for (Assignment a : preproc) {

			if (!a.isAssigned()) {

				Classroom c = bestClassroomFor(a);
				if (c != null) {

					assignClassroomToGroup(c, a, result);

					if (ProblemUtils.isLabGroup(a.getGroup())) {

						/*
						 * If the group is a lab group, try to assign the same classroom to the rest of
						 * the labs for that subject
						 */
						assignClassroomToOtherLabs(result, a, c);

					} else {

						/*
						 * If the group is a theory group, try to assign the same classroom for the rest
						 * of the subjects that share that group number.
						 */
						assignClassroomToOtherTheories(result, a, c);

					}

				} else {

					repairs.add(a);

				}
			}
		}

		// TODO: Repair assignments

		return new HashMap<String, Assignment>(result);

	}

	private List<Assignment> preprocess(List<Assignment> assignments) {
		subjectAssignmentsMap.clear();
		courseAssignmentsMap.clear();
		assignedGroupsToClassrooms.clear();
		return new ArrayList<Assignment>(assignments);
	}

	private void createMaps(Map<String, Assignment> aMap) {

		for (Subject s : subjectList) {

			// (Subject - List of assignments) map
			List<Assignment> saList = new ArrayList<Assignment>();

			for (Group g : s.getGroups()) {

				saList.add(aMap.get(g.getCode()));

				// (Course - List of assignments) map
				List<Assignment> caList = this.courseAssignmentsMap.get(s.getCourse());
				if (caList == null)
					caList = new ArrayList<Assignment>();
				caList.add(aMap.get(g.getCode()));
				this.courseAssignmentsMap.put(s.getCourse(), caList);

			}

			this.subjectAssignmentsMap.put(s.getCode(), saList);

		}

	}

	private Classroom bestClassroomFor(Assignment a) {
		Classroom selected = null;
		List<Classroom> filteredClassrooms = cfm.filterClassroomsFor(a.getGroup());

		boolean stop = false;
		while (filteredClassrooms.size() > 0 && !stop) {
			selected = filteredClassrooms.get(0);
			filteredClassrooms.remove(selected);
			if (!collisionsExistFor(a.getGroup(), selected))
				stop = true;
		}

		return selected;
	}

	private boolean collisionsExistFor(Group g, Classroom c) {
		Set<Group> gSet = assignedGroupsToClassrooms.get(c.getCode());

		if (gSet == null || gSet.size() == 0)
			return false;

		return gSet.stream().anyMatch(other -> cm.groupsCollide(g, other));
	}

	private void assignClassroomToGroup(Classroom c, Assignment a, Map<String, Assignment> result) {

		a.setClassroom(c);
		result.put(a.getGroup().getCode(), a);
		Set<Group> gSet = assignedGroupsToClassrooms.get(c.getCode());
		if (gSet == null)
			gSet = new HashSet<Group>();
		gSet.add(a.getGroup());
		assignedGroupsToClassrooms.put(c.getCode(), gSet);

	}

	private void assignClassroomToOtherLabs(Map<String, Assignment> result, Assignment a, Classroom c) {

		Subject subject = this.groupSubjectMap.get(a.getGroup().getCode());
		List<Assignment> aList = this.subjectAssignmentsMap.get(subject.getCode());

		for (Assignment assignment : aList) {

			if (!assignment.isAssigned() && ProblemUtils.isLabGroup(assignment.getGroup())) {

				if (!collisionsExistFor(assignment.getGroup(), c)) {
					assignClassroomToGroup(c, assignment, result);
				}

			}

		}
	}

	private void assignClassroomToOtherTheories(Map<String, Assignment> result, Assignment a, Classroom c) {

		Subject subject = this.groupSubjectMap.get(a.getGroup().getCode());
		List<Assignment> aList = this.courseAssignmentsMap.get(subject.getCourse());

		for (Assignment assignment : aList) {

			if (!assignment.isAssigned() && !ProblemUtils.isLabGroup(assignment.getGroup())
					&& assignment.getGroup().sameGroupNameAs(a.getGroup())) {

				if (!collisionsExistFor(assignment.getGroup(), c)) {
					assignClassroomToGroup(c, assignment, result);
				}

			}

		}

	}

}

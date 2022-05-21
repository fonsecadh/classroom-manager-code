package business.alg.gen.logic.fitness.values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import business.alg.greed.model.Assignment;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.Subject;
import business.problem.utils.ProblemUtils;

public class SharedTheoryFitnessValue extends AbstractFitnessValue {

	private List<Subject> subjects;
	private List<Classroom> theory;

	public SharedTheoryFitnessValue(double weight, List<Subject> subjects, List<Classroom> classrooms) {
		super(weight);
		this.subjects = new ArrayList<Subject>(subjects);
		this.theory = new ArrayList<Classroom>(classrooms).stream().filter(c -> ProblemUtils.isTheoryClassroom(c))
				.collect(Collectors.toList());
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		double value = 0.0;

		List<Classroom> assignedTheoryEn, assignedTheoryEs;
		assignedTheoryEn = new ArrayList<Classroom>();
		assignedTheoryEs = new ArrayList<Classroom>();

		Map<String, List<Assignment>> courseAssignmentsMap = new HashMap<String, List<Assignment>>();
		for (Subject s : subjects) {

			for (Group g : s.getGroups()) {

				List<Assignment> caList = courseAssignmentsMap.get(s.getCourse());
				if (caList == null)
					caList = new ArrayList<Assignment>();
				caList.add(assignments.get(g.getCode()));
				courseAssignmentsMap.put(s.getCourse(), caList);

			}

		}

		Set<String> courses = courseAssignmentsMap.keySet();

		for (String course : courses) {

			double courseValue = 0.0;

			assignedTheoryEn.clear();
			assignedTheoryEs.clear();

			List<String> groupNames = courseAssignmentsMap.get(course).stream()
					.map(a -> a.getGroup().getNameFromCode()).collect(Collectors.toList());

			for (String groupName : groupNames) {

				List<Assignment> assignmentsForGroupName = courseAssignmentsMap.get(course).stream()
						.filter(a -> a.getGroup().sameGroupNameAs(groupName)).collect(Collectors.toList());

				for (Assignment a : assignmentsForGroupName) {

					Group g = a.getGroup();

					if (ProblemUtils.isTheoryGroup(g)) {
						if (assignments.get(g.getCode()) != null) {
							if (ProblemUtils.isEnglishGroup(g)) {
								assignedTheoryEn.add(assignments.get(g.getCode()).getClassroom());
							} else {
								assignedTheoryEs.add(assignments.get(g.getCode()).getClassroom());
							}
						}
					}

				}

				Set<Classroom> theoSetEn, theoSetEs;

				int langCounter = 0;
				int uniqueTheoEn = 0, uniqueTheoEs = 0;
				double groupNameValue = 0.0, enValue = 0.0, esValue = 0.0;

				if (assignedTheoryEn.size() > 0) {
					theoSetEn = new HashSet<Classroom>(assignedTheoryEn);
					uniqueTheoEn = theoSetEn.size();
					enValue = 100 - (uniqueTheoEn * 100 / theory.size());
					++langCounter;
				}

				if (assignedTheoryEs.size() > 0) {
					theoSetEs = new HashSet<Classroom>(assignedTheoryEs);
					uniqueTheoEs = theoSetEs.size();
					esValue = 100 - (uniqueTheoEs * 100 / theory.size());
					++langCounter;
				}

				groupNameValue = enValue + esValue;
				if (langCounter > 0)
					groupNameValue = groupNameValue / langCounter;

				courseValue += groupNameValue;

			}

			courseValue = courseValue / groupNames.size();
			value += courseValue;

		}

		value = value / courses.size();

		return value;

	}
}

package business.alg.gen.logic.fitness.values;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import business.alg.greed.model.Assignment;
import business.problem.Classroom;
import business.problem.ClassroomType;
import business.problem.Group;
import business.problem.GroupLanguage;
import business.problem.Subject;

public class SharedTheoryFitnessValue extends AbstractFitnessValue {

	private List<Subject> subjects;
	private List<Classroom> theory;

	public SharedTheoryFitnessValue(double weight, List<Subject> subjects, List<Classroom> classrooms) {
		super(weight);
		this.subjects = new ArrayList<Subject>(subjects);
		this.theory = new ArrayList<Classroom>(classrooms).stream()
				.filter(c -> c.getType().equals(ClassroomType.THEORY)).collect(Collectors.toList());
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		double value = 0.0;

		List<Classroom> assignedTheoryEn, assignedTheoryEs;
		assignedTheoryEn = new ArrayList<Classroom>();
		assignedTheoryEs = new ArrayList<Classroom>();

		for (Subject s : subjects) {

			assignedTheoryEn.clear();
			assignedTheoryEs.clear();

			for (Group g : s.getGroups()) {
				if (g.getClassroomType().equals(ClassroomType.THEORY)) {
					if (assignments.get(g.getCode()) != null) {
						if (g.getGroupLanguage().equals(GroupLanguage.ENGLISH)) {
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
			double subjectValue = 0.0, enValue = 0.0, esValue = 0.0;

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

			subjectValue = enValue + esValue;
			if (langCounter > 0)
				subjectValue = subjectValue / langCounter;

			value += subjectValue;

		}

		value = value / subjects.size();

		return value;

	}
}

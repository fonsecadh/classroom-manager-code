package business.alg.gen.logic.fitness.values;

import java.util.ArrayList;
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

public class SharedLabsFitnessValue extends AbstractFitnessValue {

	private List<Subject> subjects;
	private List<Classroom> labs;

	public SharedLabsFitnessValue(double weight, List<Subject> subjects, List<Classroom> classrooms) {
		super(weight);
		this.subjects = new ArrayList<Subject>(subjects);
		this.labs = new ArrayList<Classroom>(classrooms).stream().filter(c -> ProblemUtils.isLabClassroom(c))
				.collect(Collectors.toList());
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		double value = 0.0;

		List<Classroom> assignedLabsEn, assignedLabsEs;
		assignedLabsEn = new ArrayList<Classroom>();
		assignedLabsEs = new ArrayList<Classroom>();

		for (Subject s : subjects) {

			assignedLabsEn.clear();
			assignedLabsEs.clear();

			for (Group g : s.getGroups()) {
				if (ProblemUtils.isLabGroup(g)) {
					if (assignments.get(g.getCode()) != null) {
						if (ProblemUtils.isEnglishGroup(g)) {
							assignedLabsEn.add(assignments.get(g.getCode()).getClassroom());
						} else {
							assignedLabsEs.add(assignments.get(g.getCode()).getClassroom());
						}
					}
				}
			}

			Set<Classroom> labSetEn, labSetEs;

			int langCounter = 0;
			int uniqueLabsEn = 0, uniqueLabsEs = 0;
			double subjectValue = 0.0, enValue = 0.0, esValue = 0.0;

			if (assignedLabsEn.size() > 0) {
				labSetEn = new HashSet<Classroom>(assignedLabsEn);
				uniqueLabsEn = labSetEn.size();
				enValue = 100 - (uniqueLabsEn * 100 / labs.size());
				++langCounter;
			}

			if (assignedLabsEs.size() > 0) {
				labSetEs = new HashSet<Classroom>(assignedLabsEs);
				uniqueLabsEs = labSetEs.size();
				esValue = 100 - (uniqueLabsEs * 100 / labs.size());
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

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
import business.problem.Subject;

public class SharedLabsFitnessValue extends AbstractFitnessValue {

	private List<Subject> subjects;
	private List<Classroom> labs;

	public SharedLabsFitnessValue(double weight, List<Subject> subjects, List<Classroom> classrooms) {
		super(weight);
		this.subjects = new ArrayList<Subject>(subjects);
		this.labs = new ArrayList<Classroom>(classrooms).stream()
				.filter(c -> c.getType().equals(ClassroomType.LABORATORY)).collect(Collectors.toList());
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		double value = 0.0;

		List<Classroom> assignedLabs = new ArrayList<Classroom>();

		for (Subject s : subjects) {

			assignedLabs.clear();
			for (Group g : s.getGroups()) {
				if (g.getClassroomType().equals(ClassroomType.LABORATORY)) {
					if (assignments.get(g.getCode()) != null) {
						assignedLabs.add(assignments.get(g.getCode()).getClassroom());
					}
				}
			}

			Set<Classroom> labSet = new HashSet<Classroom>(assignedLabs);
			int uniqueLabs = labSet.size();

			double subjectValue = 100 - (uniqueLabs * 100 / labs.size());
			value += subjectValue;

		}

		value = value / subjects.size();

		return value;

	}

}

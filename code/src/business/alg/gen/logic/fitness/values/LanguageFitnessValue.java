package business.alg.gen.logic.fitness.values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import business.alg.greed.model.Assignment;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.Subject;
import business.problem.utils.ProblemUtils;

public class LanguageFitnessValue extends AbstractFitnessValue {

	private List<Subject> subjects;

	public LanguageFitnessValue(double weight, List<Subject> subjects) {
		super(weight);
		this.subjects = new ArrayList<Subject>(subjects);
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		double value = 0.0;

		List<Classroom> enTheory, enLab, esTheory, esLab, theoryIntersec, labIntersec;
		enTheory = new ArrayList<Classroom>();
		enLab = new ArrayList<Classroom>();
		esTheory = new ArrayList<Classroom>();
		esLab = new ArrayList<Classroom>();
		theoryIntersec = new ArrayList<Classroom>();
		labIntersec = new ArrayList<Classroom>();

		for (Subject s : subjects) {
			clearLists(enTheory, enLab, esTheory, esLab, theoryIntersec, labIntersec);
			for (Group g : s.getGroups()) {
				addClassrooms(assignments, enTheory, enLab, esTheory, esLab, g);
			}

			theoryIntersec = intersection(enTheory, esTheory);
			labIntersec = intersection(enLab, esLab);

			double subjectValue, theoryValue = 0.0, labValue = 0.0;
			int langCounter = 0;

			if (enTheory.size() + esTheory.size() > 0) {
				theoryValue = 100 - (theoryIntersec.size() * 100 / (enTheory.size() + esTheory.size()));
				++langCounter;
			}

			if (enLab.size() + esLab.size() > 0) {
				labValue = 100 - (labIntersec.size() * 100 / (enLab.size() + esLab.size()));
				++langCounter;
			}

			subjectValue = theoryValue + labValue;
			if (langCounter > 0)
				subjectValue = subjectValue / langCounter;

			value += subjectValue;
		}

		value = value / subjects.size();
		return value;

	}

	private List<Classroom> intersection(List<Classroom> l1, List<Classroom> l2) {
		List<Classroom> intersection = new ArrayList<Classroom>();

		for (Classroom c1 : l1)
			for (Classroom c2 : l2)
				if (c1.getCode().equalsIgnoreCase(c2.getCode()))
					intersection.add(c1);

		return intersection;

	}

	private void addClassrooms(Map<String, Assignment> assignments, List<Classroom> enTheory, List<Classroom> enLab,
			List<Classroom> esTheory, List<Classroom> esLab, Group g) {

		if (ProblemUtils.isLabGroup(g))
			addClassroom(assignments, enLab, esLab, g);
		else
			addClassroom(assignments, enTheory, esTheory, g);

	}

	private void addClassroom(Map<String, Assignment> assignments, List<Classroom> en, List<Classroom> es, Group g) {

		if (assignments.get(g.getCode()) == null)
			return;

		if (ProblemUtils.isEnglishGroup(g))
			en.add(assignments.get(g.getCode()).getClassroom());
		else
			es.add(assignments.get(g.getCode()).getClassroom());

	}

	private void clearLists(List<Classroom> enTheory, List<Classroom> enLab, List<Classroom> esTheory,
			List<Classroom> esLab, List<Classroom> theoryIntersec, List<Classroom> labIntersec) {

		enTheory.clear();
		enLab.clear();
		esTheory.clear();
		esLab.clear();
		theoryIntersec.clear();
		labIntersec.clear();

	}

}

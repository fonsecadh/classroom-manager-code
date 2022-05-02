package business.alg.gen.logic.fitness.values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.gen.model.Preference;
import business.alg.greed.model.Assignment;
import business.problem.ClassroomType;
import business.problem.Group;
import business.problem.GroupLanguage;
import business.problem.Subject;

public class PreferencesFitnessValue extends AbstractFitnessValue {

	private Map<String, Preference> preferences;
	private List<Subject> subjects;

	// Counters
	private int correctEnglishLabs;
	private int totalEnglishLabs;

	private int correctEnglishTheory;
	private int totalEnglishTheory;

	private int correctSpanishLabs;
	private int totalSpanishLabs;

	private int correctSpanishTheory;
	private int totalSpanishTheory;

	public PreferencesFitnessValue(double weight, Map<String, Preference> preferences, List<Subject> subjects) {
		super(weight);
		this.preferences = new HashMap<String, Preference>(preferences);
		this.subjects = new ArrayList<Subject>(subjects);
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		double value = 0.0;

		for (Subject s : subjects) {

			if (preferences.get(s.getCode()) != null) {

				Preference p = preferences.get(s.getCode());

				totalEnglishLabs = 0;
				correctEnglishLabs = 0;
				totalEnglishTheory = 0;
				correctEnglishTheory = 0;
				totalSpanishLabs = 0;
				correctSpanishLabs = 0;
				totalSpanishTheory = 0;
				correctSpanishTheory = 0;

				for (Group g : s.getGroups()) {

					Assignment a = assignments.get(g.getCode());

					if (a != null && a.getClassroom() != null) {
						increaseCountersForGroup(p, g, a);
					}

				}

				double enLabValue = 0, enTheoryValue = 0, esLabValue = 0, esTheoryValue = 0, groupCounter = 0;

				if (totalEnglishLabs > 0) {
					enLabValue = 100 * correctEnglishLabs / totalEnglishLabs;
					++groupCounter;
				}

				if (totalEnglishTheory > 0) {
					enTheoryValue = 100 * correctEnglishTheory / totalEnglishTheory;
					++groupCounter;
				}

				if (totalSpanishLabs > 0) {
					esLabValue = 100 * correctSpanishLabs / totalSpanishLabs;
					++groupCounter;
				}

				if (totalSpanishTheory > 0) {
					esTheoryValue = 100 * correctSpanishTheory / totalSpanishTheory;
					++groupCounter;
				}

				double sValue = 0.0;

				sValue = enLabValue + enTheoryValue + esLabValue + esTheoryValue;
				if (groupCounter > 0)
					sValue = sValue / groupCounter;

				value += sValue;

			}
		}

		return value;
	}

	private void increaseCountersForGroup(Preference p, Group g, Assignment a) {

		boolean enLang, labType;

		enLang = g.getGroupLanguage().equals(GroupLanguage.ENGLISH);
		labType = g.getClassroomType().equals(ClassroomType.LABORATORY);

		if (enLang) {

			if (labType) {

				if (complies(p.getEnglishLabPreferences(), a.getClassroom().getCode())) {
					++correctEnglishLabs;
				}
				++totalEnglishLabs;

			} else {

				if (complies(p.getEnglishTheoryPreferences(), a.getClassroom().getCode())) {
					++correctEnglishTheory;
				}
				++totalEnglishTheory;

			}

		} else {

			if (labType) {

				if (complies(p.getSpanishLabPreferences(), a.getClassroom().getCode())) {
					++correctSpanishLabs;
				}
				++totalSpanishLabs;

			} else {

				if (complies(p.getSpanishTheoryPreferences(), a.getClassroom().getCode())) {
					++correctSpanishTheory;
				}
				++totalSpanishTheory;

			}

		}
	}

	private boolean complies(List<String> prefs, String classroomCode) {
		boolean hasPrefs, complies;

		hasPrefs = prefs.size() > 0;
		complies = prefs.contains(classroomCode);

		if (!hasPrefs || hasPrefs && complies) {
			return true;
		}

		return false;
	}

}

package business.alg.gen.logic.fitness.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.gen.model.Preference;
import business.alg.gen.model.PreferenceType;
import business.alg.greed.model.Assignment;
import business.problem.model.Classroom;

public class PreferencesFitnessValue extends AbstractFitnessValue {
	private Map<String, List<Preference>> preferences;

	public PreferencesFitnessValue(double weight,
			Map<String, List<Preference>> preferences) {
		super(weight);
		this.preferences = new HashMap<String, List<Preference>>(
				preferences);
	}

	@Override
	public double getValue(Map<String, Assignment> assignments)
	{
		double value = 0.0;
		int prefCounter = 0;
		int prefValidCounter = 0;
		for (String groupCode : preferences.keySet()) {
			Classroom c = assignments.get(groupCode).getClassroom();
			if (c != null) {
				List<Preference> prefsForGroup = preferences
						.get(groupCode);
				boolean negativeValid = prefsForGroup.stream()
						.noneMatch(p -> p.getType()
								.equals(PreferenceType.NEGATIVE)
								&& p.getClassroom()
										.equals(c));
				boolean positiveValid = prefsForGroup.stream()
						.anyMatch(p -> p.getType()
								.equals(PreferenceType.POSITIVE)
								&& p.getClassroom()
										.equals(c));
				if (negativeValid && positiveValid) {
					prefValidCounter++;
				}
			}
			prefCounter++;
		}
		if (prefCounter == 0) {
			value = 100;
		} else {
			value = (prefValidCounter / prefCounter) * 100;
		}
		return value;
	}
}

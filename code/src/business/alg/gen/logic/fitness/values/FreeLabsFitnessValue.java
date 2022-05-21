package business.alg.gen.logic.fitness.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.alg.greed.model.Assignment;
import business.problem.model.Group;
import business.problem.model.schedule.Day;
import business.problem.model.schedule.GroupSchedule;
import business.problem.utils.ProblemUtils;

public class FreeLabsFitnessValue extends AbstractFitnessValue {

	private FreeLabsCounter counter;
	private int freeLabsPref = 0;

	public FreeLabsFitnessValue(double weight, int freeLabsPreference) {
		super(weight);
		this.freeLabsPref = freeLabsPreference;
	}

	@Override
	public double getValue(Map<String, Assignment> assignments) {

		this.counter = new FreeLabsCounter();

		List<Assignment> filtered = assignments.values().stream()
				.filter(a -> a.isAssigned() && ProblemUtils.isLabGroup(a.getGroup())).collect(Collectors.toList());

		for (Assignment a : filtered) {
			Group g = a.getGroup();
			for (GroupSchedule gs : g.getAllGroupSchedules()) {
				counter.increaseCounterFor(gs);
			}
		}

		double mean = counter.getMeanByHour();
		double absDiff = Math.abs(mean - freeLabsPref);

		if (absDiff >= freeLabsPref)
			return 0;

		return 100 - (100 * absDiff / freeLabsPref);

	}

	private class FreeLabsCounter {

		private static final int START_HOUR = 9;
		private static final int FINISH_HOUR = 21;

		private Map<Day, Map<Integer, Integer>> weekMap;

		private FreeLabsCounter() {
			initializeWeekMap();
		}

		public double getMeanByHour() {
			double meanWeek = 0.0;
			for (Day d : weekMap.keySet()) {
				double meanDay = 0.0;
				Map<Integer, Integer> dayMap = weekMap.get(d);
				for (int hour : weekMap.get(d).keySet()) {
					meanDay += dayMap.get(hour);
				}
				meanDay = meanDay / (FINISH_HOUR - START_HOUR);
				meanWeek += meanDay;
			}
			meanWeek = meanWeek / (weekMap.keySet().size());
			return meanWeek;
		}

		public void increaseCounterFor(GroupSchedule gs) {
			int s, f;
			s = gs.getStart().getHour();
			f = gs.getFinish().getHour();

			Map<Integer, Integer> dayMap = weekMap.get(gs.getDay());

			for (int i = s; i < f; i++) {
				int counter = dayMap.get(i);
				dayMap.put(i, ++counter);
			}

			weekMap.put(gs.getDay(), dayMap);
		}

		private void initializeWeekMap() {
			weekMap = new HashMap<Day, Map<Integer, Integer>>();
			for (Day d : Day.values()) {
				Map<Integer, Integer> hourMap = new HashMap<Integer, Integer>();
				for (int i = START_HOUR; i < FINISH_HOUR; i++) {
					hourMap.put(i, 0);
				}
				weekMap.put(d, hourMap);
			}
		}

	}

}

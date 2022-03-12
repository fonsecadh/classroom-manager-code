package business.alg.gen.logic.fitness.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.alg.greed.model.Assignment;
import business.problem.ClassroomType;
import business.problem.Group;
import business.problem.schedule.Day;
import business.problem.schedule.GroupSchedule;

public class FreeLabsFitnessValue extends AbstractFitnessValue {

	private FreeLabsCounter counter;

	public FreeLabsFitnessValue(double weight) {
		super(weight);
		this.counter = new FreeLabsCounter();
	}

	@Override
	public double getValue(List<Assignment> assignments) {
		List<Assignment> filtered = assignments.stream()
				.filter(a -> a.isAssigned() && a.getGroup().getClassroomType() == ClassroomType.LABORATORY)
				.collect(Collectors.toList());

		for (Assignment a : filtered) {
			Group g = a.getGroup();
			for (GroupSchedule gs : g.getAllGroupSchedules()) {
				counter.increaseCounterFor(gs);
			}
		}

		return counter.getMeanByHour();
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

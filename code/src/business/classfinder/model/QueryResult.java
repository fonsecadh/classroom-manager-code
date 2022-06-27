package business.classfinder.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import business.problem.model.Classroom;
import business.problem.model.schedule.Day;
import business.problem.model.schedule.GroupSchedule;

public class QueryResult {
	private Classroom classroom;
	private Map<GroupSchedule, Boolean> schedule;
	private ClassfinderQuery query;
	private Day day;
	private String week;
	private List<GroupSchedule> finalFreeSlots;

	public QueryResult(ClassfinderQuery query, Classroom classroom, Day day,
			String week) {
		this.classroom = classroom;
		this.schedule = new TreeMap<GroupSchedule, Boolean>(Comparator
				.comparing(GroupSchedule::getStart)
				.thenComparing(GroupSchedule::getFinish));
		this.query = query;
		this.day = day;
		this.week = week;
		this.finalFreeSlots = new ArrayList<GroupSchedule>();
		initialiseScheduleMap();
	}

	public Classroom getClassroom()
	{
		return classroom;
	}

	public Map<GroupSchedule, Boolean> getSchedule()
	{
		return new HashMap<GroupSchedule, Boolean>(schedule);
	}

	public Day getDay()
	{
		return day;
	}

	public String getWeek()
	{
		return week;
	}

	public boolean isValid()
	{
		this.finalFreeSlots = getFreeSlots();
		return this.finalFreeSlots.size() > 0;
	}

	public void addCollisionToScheduleMap(GroupSchedule collision)
	{
		if (!collision.overlapsWith(query.getStartTime(),
				query.getEndTime())) {
			return;
		}
		// Max (collision start, query start)
		LocalTime start = collision.getStart()
				.isBefore(query.getStartTime())
						? LocalTime.from(query
								.getStartTime())
						: LocalTime.from(collision
								.getStart());
		// Min (collision end, query end)
		LocalTime end = collision.getFinish()
				.isAfter(query.getEndTime())
						? LocalTime.from(query
								.getEndTime())
						: LocalTime.from(collision
								.getFinish());
		while (!start.equals(end)) {
			GroupSchedule gs = new GroupSchedule();
			gs.setDay(day);
			gs.setStart(start);
			start = start.plusMinutes(30);
			gs.setFinish(start);
			this.schedule.put(gs, true);
		}
	}

	private void initialiseScheduleMap()
	{
		LocalTime start = LocalTime.from(query.getStartTime());
		LocalTime end = LocalTime.from(query.getEndTime());
		while (!start.equals(end)) {
			GroupSchedule gs = new GroupSchedule();
			gs.setDay(day);
			gs.setStart(start);
			start = start.plusMinutes(30);
			gs.setFinish(start);
			this.schedule.put(gs, false);
		}
	}

	private List<GroupSchedule> getFreeSlots()
	{
		List<GroupSchedule> freeSlots = new ArrayList<GroupSchedule>();
		Iterator<Entry<GroupSchedule, Boolean>> it = schedule.entrySet()
				.iterator();
		boolean prevFree = false;
		GroupSchedule gs = null;
		while (it.hasNext()) {
			Map.Entry<GroupSchedule, Boolean> pair = it.next();
			// If time frame is free
			if (!pair.getValue()) {
				if (prevFree) { // If the previous slot was free
					LocalTime time = LocalTime
							.from(gs.getFinish());
					time = time.plusMinutes(30);
					gs.setFinish(time);
				} else { // If the previous slot was not free
					if (gs != null) {
						freeSlots.add(gs);
						gs = null;
					}
					gs = pair.getKey();
				}
				prevFree = true;
			} else { // If time frame is not free
				if (gs != null) {
					freeSlots.add(gs);
					gs = null;
				}
				prevFree = false;
			}
		}
		if (gs != null) {
			freeSlots.add(gs);
			gs = null;
		}
		freeSlots = freeSlots.stream()
				.filter(s -> s.getDurationInHours() >= query
						.getDurationInHours())
				.collect(Collectors.toList());
		return new ArrayList<GroupSchedule>(freeSlots);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(classroom.getCode());
		sb.append(" (");
		sb.append("Week: " + week + ", ");
		sb.append("Day: " + day + ", ");
		for (int i = 0; i < finalFreeSlots.size(); i++) {
			sb.append(finalFreeSlots.get(i).toString());
			if (i < finalFreeSlots.size() - 1)
				sb.append(", ");
		}
		sb.append(")");
		return sb.toString();
	}
}

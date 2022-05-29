package business.classfinder.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

	public QueryResult(ClassfinderQuery query, Classroom classroom, Day day,
			String week) {
		this.classroom = classroom;
		this.schedule = new HashMap<GroupSchedule, Boolean>();
		this.query = query;
		this.day = day;
		this.week = week;
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

	public boolean isValid()
	{
		return getFreeSlots().size() > 0;
	}

	public void addCollisionToScheduleMap(GroupSchedule collision)
	{
		LocalTime start = LocalTime.from(collision.getStart());
		LocalTime end = LocalTime.from(collision.getFinish());
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
			if (pair.getValue()) {
				if (prevFree) { // If the previous slot was free
					LocalTime time = LocalTime
							.from(gs.getFinish());
					time.plusMinutes(30);
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
		sb.append(week + ", ");
		List<GroupSchedule> freeSlots = getFreeSlots();
		for (int i = 0; i < freeSlots.size(); i++) {
			sb.append(freeSlots.get(i).toString());
			if (i < freeSlots.size() - 1)
				sb.append(", ");
		}
		sb.append(")");
		return sb.toString();
	}
}

package business.problem.schedule;

import java.time.LocalTime;

public class GroupSchedule {

	private int id;
	private Day day;
	private LocalTime start;
	private LocalTime finish;
	private WeekType weekType;

	public int getId() {
		return id;
	}

	public Day getDay() {
		return day;
	}

	public LocalTime getStart() {
		return start;
	}

	public LocalTime getFinish() {
		return finish;
	}

	public WeekType getWeekType() {
		return weekType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public void setStart(LocalTime start) {
		this.start = start;
	}

	public void setFinish(LocalTime finish) {
		this.finish = finish;
	}

	public void setWeekType(WeekType weekType) {
		this.weekType = weekType;
	}

	public boolean overlapsWith(GroupSchedule other) {
		boolean overlaps = false;
		if (sameDay(other)) {
			if (intervalOverlap(other)) {
				if (weekOverlap(other)) {
					overlaps = true;
				}
			}
		}
		return overlaps;
	}

	private boolean weekOverlap(GroupSchedule other) {
		return getWeekType().equals(other.getWeekType())
				|| (getWeekType().equals(WeekType.ALWAYS) || (other.getWeekType().equals(WeekType.ALWAYS)));
	}

	private boolean intervalOverlap(GroupSchedule other) {
		return (getStart().isBefore(other.getFinish()) || getStart().equals(other.getFinish()))
				&& (getFinish().isAfter(other.getStart()) || getFinish().equals(other.getStart()));
	}

	private boolean sameDay(GroupSchedule other) {
		return getDay().toString().equals(other.getDay().toString());
	}

}

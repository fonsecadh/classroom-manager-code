package business.problem.model.schedule;

import java.time.LocalTime;

public class GroupSchedule {

	private Day day;
	private LocalTime start;
	private LocalTime finish;

	public Day getDay() {
		return day;
	}

	public LocalTime getStart() {
		return start;
	}

	public LocalTime getFinish() {
		return finish;
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

	public boolean overlapsWith(GroupSchedule other) {
		boolean overlaps = false;
		if (sameDay(other)) {
			if (intervalOverlap(other)) {
				overlaps = true;
			}
		}
		return overlaps;
	}

	public boolean overlapsWith(LocalTime start, LocalTime finish) {
		return (getStart().isBefore(finish) && !getStart().equals(finish))
				&& (getFinish().isAfter(start) && !getFinish().equals(start));
	}

	private boolean intervalOverlap(GroupSchedule other) {
		return (getStart().isBefore(other.getFinish()) && !getStart().equals(other.getFinish()))
				&& (getFinish().isAfter(other.getStart()) && !getFinish().equals(other.getStart()));
	}

	private boolean sameDay(GroupSchedule other) {
		return getDay().toString().equals(other.getDay().toString());
	}

}

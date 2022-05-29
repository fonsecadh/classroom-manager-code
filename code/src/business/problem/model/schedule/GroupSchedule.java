package business.problem.model.schedule;

import java.time.Duration;
import java.time.LocalTime;

public class GroupSchedule {
	private Day day;
	private LocalTime start;
	private LocalTime finish;

	public Day getDay()
	{
		return day;
	}

	public LocalTime getStart()
	{
		return start;
	}

	public LocalTime getFinish()
	{
		return finish;
	}

	public double getDurationInHours()
	{
		double minutes = Duration.between(start, finish).toMinutes();
		return minutes / 60d;
	}

	public void setDay(Day day)
	{
		this.day = day;
	}

	public void setStart(LocalTime start)
	{
		this.start = start;
	}

	public void setFinish(LocalTime finish)
	{
		this.finish = finish;
	}

	public boolean overlapsWith(GroupSchedule other)
	{
		boolean overlaps = false;
		if (sameDay(other)) {
			if (intervalOverlap(other)) {
				overlaps = true;
			}
		}
		return overlaps;
	}

	public boolean overlapsWith(LocalTime start, LocalTime finish)
	{
		return (getStart().isBefore(finish)
				&& !getStart().equals(finish))
				&& (getFinish().isAfter(start)
						&& !getFinish().equals(start));
	}

	private boolean intervalOverlap(GroupSchedule other)
	{
		return (getStart().isBefore(other.getFinish())
				&& !getStart().equals(other.getFinish()))
				&& (getFinish().isAfter(other.getStart())
						&& !getFinish().equals(other
								.getStart()));
	}

	private boolean sameDay(GroupSchedule other)
	{
		return getDay().toString().equals(other.getDay().toString());
	}

	@Override
	public String toString()
	{
		return String.format("%02d:%02d - %02d:%02d", start.getHour(),
				start.getMinute(), finish.getHour(),
				finish.getMinute());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result
				+ ((finish == null) ? 0 : finish.hashCode());
		result = prime * result
				+ ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupSchedule other = (GroupSchedule) obj;
		if (day != other.day)
			return false;
		if (finish == null) {
			if (other.finish != null)
				return false;
		} else if (!finish.equals(other.finish))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
}

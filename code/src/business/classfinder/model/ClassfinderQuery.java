package business.classfinder.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.problem.model.ClassroomType;
import business.problem.model.schedule.Day;
import business.problem.utils.ProblemUtils;

public class ClassfinderQuery {
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private double durationInHours;
	private int numberOfAttendants;
	private ClassroomType classroomType;
	private int numberOfResults;
	private List<String> weeks;
	private Map<String, List<Day>> weekDaysMap;

	public ClassfinderQuery(LocalDate startDate, LocalDate endDate) {
		this.startDate = LocalDate.from(startDate);
		this.endDate = LocalDate.from(endDate);
		initialiseWeekList();
		initialiseWeekDaysMap();
	}

	public LocalDate getStartDate()
	{
		return startDate;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public LocalTime getStartTime()
	{
		return startTime;
	}

	public LocalTime getEndTime()
	{
		return endTime;
	}

	public double getDurationInHours()
	{
		return durationInHours;
	}

	public int getNumberOfAttendants()
	{
		return numberOfAttendants;
	}

	public List<String> getAcademicWeeks()
	{
		return new ArrayList<String>(weeks);
	}

	public Map<String, List<Day>> getWeekDaysToMap()
	{
		return new HashMap<String, List<Day>>(weekDaysMap);
	}

	public ClassroomType getClassroomType()
	{
		return classroomType;
	}

	public int getNumberOfResults()
	{
		return numberOfResults;
	}

	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}

	public void setStartTime(LocalTime startTime)
	{
		this.startTime = startTime;
	}

	public void setEndTime(LocalTime endTime)
	{
		this.endTime = endTime;
	}

	public void setDurationInHours(double durationInHours)
	{
		this.durationInHours = durationInHours;
	}

	public void setNumberOfAttendants(int numberOfAttendants)
	{
		this.numberOfAttendants = numberOfAttendants;
	}

	public void setClassroomType(ClassroomType classroomType)
	{
		this.classroomType = classroomType;
	}

	public void setNumberOfResults(int numberOfResults)
	{
		this.numberOfResults = numberOfResults;
	}

	private void initialiseWeekList()
	{
		List<String> weeks = ProblemUtils.getAcademicWeeks(startDate,
				endDate);
		this.weeks = new ArrayList<String>(weeks);
	}

	private void initialiseWeekDaysMap()
	{
		Map<String, List<Day>> result = new HashMap<String, List<Day>>();
		List<LocalDate> dateList = new ArrayList<LocalDate>();
		LocalDate start = LocalDate.from(startDate);
		LocalDate end = LocalDate.from(endDate);
		while (!start.isAfter(end)) {
			dateList.add(start);
			start = start.plusDays(1);
		}
		for (LocalDate d : dateList) {
			String week = ProblemUtils.getAcademicWeek(d);
			List<Day> days = null;
			if (result.get(week) == null)
				days = new ArrayList<Day>();
			else
				days = result.get(week);
			Day day = ProblemUtils.getDay(d);
			if (day != null) {
				days.add(day);
				result.put(week, new ArrayList<Day>(days));
			}
		}
		this.weekDaysMap = new HashMap<String, List<Day>>(result);
	}

	@Override
	public String toString()
	{
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd/MM/yyyy");

		String format = "Query: %s;%s;%02d.%02d;%02d.%02d;%.2f;%d;%s;%d";

		String type = classroomType.equals(ClassroomType.LABORATORY)
				? "L"
				: "T";

		String result = String.format(format,
				startDate.format(formatter),
				endDate.format(formatter), startTime.getHour(),
				startTime.getMinute(), endTime.getHour(),
				endTime.getMinute(), durationInHours,
				numberOfAttendants, type, numberOfResults);

		return result;
	}
}

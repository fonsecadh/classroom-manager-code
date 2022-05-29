package business.classfinder.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import business.problem.model.ClassroomType;
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

	public ClassroomType getClassroomType()
	{
		return classroomType;
	}

	public int getNumberOfResults()
	{
		return numberOfResults;
	}

	public List<String> getAcademicWeeks()
	{
		return ProblemUtils.getAcademicWeeks(startDate, endDate);
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

	@Override
	public String toString()
	{
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd/MM/yyyy");

		String format = "Classfinder query: %s;%s;%02d:%02d;%02d:%02d;%f;%d;%s,%d";

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

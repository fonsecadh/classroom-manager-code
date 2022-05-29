package business.problem.model;

import java.util.ArrayList;
import java.util.List;

import business.problem.model.schedule.GroupSchedule;
import business.problem.utils.ProblemUtils;

public class Group {

	private String code;
	private int numberOfStudents;
	private ClassroomType classroomType;
	private GroupLanguage groupLanguage;
	private List<GroupSchedule> allGroupSchedules;
	private List<String> academicWeeks;

	public Group() {
		this.allGroupSchedules = new ArrayList<GroupSchedule>();
		this.academicWeeks = new ArrayList<String>();
	}

	public String getCode()
	{
		return code;
	}

	public String getNameFromCode()
	{
		return ProblemUtils.getNameFromGroupCode(code);
	}

	public String getTypeFromCode()
	{
		return ProblemUtils.getTypeFromGroupCode(code);
	}

	public String getSubjectFromCode()
	{
		return ProblemUtils.getSubjectFromGroupCode(code);
	}

	public int getNumberOfStudents()
	{
		return numberOfStudents;
	}

	public ClassroomType getClassroomType()
	{
		return classroomType;
	}

	public GroupLanguage getGroupLanguage()
	{
		return groupLanguage;
	}

	public List<GroupSchedule> getAllGroupSchedules()
	{
		return new ArrayList<GroupSchedule>(allGroupSchedules);
	}

	public List<String> getAcademicWeeks()
	{
		return new ArrayList<String>(academicWeeks);
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public void setNumberOfStudents(int numberOfStudents)
	{
		this.numberOfStudents = numberOfStudents;
	}

	public void setClassroomType(ClassroomType classroomType)
	{
		this.classroomType = classroomType;
	}

	public void setGroupLanguage(GroupLanguage groupLanguage)
	{
		this.groupLanguage = groupLanguage;
	}

	public void addGroupSchedule(GroupSchedule groupSchedule)
	{
		this.allGroupSchedules.add(groupSchedule);
	}

	public void addAcademicWeek(String academicWeek)
	{
		this.academicWeeks.add(academicWeek);
	}

	public boolean collidesWith(Group other)
	{
		boolean schedulesCollide = false, weeksCollide = false;

		outerloop: for (GroupSchedule ogs : other
				.getAllGroupSchedules()) {
			for (GroupSchedule gs : getAllGroupSchedules()) {
				if (ogs.overlapsWith(gs)) {
					schedulesCollide = true;
					break outerloop;
				}
			}
		}

		if (schedulesCollide) {
			weekloop: for (String w : getAcademicWeeks()) {
				if (other.getAcademicWeeks().contains(w)) {
					weeksCollide = true;
					break weekloop;
				}
			}
		}

		return schedulesCollide && weeksCollide;
	}

	public boolean sameGroupNameAs(Group other)
	{

		if (getNameFromCode()
				.equalsIgnoreCase(other.getNameFromCode())) {
			return true;
		}
		return false;

	}

	public boolean sameGroupNameAs(String groupName)
	{

		if (getNameFromCode().equalsIgnoreCase(groupName)) {
			return true;
		}
		return false;

	}

	public boolean sameTypeAndGroupNameAs(Group other)
	{

		if (getNameFromCode().equalsIgnoreCase(other.getNameFromCode())
				&& getTypeFromCode().equalsIgnoreCase(
						other.getTypeFromCode())) {
			return true;
		}
		return false;

	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((code == null) ? 0 : code.hashCode());
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
		Group other = (Group) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}

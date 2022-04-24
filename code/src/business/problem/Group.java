package business.problem;

import java.util.ArrayList;
import java.util.List;

import business.problem.schedule.GroupSchedule;

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

	public String getCode() {
		return code;
	}

	public int getNumberOfStudents() {
		return numberOfStudents;
	}

	public ClassroomType getClassroomType() {
		return classroomType;
	}

	public GroupLanguage getGroupLanguage() {
		return groupLanguage;
	}

	public List<GroupSchedule> getAllGroupSchedules() {
		return new ArrayList<GroupSchedule>(allGroupSchedules);
	}

	public List<String> getAcademicWeeks() {
		return new ArrayList<String>(academicWeeks);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setNumberOfStudents(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public void setClassroomType(ClassroomType classroomType) {
		this.classroomType = classroomType;
	}

	public void setGroupLanguage(GroupLanguage groupLanguage) {
		this.groupLanguage = groupLanguage;
	}

	public void addGroupSchedule(GroupSchedule groupSchedule) {
		this.allGroupSchedules.add(groupSchedule);
	}

	public void addAcademicWeek(String academicWeek) {
		this.academicWeeks.add(academicWeek);
	}

	public boolean collidesWith(Group other) {
		boolean schedulesCollide = false, weeksCollide = false;
		
		outerloop:
		for (GroupSchedule ogs : other.getAllGroupSchedules()) {
			for (GroupSchedule gs : getAllGroupSchedules()) {
				if (ogs.overlapsWith(gs)) {
					schedulesCollide = true;
					break outerloop;
				}
			}
		}
		
		if (schedulesCollide) {
			weekloop:
			for (String w : getAcademicWeeks()) {
				if (other.getAcademicWeeks().contains(w)) {
					weeksCollide = true; 
					break weekloop;
				}
			}
		}
		
		return schedulesCollide && weeksCollide;
	}

}

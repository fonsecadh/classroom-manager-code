package business.problem;

import java.util.ArrayList;
import java.util.List;

import business.problem.schedule.GroupSchedule;

public class Group {

	private int id;
	private String code;
	private int numberOfStudents;
	private ClassroomType classroomType;
	private List<GroupSchedule> allGroupSchedules;

	public int getId() {
		return id;
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

	public List<GroupSchedule> getAllGroupSchedules() {
		return new ArrayList<GroupSchedule>(allGroupSchedules);
	}

	public void setId(int id) {
		this.id = id;
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

	public void setAllGroupSchedules(List<GroupSchedule> allGroupSchedules) {
		this.allGroupSchedules = new ArrayList<GroupSchedule>(allGroupSchedules);
	}

	public boolean collidesWith(Group other) {
		boolean collides = false;
		for (GroupSchedule ogs : other.getAllGroupSchedules()) {
			for (GroupSchedule gs : getAllGroupSchedules()) {
				collides = ogs.overlapsWith(gs);
			}
		}
		return collides;
	}

}

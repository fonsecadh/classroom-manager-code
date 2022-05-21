package business.problem.model;

import java.util.ArrayList;
import java.util.List;

public class Subject {

	private String code;
	private String course;
	private String semester;
	private List<Group> groups;

	public Subject() {
		this.groups = new ArrayList<Group>();
	}

	public String getCode() {
		return code;
	}

	public String getCourse() {
		return course;
	}

	public String getSemester() {
		return semester;
	}

	public List<Group> getGroups() {
		return new ArrayList<Group>(groups);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public void addGroup(Group group) {
		this.groups.add(group);
	}

}

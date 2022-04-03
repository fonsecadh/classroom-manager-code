package business.problem;

import java.util.List;

public class Subject {

	private int id;
	private String code;
	private String course;
	private List<Group> groups;

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getCourse() {
		return course;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

}

package business.problem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a subject. A subject is uniquely identified by its code, and contains
 * a list of the groups ({@link Group}) that belong to it.
 * 
 * @author Hugo Fonseca Díaz
 *
 */
public class Subject {
	private String code;
	private String course;
	private String semester;
	private List<Group> groups;

	public Subject() {
		this.groups = new ArrayList<Group>();
	}

	public String getCode()
	{
		return code;
	}

	public String getCourse()
	{
		return course;
	}

	public String getSemester()
	{
		return semester;
	}

	public List<Group> getGroups()
	{
		return new ArrayList<Group>(groups);
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public void setCourse(String course)
	{
		this.course = course;
	}

	public void setSemester(String semester)
	{
		this.semester = semester;
	}

	public void addGroup(Group group)
	{
		this.groups.add(group);
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
		Subject other = (Subject) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Subject [code=" + code + "]";
	}
}

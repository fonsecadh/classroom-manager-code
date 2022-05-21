package business.alg.greed.model;

import business.problem.model.Classroom;
import business.problem.model.Group;

public class Assignment {

	private Group group;
	private Classroom classroom;

	public Assignment(Assignment assignment) {
		this.group = assignment.getGroup();
		this.classroom = assignment.getClassroom();
	}

	public Assignment(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public Classroom getClassroom() {
		return classroom;
	}

	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}

	public boolean isAssigned() {
		return classroom != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assignment other = (Assignment) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}

}

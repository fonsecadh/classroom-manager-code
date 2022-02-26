package business.alg.greed.model;

import business.problem.Classroom;
import business.problem.Group;

public class Assignment {

	private Group group;
	private Classroom classroom;
	
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

}

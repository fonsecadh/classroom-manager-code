package business.entities.alg;

import business.entities.Classroom;
import business.entities.Group;

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

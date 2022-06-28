package business.alg.gen.model;

import business.problem.model.Classroom;

public class Preference {
	private PreferenceType type;
	private Classroom classroom;

	public PreferenceType getType()
	{
		return type;
	}

	public Classroom getClassroom()
	{
		return classroom;
	}

	public void setType(PreferenceType type)
	{
		this.type = type;
	}

	public void setClassroom(Classroom classroom)
	{
		this.classroom = classroom;
	}
}

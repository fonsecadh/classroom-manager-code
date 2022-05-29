package business.alg.greed.model;

import business.problem.model.Classroom;

public class Restriction {
	private RestrictionType type;
	private Classroom classroom;

	public RestrictionType getType()
	{
		return type;
	}

	public Classroom getClassroom()
	{
		return classroom;
	}

	public void setType(RestrictionType type)
	{
		this.type = type;
	}

	public void setClassroom(Classroom classroom)
	{
		this.classroom = classroom;
	}
}

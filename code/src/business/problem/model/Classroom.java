package business.problem.model;

/**
 * Models a classroom. A classroom is uniquely identified by its code.
 * 
 * @author Hugo Fonseca Díaz
 *
 */
public class Classroom {
	private String code;
	private int numberOfSeats;
	private ClassroomType type;

	public String getCode()
	{
		return code;
	}

	public int getNumberOfSeats()
	{
		return numberOfSeats;
	}

	public ClassroomType getType()
	{
		return type;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public void setNumberOfSeats(int numberOfSeats)
	{
		this.numberOfSeats = numberOfSeats;
	}

	public void setType(ClassroomType type)
	{
		this.type = type;
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
		Classroom other = (Classroom) obj;
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
		return "Classroom [code=" + code + "]";
	}
}

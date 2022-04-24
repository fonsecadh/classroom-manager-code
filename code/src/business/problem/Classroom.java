package business.problem;

public class Classroom {

	private String code;
	private int numberOfSeats;
	private ClassroomType type;

	public String getCode() {
		return code;
	}

	public int getNumberOfSeats() {
		return numberOfSeats;
	}

	public ClassroomType getType() {
		return type;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public void setType(ClassroomType type) {
		this.type = type;
	}

}

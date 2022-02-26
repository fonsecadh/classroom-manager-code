package business.problem;

public class Group {

	private int id;
	private String code;
	private Subject subject;
	private int numberOfStudents;

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public Subject getSubject() {
		return subject;
	}

	public int getNumberOfStudents() {
		return numberOfStudents;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public void setNumberOfStudents(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

}

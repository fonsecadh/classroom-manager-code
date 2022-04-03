package business.alg.gen.logic;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import business.alg.gen.model.Individual;
import business.alg.greed.logic.Decoder;
import business.alg.greed.model.Assignment;
import business.problem.Classroom;
import business.problem.Group;
import business.problem.Subject;

public class IndividualPrinter {

	List<Subject> subjects;
	Decoder decoder;

	public IndividualPrinter(List<Subject> subjects, Decoder decoder) {
		this.subjects = new ArrayList<Subject>(subjects);
		this.decoder = decoder;
	}

	public void printIndividual(PrintStream printStream, Individual individual) {
		StringBuilder sb = new StringBuilder();

		Map<Integer, Assignment> assignmentsMap = decoder.decodeAsMap(individual);

		List<String> coursesDuplicates = subjects.stream().map(s -> s.getCourse()).collect(Collectors.toList());
		Set<String> courses = new TreeSet<String>(coursesDuplicates);

		Comparator<Subject> c = Comparator.comparing(s -> s.getCourse());
		c = c.thenComparing(Comparator.comparing(s -> s.getCode()));

		subjects.sort(c);

		for (String course : courses) {
			String cMsg = "COURSE: " + course.toUpperCase();
			appendTitle(sb, cMsg);

			List<Subject> courseSubjects = subjects.stream().filter(s -> s.getCourse().equalsIgnoreCase(course))
					.collect(Collectors.toList());

			for (Subject subject : courseSubjects) {
				String sMsg = "SUBJECT: " + subject.getCode().toUpperCase();
				appendTitle(sb, sMsg);

				for (Group group : subject.getGroups()) {
					Classroom classroom = assignmentsMap.get(group.getId()).getClassroom();
					String gMsg = group.getCode().toUpperCase() + ":" + classroom.getCode().toUpperCase();
					appendLine(sb, gMsg);
				}
			}
		}

		printStream.println(sb.toString());
	}

	private void appendTitle(StringBuilder sb, String msg) {
		sb.append(msg + "\n");
		for (int i = 0; i < msg.length(); i++) {
			sb.append("=");
		}
		sb.append("\n");
	}

	private void appendLine(StringBuilder sb, String msg) {
		sb.append(msg + "\n");
	}

}

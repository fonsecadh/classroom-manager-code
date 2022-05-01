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
import business.alg.greed.logic.GreedyAlgorithm;
import business.alg.greed.model.Assignment;
import business.problem.Classroom;
import business.problem.Group;
import business.problem.Subject;

public class IndividualPrinter {

	private List<Subject> subjects;
	private Decoder decoder;
	private GreedyAlgorithm greedy;

	public IndividualPrinter(List<Subject> subjects, Decoder decoder, GreedyAlgorithm greedy) {
		this.subjects = new ArrayList<Subject>(subjects);
		this.decoder = decoder;
		this.greedy = greedy;
	}

	public String getPrettyIndividual(Individual individual) {
		StringBuilder sb = new StringBuilder();

		List<Assignment> decoded = decoder.decode(individual);
		Map<String, Assignment> assignmentsMap = greedy.greedyAlgorithm(decoded);

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
					Classroom classroom = assignmentsMap.get(group.getCode()).getClassroom();
					String gMsg = group.getCode().toUpperCase() + " -> ";
					if (classroom != null)
						gMsg += classroom.getCode().toUpperCase();
					appendLine(sb, gMsg);
				}
				appendNewLine(sb);
			}
		}

		return sb.toString();
	}

	public void printIndividual(PrintStream printStream, Individual individual) {
		printStream.println(getPrettyIndividual(individual));
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

	private void appendNewLine(StringBuilder sb) {
		sb.append("\n");
	}

}

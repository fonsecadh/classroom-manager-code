package business.alg.gen.logic;

import java.io.PrintStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import business.alg.greed.model.Assignment;
import business.problem.Classroom;
import business.problem.Group;
import business.problem.Subject;
import business.problem.schedule.Day;

public class IndividualPrinter {

	private List<Subject> subjects;
	private Map<String, Assignment> assignmentsMap;

	public IndividualPrinter(List<Subject> subjects, Map<String, Assignment> assignmentsMap) {
		this.subjects = new ArrayList<Subject>(subjects);
		this.assignmentsMap = new HashMap<String, Assignment>(assignmentsMap);
	}

	public String getPrettyIndividual() {
		StringBuilder sb = new StringBuilder();

		List<String> coursesDuplicates = subjects.stream().map(s -> s.getCourse()).collect(Collectors.toList());
		Set<String> courses = new TreeSet<String>(coursesDuplicates);

		Comparator<Subject> c = Comparator.comparing(s -> s.getCourse());
		c = c.thenComparing(Comparator.comparing(s -> s.getCode()));

		subjects.sort(c);

		for (String course : courses) {
			String cMsg = "COURSE: " + course;
			appendTitle(sb, cMsg);

			List<Subject> courseSubjects = subjects.stream().filter(s -> s.getCourse().equalsIgnoreCase(course))
					.collect(Collectors.toList());

			for (Subject subject : courseSubjects) {
				String sMsg = "SUBJECT: " + subject.getCode();
				appendTitle(sb, sMsg);

				for (Group group : subject.getGroups()) {
					Classroom classroom = assignmentsMap.get(group.getCode()).getClassroom();
					String gMsg = group.getCode() + " -> ";
					if (classroom != null)
						gMsg += classroom.getCode();
					else
						gMsg += "NO CLASS ASSIGNED!";
					appendLine(sb, gMsg);
				}
				appendNewLine(sb);
			}
		}

		return sb.toString();
	}

	public void printIndividual(PrintStream printStream) {
		printStream.println(getPrettyIndividual());
	}

	public String getTimetableFor(Classroom c) {

		int hourColWidth = 13;
		int maxColWidth = 9;

		int mondayWidth, tuesdayWidth, wednesdayWidth, thursdayWidth, fridayWidth;
		mondayWidth = maxColWidth;
		tuesdayWidth = maxColWidth;
		wednesdayWidth = maxColWidth;
		thursdayWidth = maxColWidth;
		fridayWidth = maxColWidth;

		String[][] data = new String[25][8];

		data[0] = new String[] { "HORA", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO" };

		LocalTime start = LocalTime.of(9, 0);
		LocalTime end = LocalTime.of(9, 30);

		for (int i = 1; i <= 24; i++) {

			data[i] = new String[8];
			data[i][0] = String.format("%02d:%02d - %02d:%02d", start.getHour(), start.getMinute(), end.getHour(),
					end.getMinute());

			data[i][1] = getGroupsFor(c, Day.MONDAY, start, end);
			if (data[i][1].length() > mondayWidth)
				mondayWidth = data[i][1].length();

			data[i][2] = getGroupsFor(c, Day.TUESDAY, start, end);
			if (data[i][2].length() > tuesdayWidth)
				tuesdayWidth = data[i][2].length();

			data[i][3] = getGroupsFor(c, Day.WEDNESDAY, start, end);
			if (data[i][3].length() > wednesdayWidth)
				wednesdayWidth = data[i][3].length();

			data[i][4] = getGroupsFor(c, Day.THURSDAY, start, end);
			if (data[i][4].length() > thursdayWidth)
				thursdayWidth = data[i][4].length();

			data[i][5] = getGroupsFor(c, Day.FRIDAY, start, end);
			if (data[i][5].length() > fridayWidth)
				fridayWidth = data[i][5].length();

			data[i][6] = ""; // For the moment Saturdays and Sundays are not considered
			data[i][7] = "";

			start = start.plusMinutes(30);
			end = end.plusMinutes(30);

		}

		StringBuilder timetable = new StringBuilder();

		String form = "| %-" + hourColWidth + "s | %-" + mondayWidth + "s | %-" + tuesdayWidth + "s | %-"
				+ wednesdayWidth + "s | %-" + thursdayWidth + "s | %-" + fridayWidth + "s | %-" + maxColWidth + "s | %-"
				+ maxColWidth + "s |";

		String formBorder = form.replace("|", "+");
		String border = String.format(formBorder, " ", " ", " ", " ", " ", " ", " ", " ").replace(" ", "-");

		appendLine(timetable, String.format("Nombre del aula: %s", c.getCode()));
		appendNewLine(timetable);
		appendLine(timetable, border);

		for (int i = 0; i <= 24; i++) {

			String row = String.format(form, data[i][0], data[i][1], data[i][2], data[i][3], data[i][4], data[i][5],
					data[i][6], data[i][7]);
			appendLine(timetable, row);
			appendLine(timetable, border);

		}

		appendNewLine(timetable);

		return timetable.toString();

	}

	private String getGroupsFor(Classroom c, Day d, LocalTime start, LocalTime end) {

		String str = "";

		List<Group> gList = assignmentsMap.values().stream()
				.filter(a -> a.getClassroom().getCode().equalsIgnoreCase(c.getCode())).map(a -> a.getGroup())
				.filter(g -> g.getAllGroupSchedules().stream()
						.anyMatch(s -> s.getDay().equals(d) && s.overlapsWith(start, end)))
				.collect(Collectors.toList());

		for (int i = 0; i < gList.size(); i++) {
			str += gList.get(i).getCode();
			if (i < gList.size() - 1) {
				str += ", ";
			}
		}

		return str;

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

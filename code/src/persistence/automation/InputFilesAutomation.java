package persistence.automation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import business.problem.model.ClassroomType;
import business.problem.model.Group;
import business.problem.model.GroupLanguage;
import business.problem.model.schedule.GroupSchedule;

public class InputFilesAutomation {
	private List<Group> groups;

	public InputFilesAutomation(List<Group> groups) {
		this.groups = new ArrayList<Group>(groups);
		this.groups.sort(Comparator.comparing(Group::getSubjectFromCode)
				.thenComparing(Group::getCode));
	}

	public String getGroupData()
	{
		StringBuilder sb = new StringBuilder();
		appendLine(sb, "﻿Codigo;CodigoAsignatura;NEstudiantes;Tipo(L/T);Idioma(EN/ES)");
		for (Group g : groups) {
			String type = g.getClassroomType().equals(
					ClassroomType.LABORATORY) ? "L" : "T";
			String lang = g.getGroupLanguage().equals(
					GroupLanguage.ENGLISH) ? "EN" : "ES";
			String msg = String.format("%s;%s;%d;%s;%s",
					g.getCode(), g.getSubjectFromCode(),
					g.getNumberOfStudents(), type, lang);
			appendLine(sb, msg);
		}
		return sb.toString();
	}

	public String getGroupScheduleData()
	{
		StringBuilder sb = new StringBuilder();
		appendLine(sb, "CodigoGrupo;Dia;HoraInicio;HoraFin");
		for (Group g : groups) {
			for (GroupSchedule gs : g.getAllGroupSchedules()) {
				String day = "";
				switch (gs.getDay()) {
				case MONDAY:
					day = "L";
					break;
				case TUESDAY:
					day = "M";
					break;
				case WEDNESDAY:
					day = "X";
					break;
				case THURSDAY:
					day = "J";
					break;
				case FRIDAY:
					day = "V";
					break;
				}
				String startTime = String.format("%02d.%02d",
						gs.getStart().getHour(),
						gs.getStart().getMinute());
				String endTime = String.format("%02d.%02d",
						gs.getFinish().getHour(),
						gs.getFinish().getMinute());
				String msg = String.format("%s;%s;%s;%s",
						g.getCode(), day, startTime,
						endTime);
				appendLine(sb, msg);
			}
		}
		return sb.toString();
	}

	public String getAcademicWeeksData()
	{
		StringBuilder sb = new StringBuilder();
		appendLine(sb, "﻿CodigoGrupo;SemanasLectivas");
		for (Group g : groups) {
			String weeksMsg = "";
			List<String> weeks = g.getAcademicWeeks();
			for (int i = 0; i < weeks.size(); i++) {
				weeksMsg += weeks.get(i);
				if (i < weeks.size() - 1) {
					weeksMsg += ",";
				}
			}
			String msg = String.format("%s;%s", g.getCode(),
					weeksMsg);
			appendLine(sb, msg);
		}
		return sb.toString();
	}

	private void appendLine(StringBuilder sb, String msg)
	{
		sb.append(msg + "\n");
	}
}

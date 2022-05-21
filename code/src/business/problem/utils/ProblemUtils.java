package business.problem.utils;

import business.problem.model.Classroom;
import business.problem.model.ClassroomType;
import business.problem.model.Group;
import business.problem.model.GroupLanguage;

public class ProblemUtils {

	public static ClassroomType getClassroomTypeFromGroupCode(String code) {

		ClassroomType ct = null;

		switch (getTypeFromGroupCode(code)) {

		case ("T"):
		case ("S"):
			ct = ClassroomType.THEORY;
			break;

		case ("L"):
			ct = ClassroomType.LABORATORY;
			break;

		}

		return ct;

	}

	public static String getNameFromGroupCode(String code) {

		String[] splittedStr = code.split("\\.");
		if (splittedStr.length == 3) {
			return splittedStr[2];
		}
		return "";

	}

	public static String getTypeFromGroupCode(String code) {

		String[] splittedStr = code.split("\\.");
		if (splittedStr.length == 3) {
			return splittedStr[1];
		}
		return "";

	}

	public static String getSubjectFromGroupCode(String code) {

		String[] splittedStr = code.split("\\.");
		if (splittedStr.length == 3) {
			return splittedStr[0];
		}
		return "";

	}

	public static boolean isLabClassroom(Classroom c) {

		if (c.getType().equals(ClassroomType.LABORATORY)) {
			return true;
		}
		return false;

	}

	public static boolean isTheoryClassroom(Classroom c) {

		if (c.getType().equals(ClassroomType.THEORY)) {
			return true;
		}
		return false;

	}

	public static boolean isLabGroup(Group g) {

		if (g.getClassroomType().equals(ClassroomType.LABORATORY)) {
			return true;
		}
		return false;

	}

	public static boolean isTheoryGroup(Group g) {

		if (g.getClassroomType().equals(ClassroomType.THEORY)) {
			return true;
		}
		return false;

	}

	public static boolean isEnglishGroup(Group g) {

		if (g.getGroupLanguage().equals(GroupLanguage.ENGLISH)) {
			return true;
		}
		return false;

	}

	public static boolean isSpanishGroup(Group g) {

		if (g.getGroupLanguage().equals(GroupLanguage.SPANISH)) {
			return true;
		}
		return false;

	}

}

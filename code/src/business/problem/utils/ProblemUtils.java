package business.problem.utils;

import business.problem.model.Classroom;
import business.problem.model.ClassroomType;
import business.problem.model.Group;
import business.problem.model.GroupLanguage;

public class ProblemUtils {

	public static boolean isLabClassroom(Classroom c) {

		if (c.getType().equals(ClassroomType.LABORATORY)) {
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

	public static boolean isEnglishGroup(Group g) {

		if (g.getGroupLanguage().equals(GroupLanguage.ENGLISH)) {
			return true;
		}
		return false;

	}

}

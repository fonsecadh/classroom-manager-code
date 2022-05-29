package persistence.problem.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.gen.model.Preference;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Classroom;
import business.problem.model.ClassroomType;
import business.problem.model.GroupLanguage;
import business.problem.model.Subject;
import persistence.filemanager.FileManager;
import persistence.problem.PreferencesDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class PreferencesDataAccessCsv implements PreferencesDataAccess {
	public static final String CSVNAME = "PREFERENCES";

	@Override
	public Map<String, Preference> loadPreferences(String filename,
			Map<String, Classroom> classrooms,
			Map<String, Subject> subjects, FileManager fileManager)
			throws PersistenceException, InputValidationException
	{
		Map<String, Preference> prefs = new HashMap<String, Preference>();

		List<String> lines = fileManager.readLinesFromFile(filename);
		for (int i = 1; i < lines.size(); i++) { // Ignore header
			lineToPreferences(lines.get(i), i, classrooms, subjects,
					prefs);
		}
		return prefs;
	}

	private void lineToPreferences(String line, int lineNumber,
			Map<String, Classroom> classrooms,
			Map<String, Subject> subjects,
			Map<String, Preference> prefs)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, 4, CSVNAME, lineNumber);

		String subjectCode = fields[0].trim();
		String lang = fields[1].trim();
		String type = fields[2].trim();
		String classroomCode = fields[3].trim();

		validate(subjectCode, lang, type, classroomCode, lineNumber);

		GroupLanguage gl = null;
		switch (lang) {
		case "ES":
			gl = GroupLanguage.SPANISH;
			break;
		case "EN":
			gl = GroupLanguage.ENGLISH;
			break;
		}
		ClassroomType ct = null;
		switch (type) {
		case "T":
			ct = ClassroomType.THEORY;
			break;
		case "L":
			ct = ClassroomType.LABORATORY;
			break;
		}
		Subject s = subjects.get(subjectCode);
		if (s == null) {
			String msg = String.format(
					"Non existing code for subject in %s csv file (%s), line %d",
					CSVNAME, subjectCode, lineNumber);
			throw new InputValidationException(msg);
		}
		Classroom c = classrooms.get(classroomCode);
		if (c == null) {
			String msg = String.format(
					"Non existing code for classroom in %s csv file (%s), line %d",
					CSVNAME, classroomCode, lineNumber);
			throw new InputValidationException(msg);
		}
		Preference p = prefs.get(s.getCode());

		if (p == null)
			p = new Preference();
		if (gl.equals(GroupLanguage.ENGLISH)) {
			if (ct.equals(ClassroomType.LABORATORY))
				p.addEnglishLabPreference(c.getCode());
			else
				p.addEnglishTheoryPreference(c.getCode());
		} else {
			if (ct.equals(ClassroomType.LABORATORY))
				p.addSpanishLabPreference(c.getCode());
			else
				p.addSpanishTheoryPreference(c.getCode());
		}
		prefs.put(s.getCode(), p);
	}

	private void validate(String subjectCode, String lang, String type,
			String classroomCode, int lineNumber)
			throws InputValidationException
	{
		String csvName = CSVNAME;

		// Subject code validation
		ValidationUtils.validateString(subjectCode, csvName,
				lineNumber);

		// Language
		ValidationUtils.validateString(lang, csvName, lineNumber);

		String[] valuesLang = { "EN", "ES" };
		ValidationUtils.validateStringValues(lang, csvName, valuesLang,
				lineNumber);

		// Type validation
		ValidationUtils.validateString(type, csvName, lineNumber);

		String[] valuesType = { "T", "L" };
		ValidationUtils.validateStringValues(type, csvName, valuesType,
				lineNumber);

		// Classroom code validation
		ValidationUtils.validateString(classroomCode, csvName,
				lineNumber);
	}
}

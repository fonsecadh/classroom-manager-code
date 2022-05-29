package business.alg.gen.model;

import java.util.ArrayList;
import java.util.List;

public class Preference {

	private List<String> enTheory;
	private List<String> enLab;
	private List<String> esTheory;
	private List<String> esLab;

	public Preference() {
		this.enTheory = new ArrayList<String>();
		this.enLab = new ArrayList<String>();
		this.esTheory = new ArrayList<String>();
		this.esLab = new ArrayList<String>();
	}

	public List<String> getEnglishTheoryPreferences()
	{
		return new ArrayList<String>(enTheory);
	}

	public List<String> getEnglishLabPreferences()
	{
		return new ArrayList<String>(enLab);
	}

	public List<String> getSpanishTheoryPreferences()
	{
		return new ArrayList<String>(esTheory);
	}

	public List<String> getSpanishLabPreferences()
	{
		return new ArrayList<String>(esLab);
	}

	public void addEnglishTheoryPreference(String classroomCode)
	{
		this.enTheory.add(classroomCode);
	}

	public void addEnglishLabPreference(String classroomCode)
	{
		this.enLab.add(classroomCode);
	}

	public void addSpanishTheoryPreference(String classroomCode)
	{
		this.esTheory.add(classroomCode);
	}

	public void addSpanishLabPreference(String classroomCode)
	{
		this.esLab.add(classroomCode);
	}

}

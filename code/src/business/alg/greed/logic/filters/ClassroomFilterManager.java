package business.alg.greed.logic.filters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.alg.gen.model.Preference;
import business.alg.gen.model.PreferenceType;
import business.problem.model.Classroom;
import business.problem.model.Group;

/**
 * Manager of the Lazy Filter Dictionary (LFD).
 * 
 * @author Hugo Fonseca Díaz
 *
 */
public class ClassroomFilterManager {
	private List<ClassroomFilter> filters;
	private List<Classroom> classrooms;
	private Map<String, List<Preference>> preferences;
	/**
	 * Map with the filtered classrooms for each group. <br>
	 * <br>
	 * Key: The group ID. <br>
	 * Value: A list with the filtered classrooms;
	 */
	private Map<String, List<Classroom>> mapFiltered;
	private boolean preferenceBias;

	public ClassroomFilterManager(List<ClassroomFilter> classroomFilters,
			List<Classroom> classrooms,
			Map<String, List<Preference>> preferences,
			boolean preferenceBias) {
		this.filters = new ArrayList<ClassroomFilter>(classroomFilters);
		this.classrooms = new ArrayList<Classroom>(classrooms);
		this.mapFiltered = new HashMap<String, List<Classroom>>();
		this.preferences = new HashMap<String, List<Preference>>(
				preferences);
		this.preferenceBias = preferenceBias;
	}

	/**
	 * Filters the classrooms ({@link Classroom}) valid for a given group
	 * ({@link Group}). This method applies all the filters configured by
	 * the prototype, and only executes the filter operation the first time
	 * for each group, storing the result in the LFD.
	 * 
	 * @param group      The group.
	 * @param classrooms The list of classrooms to be filtered.
	 * @return The list of filtered classrooms.
	 */
	public List<Classroom> filterClassroomsFor(Group g)
	{
		List<Classroom> fc = mapFiltered.get(g.getCode());
		if (fc == null) {
			fc = new ArrayList<Classroom>(classrooms);
			for (ClassroomFilter cf : filters) {
				fc = cf.filterByGroup(g, fc);
			}
			// If a group has preferences and the greedy is biased
			// Order is positive prefs -> no prefs -> negative prefs
			List<Preference> prefs = preferences.get(g.getCode());
			if (prefs != null && prefs.size() > 0
					&& preferenceBias) {
				List<Classroom> cPos = prefs.stream()
						.filter(p -> p.getType().equals(
								PreferenceType.POSITIVE))
						.map(p -> p.getClassroom())
						.collect(Collectors.toList());

				List<Classroom> cNeg = prefs.stream()
						.filter(p -> p.getType().equals(
								PreferenceType.NEGATIVE))
						.map(p -> p.getClassroom())
						.collect(Collectors.toList());

				List<Classroom> first = new ArrayList<Classroom>();
				List<Classroom> middle = new ArrayList<Classroom>();
				List<Classroom> last = new ArrayList<Classroom>();
				for (Classroom c : fc) {
					if (cNeg.contains(c)) {
						last.add(c);
					} else if (cPos.contains(c)) {
						first.add(c);
					} else {
						middle.add(c);
					}
				}
				Comparator<Classroom> comp = Comparator
						.comparing(Classroom::getNumberOfSeats);
				first.sort(comp);
				middle.sort(comp);
				last.sort(comp);

				fc.clear();
				for (Classroom c : first)
					fc.add(c);
				for (Classroom c : middle)
					fc.add(c);
				for (Classroom c : last)
					fc.add(c);
			} else { // If a group does not have preferences
				fc.sort(Comparator.comparing(
						Classroom::getNumberOfSeats));
			}
			mapFiltered.put(g.getCode(), fc);
		}
		return new ArrayList<Classroom>(fc);
	}
}

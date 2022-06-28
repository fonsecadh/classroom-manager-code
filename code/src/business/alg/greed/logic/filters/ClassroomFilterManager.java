package business.alg.greed.logic.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	/**
	 * Map with the filtered classrooms for each group. <br>
	 * <br>
	 * Key: The group ID. <br>
	 * Value: A list with the filtered classrooms;
	 */
	private Map<String, List<Classroom>> mapFiltered;

	public ClassroomFilterManager(List<ClassroomFilter> classroomFilters,
			List<Classroom> classrooms) {
		this.filters = new ArrayList<ClassroomFilter>(classroomFilters);
		this.classrooms = new ArrayList<Classroom>(classrooms);
		this.mapFiltered = new HashMap<String, List<Classroom>>();
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
			Collections.sort(fc, (c1, c2) -> c1.getNumberOfSeats()
					- c2.getNumberOfSeats());
			mapFiltered.put(g.getCode(), fc);
		}
		return new ArrayList<Classroom>(fc);
	}
}

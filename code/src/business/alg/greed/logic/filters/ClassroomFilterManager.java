package business.alg.greed.logic.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.problem.Classroom;
import business.problem.Group;

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

	public ClassroomFilterManager(List<ClassroomFilter> classroomFilters, List<Classroom> classrooms) {
		this.filters = new ArrayList<ClassroomFilter>(classroomFilters);
		this.classrooms = new ArrayList<Classroom>(classrooms);
		this.mapFiltered = new HashMap<String, List<Classroom>>();
	}

	public List<Classroom> filterClassroomsFor(Group g) {
		List<Classroom> fc = mapFiltered.get(g.getCode());
		if (fc == null) {
			fc = new ArrayList<Classroom>(classrooms);
			for (ClassroomFilter cf : filters) {
				fc = cf.filterByGroup(g, fc);
			}
			Collections.sort(fc, (c1, c2) -> c1.getNumberOfSeats() - c2.getNumberOfSeats());
			mapFiltered.put(g.getCode(), fc);
		}
		return new ArrayList<Classroom>(fc);
	}

}

package business.alg.greed.logic.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.alg.greed.model.Restriction;
import business.alg.greed.model.RestrictionType;
import business.problem.model.Classroom;
import business.problem.model.Group;

public class RestrictionFilter implements ClassroomFilter {
	private Map<String, List<Restriction>> rMap;

	public RestrictionFilter(Map<String, List<Restriction>> restrictions) {
		this.rMap = new HashMap<String, List<Restriction>>(
				restrictions);
	}

	@Override
	public List<Classroom> filterByGroup(Group group,
			List<Classroom> classrooms)
	{
		List<Restriction> rList = rMap.get(group.getCode());

		// If there are no restrictions for this group
		if (rList == null)
			return new ArrayList<Classroom>(classrooms);

		// If there are restrictions for this group
		List<Classroom> negativeClassrooms = rList.stream()
				.filter(r -> r.getType().equals(
						RestrictionType.NEGATIVE))
				.map(r -> r.getClassroom())
				.collect(Collectors.toList());

		List<Classroom> positiveClassrooms = rList.stream()
				.filter(r -> r.getType().equals(
						RestrictionType.POSITIVE))
				.map(r -> r.getClassroom())
				.collect(Collectors.toList());

		return classrooms.stream()
				.filter(c -> !negativeClassrooms.contains(c))
				.filter(c -> positiveClassrooms.contains(c))
				.collect(Collectors.toList());
	}
}

package business.classfinder.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.alg.greed.model.Assignment;
import business.classfinder.model.ClassfinderQuery;
import business.classfinder.model.QueryResult;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.schedule.Day;
import business.problem.model.schedule.GroupSchedule;

public class Classfinder {
	private List<ClassfinderQuery> queries;
	private List<Classroom> classrooms;
	/**
	 * Map with the assignments related to each classroom. <br>
	 * <br>
	 * Key: Classroom ID. <br>
	 * Value: List of assignments ({@link Assignment}) related to that
	 * classroom ({@link Classroom}).
	 */
	private Map<String, List<Assignment>> classroomAssignmentMap;

	public Classfinder(List<ClassfinderQuery> queries,
			List<Classroom> classrooms,
			Map<String, List<Assignment>> classroomAssignmentMap) {
		this.queries = new ArrayList<ClassfinderQuery>(queries);
		this.classrooms = new ArrayList<Classroom>(classrooms);
		this.classroomAssignmentMap = new HashMap<String, List<Assignment>>(
				classroomAssignmentMap);
	}

	public String getAllQueryResults()
	{
		StringBuilder sb = new StringBuilder();

		appendTitle(sb, "Classfinder Query Results");
		for (ClassfinderQuery q : queries) {
			appendLine(sb, q.toString());

			List<QueryResult> qrList = getResultsForQuery(q);

			appendText(sb, "Result: ");
			for (int i = 0; i < qrList.size(); i++) {
				appendText(sb, qrList.get(i).toString());
				if (i == qrList.size() - 1)
					appendText(sb, ", ");
			}
			appendNewLine(sb);
		}
		appendNewLine(sb);
		return sb.toString();
	}

	private List<QueryResult> getResultsForQuery(ClassfinderQuery q)
	{
		List<QueryResult> qrList = new ArrayList<QueryResult>();
		List<Classroom> cList = classrooms.stream().filter(
				c -> q.getClassroomType().equals(c.getType()))
				.filter(c -> q.getNumberOfAttendants() <= c
						.getNumberOfSeats())
				.collect(Collectors.toList());
		for (int i = 0; i < cList.size(); i++) {
			Classroom c = cList.get(i);
			addQueryResultsForClassroom(c, q, qrList);
		}
		return qrList;
	}

	private void addQueryResultsForClassroom(Classroom c,
			ClassfinderQuery q, List<QueryResult> qrList)
	{
		List<String> weeks = q.getAcademicWeeks();
		Map<String, List<Day>> weekDaysMap = q.getWeekDaysToMap();
		List<Assignment> aList = classroomAssignmentMap
				.get(c.getCode());
		if (aList == null)
			aList = new ArrayList<Assignment>();

		List<Group> gList = aList.stream().map(a -> a.getGroup())
				.collect(Collectors.toList());

		weekloop: for (String w : weeks) {
			List<Day> days = weekDaysMap.get(w);
			if (days != null) {
				for (Day d : days) {
					QueryResult qr = createResult(c, q, w,
							d, gList);
					if (qr.isValid()) {
						qrList.add(qr);
						boolean cond = qrList
								.size() == q.getNumberOfResults();
						if (cond) {
							break weekloop;
						}
					}
				}
			}
		}
	}

	private QueryResult createResult(Classroom c, ClassfinderQuery q,
			String w, Day d, List<Group> gList)
	{
		QueryResult qr = new QueryResult(q, c, d, w);

		List<Group> filtered = new ArrayList<Group>();
		for (Group g : gList) {
			if (g.getAcademicWeeks().contains(w))
				filtered.add(g);
		}
		for (Group g : filtered) {
			for (GroupSchedule gs : g.getAllGroupSchedules()) {
				if (gs.getDay().equals(d)) {
					qr.addCollisionToScheduleMap(gs);
				}
			}
		}
		return qr;
	}

	private void appendTitle(StringBuilder sb, String msg)
	{
		sb.append(msg + "\n");
		for (int i = 0; i < msg.length(); i++) {
			sb.append("=");
		}
		sb.append("\n");
	}

	private void appendLine(StringBuilder sb, String msg)
	{
		sb.append(msg + "\n");
	}

	private void appendNewLine(StringBuilder sb)
	{
		sb.append("\n");
	}

	private void appendText(StringBuilder sb, String msg)
	{
		sb.append(msg);
	}
}

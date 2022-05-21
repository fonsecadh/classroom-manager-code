package business.classfinder.logic;

import java.util.ArrayList;
import java.util.List;

import business.classfinder.model.ClassfinderQuery;

public class Classfinder {

	private List<ClassfinderQuery> queries;

	public Classfinder(List<ClassfinderQuery> queries) {
		this.queries = new ArrayList<ClassfinderQuery>(queries);
	}

	public String queryResults() {

		StringBuilder sb = new StringBuilder();
		
		appendTitle(sb, "Classfinder Query Results");

		for (ClassfinderQuery q : queries) {
			
			appendLine(sb, q.toString());
			
			

		}

		return sb.toString();

	}

	private void appendTitle(StringBuilder sb, String msg) {
		sb.append(msg + "\n");
		for (int i = 0; i < msg.length(); i++) {
			sb.append("=");
		}
		sb.append("\n");
	}

	private void appendLine(StringBuilder sb, String msg) {
		sb.append(msg + "\n");
	}

	private void appendNewLine(StringBuilder sb) {
		sb.append("\n");
	}

}

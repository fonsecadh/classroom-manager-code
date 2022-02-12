package business.alg;

public class GreedyAlgorithm {
	private int nGroups;
	private int nClassrooms;
	
	public boolean[][] greedyAlgorithm(boolean[][] assignments, double[][] costs) {
		for (int i = 0; i < nGroups; i++) {
			for (int j = 0; j < nClassrooms; j++) {
				assignments[i][j] = false;			}
		}
		
		for (int i = 0; i < nGroups; i++) {
			assignments[i][bestClass(costs, assignments, i)] = true;
		}
		
		return assignments;
	}
	
	private int bestClass(double[][] costs, boolean[][] assignments, int group) {
		double min = Double.POSITIVE_INFINITY;
		int bestClass = -1;
		
		for (int j = 0; j < nClassrooms; j++) {
			if (!classAlreadyChosen(assignments, group, j) && costs[group][j] < min) {
				min = costs[group][j];
				bestClass = j;
			}
		}
		
		return bestClass;
	}

	private boolean classAlreadyChosen(boolean[][] assignments, int group, int classroom) {
		for (int i = 0; i < group - 1; i++) {
			if (assignments[i][classroom]) {
				return true;
			}
		}
		return false;
	}
}

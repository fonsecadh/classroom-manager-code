package business.alg.gen.model;

public class GenAlgoMetric {

	private int generation;
	private double bestFitness;
	private double time;

	public GenAlgoMetric(int generation, double bestFitness, double time) {
		this.generation = generation;
		this.bestFitness = bestFitness;
		this.time = time;
	}

	public int getGeneration() {
		return generation;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public double getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "Generation = " + generation + ", best fitness = " + bestFitness + ", time (s) = " + (time / 1000);
	}

}

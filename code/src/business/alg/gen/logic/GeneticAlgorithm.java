package business.alg.gen.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import business.alg.gen.logic.fitness.FitnessFunction;
import business.alg.gen.model.Individual;
import business.alg.gen.model.Metrics;
import business.loghandler.LogHandler;
import ui.CommandLineInterface;

/**
 * Models a genetic algorithm with its operators.
 * 
 * @author Hugo Fonseca Díaz
 *
 */
public class GeneticAlgorithm {
	private int popSize;
	private int individualLength;
	private double crossoverProb;
	private double mutationProb;
	private long maxTimeMs;
	private int nGenerations;
	private FitnessFunction fn;
	private IndividualManager im;
	private int showGenInfo;
	private Random random;
	private Metrics metrics;

	/**
	 * Creates a Genetic Algorithm given its parameters.
	 * 
	 * @param individualLength     Length of the individual
	 *                             ({@link Individual}).
	 * @param populationSize       Size of the population.
	 * @param mutationProbability  The mutation probability.
	 * @param crossoverProbability The crossover probability.
	 * @param maxTimeMilliseconds  The maximum time for the execution of the
	 *                             algorithm, in milliseconds.
	 * @param numberOfGenerations  The maximum number of generations for the
	 *                             execution of the algorithm.
	 * @param fitnessFunction      The fitness function
	 *                             ({@link FitnessFunction}) of the
	 *                             algorithm.
	 * @param individualManager    A manager ({@link IndividualManager}) for
	 *                             the individual operations.
	 * @param showGenInfo 
	 */
	public GeneticAlgorithm(int individualLength, int populationSize,
			double mutationProbability, double crossoverProbability,
			long maxTimeMilliseconds, int numberOfGenerations,
			FitnessFunction fitnessFunction,
			IndividualManager individualManager, int showGenInfo) {
		this.individualLength = individualLength;
		this.popSize = populationSize;
		this.mutationProb = mutationProbability;
		this.crossoverProb = crossoverProbability;
		this.maxTimeMs = maxTimeMilliseconds;
		this.nGenerations = numberOfGenerations;
		this.fn = fitnessFunction;
		this.im = individualManager;
		this.showGenInfo = showGenInfo;
		this.random = new Random();
		this.metrics = Metrics.getInstance();
	}

	/**
	 * The objective of this algorithm is to generate a given number of
	 * generations of individuals and return the individual with the
	 * greatest fitness value at the end of the execution.
	 * 
	 * @return The individual ({@link Individual}) with the best fitness
	 *         value.
	 */
	public Individual geneticAlgorithm()
	{
		// CLI and LOG
		CommandLineInterface cli = CommandLineInterface.getInstance();
		LogHandler logh = LogHandler.getInstance();

		String parametersMsg = "Parameters: \n"
				+ "\n\t-> Max number of generations: "
				+ nGenerations + "\n\t-> Max time (ms): "
				+ maxTimeMs + "\n\t-> Mutation probability: "
				+ mutationProb
				+ "\n\t-> Crossover probability: "
				+ crossoverProb + "\n\t-> Population size: "
				+ popSize;
		String parametersMsgExtended = parametersMsg
				+ "\n\t-> Fitness function: "
				+ fn.getClass().getName()
				+ "\n\t-> Individual length: "
				+ individualLength;

		cli.showMessage("START Executing Genetic Algorithm...");
		cli.showNewLine();
		cli.showMessage(parametersMsg);
		cli.showNewLine();

		logh.log(Level.FINE, GeneticAlgorithm.class.getName(),
				"geneticAlgorithm", "START Genetic Algorithm");
		logh.log(Level.FINER, GeneticAlgorithm.class.getName(),
				"geneticAlgorithm", parametersMsgExtended);

		// Time variables
		long startTime = System.currentTimeMillis();
		long currentTime;
		long totalTime;

		// The algorithm starts
		List<Individual> population = new ArrayList<Individual>(
				initialPopulation());
		Individual bestIndividual = bestIndividual(population);

		int gen = 0;
		do {
			// Core of the genetic algorithm
			population = nextGeneration(population);
			bestIndividual = bestIndividual(population);
			++gen;

			currentTime = System.currentTimeMillis();
			totalTime = currentTime - startTime;

			// Metrics for the command line interface
			if (showGenInfo != 0 && (gen == 1 || gen % showGenInfo == 0)) {
				cli.showMessage(String.format(
						"Generation = %d, best fitness = %.2f, average fitness = %.2f, current time (s) = %.2f",
						gen,
						metrics.getDouble(
								"BEST_FITNESS"),
						metrics.getDouble(
								"AVG_FITNESS"),
						(totalTime / 1000.0)));
			}
		} while (gen < nGenerations && totalTime < maxTimeMs);
		// CLI and LOG
		cli.showNewLine();
		cli.showMessage("END Genetic Algorithm");
		cli.showNewLine();
		logh.log(Level.FINE, GeneticAlgorithm.class.getName(),
				"geneticAlgorithm", "END Genetic Algorithm");

		return bestIndividual;
	}

	private Individual bestIndividual(List<Individual> population)
	{
		Individual bestIndividual = null;
		double bestFitness = Double.NEGATIVE_INFINITY;
		double avgFitness = 0.0;
		for (Individual individual : population) {
			double fValue = fitnessFunction(individual);
			avgFitness += fValue;
			if (fValue > bestFitness) {
				bestIndividual = individual;
				bestFitness = fValue;
			}
		}
		avgFitness = avgFitness / population.size();

		metrics.set("BEST_FITNESS", bestFitness);
		metrics.set("AVG_FITNESS", avgFitness);
		return bestIndividual;
	}

	private List<Individual> nextGeneration(List<Individual> population)
	{
		List<Individual> newPopulation, oldPopulation;
		newPopulation = new ArrayList<Individual>();
		oldPopulation = new ArrayList<Individual>(population);
		while (!oldPopulation.isEmpty()) {
			Individual p1, p2, c1, c2; // Parents and children

			// Selection
			// Note: SELECTED elements are REMOVED from POPULATION
			p1 = selection(oldPopulation);
			p2 = selection(oldPopulation);

			c1 = p1;
			c2 = p2;

			// Crossover
			if (crossoverProb >= random.nextDouble()) {
				c1 = crossover(p1, p2);
				c2 = crossover(p1, p2);
			}
			// Mutation
			if (mutationProb >= random.nextDouble()) {
				c1 = mutation(c1);
				c2 = mutation(c2);
			}
			// Tournament
			Individual[] best = tournament(p1, p2, c1, c2);
			Individual w1 = new Individual(
					best[0].getRepresentation());
			Individual w2 = new Individual(
					best[1].getRepresentation());
			newPopulation.add(w1);
			newPopulation.add(w2);
		}
		return newPopulation;
	}

	private double fitnessFunction(Individual individual)
	{
		if (individual.getFitness() == Double.NEGATIVE_INFINITY) {
			individual.setFitness(fn.fitnessFunction(individual));
		}
		return individual.getFitness();
	}

	private Set<Individual> initialPopulation()
	{
		Map<String, Boolean> uniqueMap = new HashMap<String, Boolean>();
		Set<Individual> p = new HashSet<Individual>();
		while (p.size() < popSize) {
			Individual ran = im.generateRandomIndividual();
			String key = "";
			for (int i = 0; i < ran.getRepresentation()
					.size(); i++) {
				key += ran.getRepresentation().get(i) + "@";
			}
			if (uniqueMap.get(key) == null) {
				uniqueMap.put(key, false);
				p.add(ran);
			}
		}
		return p;
	}

	private Individual selection(List<Individual> population)
	{
		int index = random.nextInt(population.size());
		Individual selected = population.get(index);
		population.remove(index);
		return selected;
	}

	private Individual crossover(Individual firstParent,
			Individual secondParent)
	{
		int p1 = randomOffset(individualLength);
		int p2 = randomOffset(individualLength);

		List<String> xArray = firstParent.getRepresentation();
		List<String> yArray = secondParent.getRepresentation();
		List<String> offArray = new ArrayList<String>(xArray);

		// Keep the substring from p1 to p2-1 to the offspring, order
		// and position
		// The remaining genes, p2 to p1-1, from the second parent,
		// relative order
		int k = p2;
		for (int i = 0; i < individualLength; i++) {
			int j = p1;
			while (j < p2 + (p2 <= p1 ? individualLength : 0)
					&& yArray.get(i) != xArray.get(
							j % individualLength)) {
				j++;
			}
			if (j == p2 + (p2 <= p1 ? individualLength : 0)) {
				// yArray[i] is not in offArray from p1 to p2-1
				offArray.set(k % individualLength,
						yArray.get(i));
				k++;
			}
		}
		return new Individual(offArray);
	}

	private Individual mutation(Individual child)
	{
		int p1 = randomOffset(individualLength - 1);
		int p2 = randomOffset(individualLength - 1);
		while (p1 == p2) {
			p2 = randomOffset(individualLength);
		}
		List<String> mutatedRepresentation = new ArrayList<String>(
				child.getRepresentation());

		// We swap two random points
		String tmp = mutatedRepresentation.get(p1);
		mutatedRepresentation.set(p1, mutatedRepresentation.get(p2));
		mutatedRepresentation.set(p2, tmp);

		return new Individual(mutatedRepresentation);
	}

	private Individual[] tournament(Individual a, Individual b,
			Individual c, Individual d)
	{
		List<Individual> iList = new ArrayList<Individual>();

		iList.add(a);
		iList.add(b);
		iList.add(c);
		iList.add(d);

		iList.sort((i1, i2) -> Double.compare(fitnessFunction(i2),
				fitnessFunction(i1)));

		Individual[] best = { iList.get(0), iList.get(1) };
		return best;
	}

	private int randomOffset(int length)
	{
		return random.nextInt(length);
	}
}

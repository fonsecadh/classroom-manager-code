package business.alg.gen.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import business.alg.gen.logic.fitness.FitnessFunction;
import business.alg.gen.model.GenAlgoMetric;
import business.alg.gen.model.Individual;
import business.alg.gen.utils.GenAlgoUtils;
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

	private double mutationProb;

	private long maxTimeMs;
	private int nGenerations;

	private FitnessFunction fn;

	private Random random;

	/**
	 * The greatest fitness of each generation. Used for the metrics.
	 */
	private double genBestFitness;

	/**
	 * Creates a Genetic Algorithm given its parameters.
	 * 
	 * @param individualLength    Length of the individual ({@link Individual}).
	 * @param populationSize      Size of the population.
	 * @param mutationProbability The mutation probability.
	 * @param maxTimeMilliseconds The maximum time for the execution of the
	 *                            algorithm, in milliseconds.
	 * @param numberOfGenerations The maximum number of generations for the
	 *                            execution of the algorithm.
	 * @param fitnessFunction     The fitness function of the algorithm.
	 */
	public GeneticAlgorithm(int individualLength, int populationSize, double mutationProbability,
			long maxTimeMilliseconds, int numberOfGenerations, FitnessFunction fitnessFunction) {
		this.individualLength = individualLength;
		this.popSize = populationSize;
		this.mutationProb = mutationProbability;
		this.maxTimeMs = maxTimeMilliseconds;
		this.nGenerations = numberOfGenerations;
		this.fn = fitnessFunction;
		this.random = new Random();
	}

	/**
	 * The objective of this algorithm is to generate a given number of generations
	 * of individuals and return the individual with the greatest fitness value at
	 * the end of the execution.
	 * 
	 * @return The individual ({@link Individual}) with the best fitness value.
	 */
	public Individual geneticAlgorithm() {

		// CLI and LOG
		CommandLineInterface cli = CommandLineInterface.getInstance();
		LogHandler logh = LogHandler.getInstance();

		String parametersMsg = "Max number of generations: " + nGenerations + ", max time (ms): " + maxTimeMs
				+ ", mutation probability: " + mutationProb + ", population size: " + popSize;
		String parametersMsgExtended = parametersMsg + ", fitness function: " + fn.getClass().getName()
				+ ", individual length: " + individualLength;

		cli.showMessage("START Executing Genetic Algorithm...");
		cli.showMessage(parametersMsg);

		logh.log(Level.FINE, GeneticAlgorithm.class.getName(), "geneticAlgorithm", "START Genetic Algorithm");
		logh.log(Level.FINER, GeneticAlgorithm.class.getName(), "geneticAlgorithm", parametersMsgExtended);

		// Time variables
		long startTime = System.currentTimeMillis();
		long currentTime;
		long totalTime;
		long genTime;

		// The algorithm starts
		List<Individual> population = new ArrayList<>(initialPopulation());
		Individual bestIndividual = bestIndividual(population);

		int gen = 0;

		do {

			genTime = System.currentTimeMillis();

			// Core of the genetic algorithm
			population = nextGeneration(population, bestIndividual);
			bestIndividual = bestIndividual(population);
			++gen;

			currentTime = System.currentTimeMillis();
			genTime = currentTime - genTime;
			totalTime = currentTime - startTime;

			// Metrics for the command line interface
			GenAlgoMetric m = new GenAlgoMetric(gen, genBestFitness, genTime);
			cli.showMessage(m.toString());

		} while (gen < nGenerations && totalTime < maxTimeMs);

		// CLI and LOG
		cli.showMessage("END Genetic Algorithm");
		logh.log(Level.FINE, GeneticAlgorithm.class.getName(), "geneticAlgorithm", "START Genetic Algorithm");

		return bestIndividual;

	}

	private Individual bestIndividual(List<Individual> population) {
		Individual bestIndividual = null;
		double bestFitness = Double.NEGATIVE_INFINITY;

		for (Individual individual : population) {
			double fValue = fitnessFunction(individual);
			if (fValue > bestFitness) {
				bestIndividual = individual;
				bestFitness = fValue;
			}
		}

		this.genBestFitness = bestFitness;
		return bestIndividual;
	}

	private List<Individual> nextGeneration(List<Individual> population, Individual bestBefore) {
		List<Individual> newPopulation = new ArrayList<Individual>(population.size());
		for (int i = 0; i < population.size() - 1; i++) {
			Individual x = selection(population);
			Individual y = selection(population);
			Individual child = crossover(x, y);
			if (random.nextDouble() <= mutationProb) {
				child = mutation(child);
			}
			newPopulation.add(child);
		}
		// Elitism
		newPopulation.add(bestBefore);
		return newPopulation;
	}

	private double fitnessFunction(Individual individual) {
		return fn.fitnessFunction(individual);
	}

	private Set<Individual> initialPopulation() {
		Set<Individual> p = new HashSet<Individual>();
		while (p.size() < popSize) {
			p.add(GenAlgoUtils.generateRandomIndividual(individualLength));
		}
		return p;
	}

	private Individual selection(List<Individual> population) {
		return population.get(random.nextInt(population.size()));
	}

	private Individual crossover(Individual firstParent, Individual secondParent) {
		int p1 = randomOffset(individualLength);
		int p2 = randomOffset(individualLength);
		List<Integer> xArray = firstParent.getRepresentation();
		List<Integer> yArray = secondParent.getRepresentation();
		List<Integer> offArray = new ArrayList<Integer>(xArray);

		// Keep the substring from p1 to p2-1 to the offspring, order and position
		// The remaining genes, p2 to p1-1, from the second parent, relative order
		int k = p2;
		for (int i = 0; i < individualLength; i++) {
			int j = p1;
			while (j < p2 + (p2 <= p1 ? individualLength : 0) && yArray.get(i) != xArray.get(j % individualLength)) {
				j++;
			}
			if (j == p2 + (p2 <= p1 ? individualLength : 0)) {
				// yArray[i] is not in offArray from p1 to p2-1
				offArray.set(k % individualLength, yArray.get(i));
				k++;
			}
		}
		return new Individual(offArray);
	}

	private Individual mutation(Individual child) {
		int p1 = randomOffset(individualLength - 1);
		int p2 = randomOffset(individualLength - 1);
		while (p1 == p2) {
			p2 = randomOffset(individualLength);
		}

		List<Integer> mutatedRepresentation = new ArrayList<Integer>(child.getRepresentation());

		// We swap two random points
		Integer tmp = mutatedRepresentation.get(p1);
		mutatedRepresentation.set(p1, mutatedRepresentation.get(p2));
		mutatedRepresentation.set(p2, tmp);

		return new Individual(mutatedRepresentation);
	}

	private int randomOffset(int length) {
		return random.nextInt(length);
	}

}

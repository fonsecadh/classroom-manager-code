package business.alg.gen.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import business.alg.gen.logic.fitness.FitnessFunction;
import business.alg.gen.model.Individual;
import business.utils.GenAlgoUtils;

public class GeneticAlgorithm {

	private int popSize;

	private int individualLength;

	private double mutationProb;
	private long maxTimeMs;
	
	private int nGenerations;

	private FitnessFunction fn;

	private Random random;

	public GeneticAlgorithm(
			int individualLength, 
			int populationSize, 
			double mutationProbability,
			long maxTimeMilliseconds,
			int numberOfGenerations,
			FitnessFunction fitnessFunction
	) {
		this.individualLength = individualLength;
		this.popSize = populationSize;
		this.mutationProb = mutationProbability;
		this.maxTimeMs = maxTimeMilliseconds;
		this.nGenerations = numberOfGenerations;
		this.fn = fitnessFunction;
		this.random = new Random();
	}

	public Individual geneticAlgorithm() {
		List<Individual> population = new ArrayList<>(initialPopulation());
		Individual bestIndividual = bestIndividual(population);

		long startTime = System.currentTimeMillis();
		long currentTime;
		long totalTime;
		
		int gen = 0;

		do {
			population = nextGeneration(population, bestIndividual);
			bestIndividual = bestIndividual(population);

			++gen;

			currentTime = System.currentTimeMillis();
			totalTime = currentTime - startTime;
		} while (gen < nGenerations && totalTime < maxTimeMs);

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
		return null;
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

		return bestIndividual;
	}

	private int randomOffset(int length) {
		return random.nextInt(length);
	}

}

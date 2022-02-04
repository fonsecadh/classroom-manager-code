package business.alg;

import java.util.HashSet;
import java.util.Set;

import business.entities.alg.Individual;
import business.utils.GenAlgoUtils;

public class GeneticAlgorithm {
	private int nGroups;
	private int nClassrooms;
	private int populationSize;
	
	public Individual geneticAlgorithm() {
		Individual bestIndividual = null;
		return bestIndividual;
	}
	
	private double fitnessFunction(Individual individual) {
		return 0.0;
	}
	
	private Set<Individual> initialPopulation() {
		Set<Individual> p = new HashSet<Individual>();
		while (p.size() < populationSize) {
			p.add(GenAlgoUtils.generateRandomIndividual(nGroups, nClassrooms));
		}
		return p;
	}
	
	private void selection() {
		
	}
	
	private void crossover() {
		
	}
	
	private void mutation() {
		
	}
}

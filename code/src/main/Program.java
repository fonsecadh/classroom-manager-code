package main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import business.alg.gen.logic.GeneticAlgorithm;
import business.alg.gen.logic.fitness.DefaultFitnessFunction;
import business.alg.gen.logic.fitness.FitnessFunction;
import business.alg.gen.logic.fitness.values.FitnessValue;
import business.alg.gen.model.Individual;
import business.alg.greed.logic.Decoder;
import business.alg.greed.logic.GreedyAlgorithm;
import business.alg.greed.logic.filters.ClassroomFilter;
import business.alg.greed.logic.filters.ClassroomFilterManager;
import business.alg.greed.model.Assignment;
import business.errorhandler.ErrorHandler;
import business.loghandler.LogHandler;
import business.problem.Classroom;
import business.problem.Group;

public class Program {

	public static void main(String[] args) {

		try {

			// Persistence
			// TODO: Retrieve configuration and data from files
			List<Group> groups = new ArrayList<Group>();
			List<Classroom> classrooms = new ArrayList<Classroom>();

			List<ClassroomFilter> classroomFilters = new ArrayList<ClassroomFilter>();
			List<FitnessValue> fitnessValues = new ArrayList<FitnessValue>();

			int individualLength = groups.size();
			int populationSize = 100;
			double mutationProbability = 0.3;
			long maxTimeMilliseconds = 30000;
			int numberOfGenerations = 20;

			// Business logic
			ClassroomFilterManager cfm = new ClassroomFilterManager(classroomFilters, classrooms);
			GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(cfm);

			Decoder decoder = new Decoder();
			for (Group g : groups) {
				decoder.putMasterAssignment(g.getId(), new Assignment(g));
			}

			FitnessFunction fitnessFunction = new DefaultFitnessFunction(decoder, greedyAlgo, fitnessValues);

			GeneticAlgorithm genAlgo = new GeneticAlgorithm(individualLength, populationSize, mutationProbability,
					maxTimeMilliseconds, numberOfGenerations, fitnessFunction);

			Individual bestIndividual = genAlgo.geneticAlgorithm();
			System.out.println(bestIndividual.toString());

		} catch (Exception e) {

			LogHandler.getInstance().log(Level.SEVERE, Program.class.getName(), "main", e.getLocalizedMessage(), e);
			ErrorHandler.getInstance().addError(e);

		} finally {

			if (ErrorHandler.getInstance().anyErrors())
				ErrorHandler.getInstance().showErrors(System.out);

		}
	}

}

package main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import business.alg.gen.logic.GeneticAlgorithm;
import business.alg.gen.logic.fitness.DefaultFitnessFunction;
import business.alg.gen.logic.fitness.FitnessFunction;
import business.alg.gen.logic.fitness.values.CollisionsFitnessValue;
import business.alg.gen.logic.fitness.values.FitnessValue;
import business.alg.gen.logic.fitness.values.FreeLabsFitnessValue;
import business.alg.gen.model.Individual;
import business.alg.greed.logic.Decoder;
import business.alg.greed.logic.GreedyAlgorithm;
import business.alg.greed.logic.collisions.CollisionManager;
import business.alg.greed.logic.filters.CapacityFilter;
import business.alg.greed.logic.filters.ClassTypeFilter;
import business.alg.greed.logic.filters.ClassroomFilter;
import business.alg.greed.logic.filters.ClassroomFilterManager;
import business.alg.greed.model.Assignment;
import business.errorhandler.logic.ErrorHandler;
import business.errorhandler.model.ErrorType;
import business.loghandler.LogHandler;
import business.problem.Classroom;
import business.problem.Group;
import business.problem.Subject;
import business.problem.schedule.GroupSchedule;
import ui.CommandLineInterface;

public class Program {

	public static void main(String[] args) {

		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();
		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();

		try {

			cli.showProgramDetails();

			// Persistence
			cli.showMessage("START Processing input files...");
			logh.log(Level.FINE, Program.class.getName(), "main", "START Persistence logic");

			// TODO: Retrieve configuration and data from files
			List<Subject> subjects = new ArrayList<Subject>();
			List<Classroom> classrooms = new ArrayList<Classroom>();
			List<Group> groups = new ArrayList<Group>();
			List<GroupSchedule> schedules = new ArrayList<GroupSchedule>();

			// Genetic parameters
			int individualLength = groups.size();
			int populationSize = 100;
			double mutationProbability = 0.3;
			long maxTimeMilliseconds = 30000;
			int numberOfGenerations = 20;

			// Fitness weights
			double collisionsFnWeight = 0.7;
			double freeLabsFnWeight = 0.2;

			// Persistence errors
			if (errh.anyErrors()) {
				errh.getCustomErrorMessages().forEach(e -> cli.showError(e));
				cli.showEndOfProgramWithErrors();
				return;
			}

			cli.showMessage("END Processing input files...");
			logh.log(Level.FINE, Program.class.getName(), "main", "END Persistence logic");

			// Business logic
			logh.log(Level.FINE, Program.class.getName(), "main", "START Business logic");

			List<ClassroomFilter> classroomFilters = new ArrayList<ClassroomFilter>();
			List<FitnessValue> fitnessValues = new ArrayList<FitnessValue>();

			classroomFilters.add(new ClassTypeFilter());
			classroomFilters.add(new CapacityFilter());

			fitnessValues.add(new CollisionsFitnessValue(collisionsFnWeight));
			fitnessValues.add(new FreeLabsFitnessValue(freeLabsFnWeight));

			ClassroomFilterManager cfm = new ClassroomFilterManager(classroomFilters, classrooms);
			CollisionManager cm = new CollisionManager();
			GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(cfm, cm);

			Decoder decoder = new Decoder();
			for (Group g : groups) {
				decoder.putMasterAssignment(g.getCode(), new Assignment(g));
			}

			FitnessFunction fitnessFunction = new DefaultFitnessFunction(decoder, greedyAlgo, fitnessValues);

			GeneticAlgorithm genAlgo = new GeneticAlgorithm(individualLength, populationSize, mutationProbability,
					maxTimeMilliseconds, numberOfGenerations, fitnessFunction);

			Individual bestIndividual = genAlgo.geneticAlgorithm();
			System.out.println(bestIndividual.toString());

			// Business errors
			if (errh.anyErrors()) {
				errh.getCustomErrorMessages().forEach(e -> cli.showError(e));
				cli.showEndOfProgramWithErrors();
				return;
			}

			logh.log(Level.FINE, Program.class.getName(), "main", "END Business logic");

		} catch (Exception e) {

			logh.log(Level.SEVERE, Program.class.getName(), "main", e.getLocalizedMessage(), e);
			errh.addError(new ErrorType(e));

		} finally {

			if (errh.anyErrors()) {
				errh.getCustomErrorMessages().forEach(e -> cli.showError(e));
				cli.showEndOfProgramWithErrors();
			} else
				cli.showEndOfProgram();

		}
	}

}

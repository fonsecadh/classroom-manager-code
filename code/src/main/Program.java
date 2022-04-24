package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import business.alg.gen.logic.GeneticAlgorithm;
import business.alg.gen.logic.IndividualManager;
import business.alg.gen.logic.IndividualPrinter;
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
import persistence.filemanager.FileManager;
import persistence.problem.csv.AcademicWeeksDataAccessCsv;
import persistence.problem.csv.ClassroomsDataAccessCsv;
import persistence.problem.csv.GroupScheduleDataAccessCsv;
import persistence.problem.csv.GroupsDataAccessCsv;
import persistence.problem.csv.SubjectDataAccessCsv;
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

			// Input
			String subjectFilePath = "files/input/1_CSV_Asignatura.csv";
			String classroomsFilePath = "files/input/2_CSV_Aula.csv";
			String groupsFilePath = "files/input/3_CSV_Grupo.csv";
			String groupScheduleFilePath = "files/input/4_CSV_Horario.csv";
			String weeksFilePath = "files/input/5_CSV_SemanaLectiva.csv";

			Map<String, Subject> subjects = new SubjectDataAccessCsv().loadSubjects(subjectFilePath);
			Map<String, Classroom> classrooms = new ClassroomsDataAccessCsv().loadClassrooms(classroomsFilePath);
			Map<String, Group> groups = new GroupsDataAccessCsv().loadGroups(groupsFilePath, subjects);
			new GroupScheduleDataAccessCsv().loadGroupSchedule(groupScheduleFilePath, groups);
			new AcademicWeeksDataAccessCsv().loadAcademicWeeks(weeksFilePath, groups);

			List<Subject> subjectList = new ArrayList<Subject>(subjects.values());
			List<Classroom> classroomList = new ArrayList<Classroom>(classrooms.values());
			List<Group> groupList = new ArrayList<Group>(groups.values());

			// Output
			String outputFilePath = "files/output/output.txt";
			FileManager fm = new FileManager();

			// Genetic parameters
			int individualLength = groups.size();
			int populationSize = 50;
			double mutationProbability = 0.3;
			long maxTimeMilliseconds = 30000;
			int numberOfGenerations = 10;

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

			ClassroomFilterManager cfm = new ClassroomFilterManager(classroomFilters, classroomList);
			CollisionManager cm = new CollisionManager();
			GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(cfm, cm);

			Decoder decoder = new Decoder();
			for (Group g : groupList) {
				decoder.putMasterAssignment(g.getCode(), new Assignment(g));
			}

			FitnessFunction fitnessFunction = new DefaultFitnessFunction(decoder, greedyAlgo, fitnessValues);

			List<String> groupCodes = groupList.stream().map(g -> g.getCode()).collect(Collectors.toList());
			IndividualManager individualManager = new IndividualManager(groupCodes);

			GeneticAlgorithm genAlgo = new GeneticAlgorithm(individualLength, populationSize, mutationProbability,
					maxTimeMilliseconds, numberOfGenerations, fitnessFunction, individualManager);

			Individual bestIndividual = genAlgo.geneticAlgorithm();
			IndividualPrinter individualPrinter = new IndividualPrinter(subjectList, decoder);
			fm.writeToFile(outputFilePath, individualPrinter.getPrettyIndividual(bestIndividual));

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

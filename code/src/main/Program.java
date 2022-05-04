package main;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
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
import business.alg.gen.logic.fitness.values.LanguageFitnessValue;
import business.alg.gen.logic.fitness.values.PreferencesFitnessValue;
import business.alg.gen.logic.fitness.values.SharedLabsFitnessValue;
import business.alg.gen.logic.fitness.values.SharedTheoryFitnessValue;
import business.alg.gen.model.Individual;
import business.alg.gen.model.Preference;
import business.alg.greed.logic.Decoder;
import business.alg.greed.logic.GreedyAlgorithm;
import business.alg.greed.logic.collisions.CollisionManager;
import business.alg.greed.logic.filters.CapacityFilter;
import business.alg.greed.logic.filters.ClassTypeFilter;
import business.alg.greed.logic.filters.ClassroomFilter;
import business.alg.greed.logic.filters.ClassroomFilterManager;
import business.alg.greed.logic.filters.RestrictionFilter;
import business.alg.greed.model.Assignment;
import business.alg.greed.model.Restriction;
import business.config.Config;
import business.errorhandler.logic.ErrorHandler;
import business.errorhandler.model.ErrorType;
import business.loghandler.LogHandler;
import business.problem.Classroom;
import business.problem.Group;
import business.problem.Subject;
import persistence.filemanager.FileManager;
import persistence.problem.AssignmentsDataAccess;
import persistence.problem.csv.AcademicWeeksDataAccessCsv;
import persistence.problem.csv.AssignmentDataAccessCsv;
import persistence.problem.csv.ClassroomsDataAccessCsv;
import persistence.problem.csv.GroupScheduleDataAccessCsv;
import persistence.problem.csv.GroupsDataAccessCsv;
import persistence.problem.csv.PreferencesDataAccessCsv;
import persistence.problem.csv.RestrictionsDataAccessCsv;
import persistence.problem.csv.SubjectDataAccessCsv;
import ui.CommandLineInterface;

public class Program {

	public static void main(String[] args) {

		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();

		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();
		Config config = Config.getInstance();

		// Time
		long startTime = System.currentTimeMillis(), currentTime, totalTime;

		try {

			cli.showProgramDetails("1.0.0");

			// Persistence
			cli.showMessage("START Processing input files...");
			cli.showNewLine();
			logh.log(Level.FINE, Program.class.getName(), "main", "START Persistence logic");

			// FileManager
			FileManager fm = new FileManager();

			// Config file
			String configFilePath = "files/config/classroom-manager.properties";

			cli.showMessageWithoutNewLine("Loading CONFIG file...");
			config.load(configFilePath);
			cli.showMessage(" DONE");

			// Input

			// Required
			String subjectsFilePath = config.getProperty("SUBJECTS_FILE_PATH");
			String classroomsFilePath = config.getProperty("CLASSROOMS_FILE_PATH");
			String groupsFilePath = config.getProperty("GROUPS_FILE_PATH");
			String groupScheduleFilePath = config.getProperty("GROUPSCHEDULE_FILE_PATH");
			String weeksFilePath = config.getProperty("WEEKS_FILE_PATH");

			// Optional
			boolean loadAssignments = Boolean.parseBoolean(config.getProperty("LOAD_ASSIGNMENTS"));
			String assignmentsFilePath = config.getProperty("ASSIGNMENTS_FILE_PATH");

			boolean loadPreferences = Boolean.parseBoolean(config.getProperty("LOAD_PREFERENCES"));
			String preferencesFilePath = config.getProperty("PREFERENCES_FILE_PATH");

			boolean loadRestrictions = Boolean.parseBoolean(config.getProperty("LOAD_RESTRICTIONS"));
			String restrictionsFilePath = config.getProperty("RESTRICTIONS_FILE_PATH");

			// Load files

			cli.showMessageWithoutNewLine("Loading SUBJECTS file...");
			Map<String, Subject> subjects = new SubjectDataAccessCsv().loadSubjects(subjectsFilePath, fm);
			cli.showMessage(" DONE");

			cli.showMessageWithoutNewLine("Loading CLASSROOMS file...");
			Map<String, Classroom> classrooms = new ClassroomsDataAccessCsv().loadClassrooms(classroomsFilePath, fm);
			cli.showMessage(" DONE");

			cli.showMessageWithoutNewLine("Loading GROUPS file...");
			Map<String, Group> groups = new GroupsDataAccessCsv().loadGroups(groupsFilePath, subjects, fm);
			cli.showMessage(" DONE");

			cli.showMessageWithoutNewLine("Loading GROUPSCHEDULE file...");
			new GroupScheduleDataAccessCsv().loadGroupSchedule(groupScheduleFilePath, groups, fm);
			cli.showMessage(" DONE");

			cli.showMessageWithoutNewLine("Loading WEEKS file...");
			new AcademicWeeksDataAccessCsv().loadAcademicWeeks(weeksFilePath, groups, fm);
			cli.showMessage(" DONE");

			AssignmentsDataAccess ada = new AssignmentDataAccessCsv();
			Map<String, Assignment> assignments = new HashMap<String, Assignment>();
			if (loadAssignments) {
				cli.showMessageWithoutNewLine("Loading ASSIGNMENTS file...");
				assignments = ada.loadAssignments(assignmentsFilePath, groups, classrooms, fm);
				cli.showMessage(" DONE");
			}

			Map<String, Preference> preferences = null;
			if (loadPreferences) {
				cli.showMessageWithoutNewLine("Loading PREFERENCES file...");
				preferences = new PreferencesDataAccessCsv().loadPreferences(preferencesFilePath, classrooms, subjects,
						fm);
				cli.showMessage(" DONE");
			}

			Map<String, List<Restriction>> restrictions = null;
			if (loadRestrictions) {
				cli.showMessageWithoutNewLine("Loading RESTRICTIONS file...");
				restrictions = new RestrictionsDataAccessCsv().loadRestrictions(restrictionsFilePath, classrooms,
						groups, fm);
				cli.showMessage(" DONE");
			}

			List<Subject> subjectList = new ArrayList<Subject>(subjects.values());
			List<Classroom> classroomList = new ArrayList<Classroom>(classrooms.values());
			List<Group> groupList = new ArrayList<Group>(groups.values());

			// Output
			String outputFilePath = "files/output/output.txt";
			String outputAssignmentsFilePath = "files/output/X_CSV_Asignaciones.csv";

			// Genetic parameters
			int individualLength = groups.size();
			int populationSize = Integer.parseInt(config.getProperty("POP_SIZE"));
			double crossoverProbability = Double.parseDouble(config.getProperty("CROSS_PROB"));
			double mutationProbability = Double.parseDouble(config.getProperty("MUTA_PROB"));
			long maxTimeMilliseconds = Long.parseLong(config.getProperty("MAX_TIME_MS"));
			int numberOfGenerations = Integer.parseInt(config.getProperty("NUM_GEN"));
			int freeLabs = Integer.parseInt(config.getProperty("FREE_LABS"));

			// Fitness weights
			double collisionsFnWeight = Double.parseDouble(config.getProperty("COL_WEIGHT"));
			double freeLabsFnWeight = Double.parseDouble(config.getProperty("FREE_LABS_WEIGHT"));
			double languageFnWeight = Double.parseDouble(config.getProperty("LANG_WEIGHT"));
			double sharedLabsFnWeight = Double.parseDouble(config.getProperty("SHARED_LABS_WEIGHT"));
			double sharedTheoryFnWeight = Double.parseDouble(config.getProperty("SHARED_THEORY_WEIGHT"));
			double prefsFnWeight = Double.parseDouble(config.getProperty("PREFS_WEIGHT"));

			// Persistence errors
			if (errh.anyErrors()) {
				errh.getCustomErrorMessages().forEach(e -> cli.showError(e));
				cli.showEndOfProgramWithErrors();
				return;
			}

			cli.showNewLine();
			cli.showMessage("END Processing input files");
			cli.showNewLine();
			logh.log(Level.FINE, Program.class.getName(), "main", "END Persistence logic");

			// Business logic
			logh.log(Level.FINE, Program.class.getName(), "main", "START Business logic");

			List<ClassroomFilter> classroomFilters = new ArrayList<ClassroomFilter>();
			List<FitnessValue> fitnessValues = new ArrayList<FitnessValue>();

			// Classroom filters
			classroomFilters.add(new ClassTypeFilter());
			classroomFilters.add(new CapacityFilter());

			if (loadRestrictions)
				classroomFilters.add(new RestrictionFilter(restrictions));

			// Fitness values
			fitnessValues.add(new CollisionsFitnessValue(collisionsFnWeight));
			fitnessValues.add(new FreeLabsFitnessValue(freeLabsFnWeight, freeLabs));
			fitnessValues.add(new LanguageFitnessValue(languageFnWeight, subjectList));
			fitnessValues.add(new SharedLabsFitnessValue(sharedLabsFnWeight, subjectList, classroomList));
			fitnessValues.add(new SharedTheoryFitnessValue(sharedTheoryFnWeight, subjectList, classroomList));

			if (loadPreferences)
				fitnessValues.add(new PreferencesFitnessValue(prefsFnWeight, preferences, subjectList));

			ClassroomFilterManager cfm = new ClassroomFilterManager(classroomFilters, classroomList);
			CollisionManager cm = new CollisionManager();
			GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(cfm, cm);

			Decoder decoder = new Decoder();
			for (Group g : groupList) {
				if (assignments.get(g.getCode()) != null)
					decoder.putMasterAssignment(g.getCode(), new Assignment(assignments.get(g.getCode())));
				else
					decoder.putMasterAssignment(g.getCode(), new Assignment(g));
			}

			FitnessFunction fitnessFunction = new DefaultFitnessFunction(decoder, greedyAlgo, fitnessValues);

			List<String> groupCodes = groupList.stream().map(g -> g.getCode()).collect(Collectors.toList());
			IndividualManager individualManager = new IndividualManager(groupCodes);

			GeneticAlgorithm genAlgo = new GeneticAlgorithm(individualLength, populationSize, mutationProbability,
					crossoverProbability, maxTimeMilliseconds, numberOfGenerations, fitnessFunction, individualManager);

			Individual bestIndividual = genAlgo.geneticAlgorithm();

			// Output files
			List<Assignment> decoded = decoder.decode(bestIndividual);
			Map<String, Assignment> assignmentsMap = greedyAlgo.greedyAlgorithm(decoded);

			IndividualPrinter individualPrinter = new IndividualPrinter(subjectList, assignmentsMap);
			fm.writeToFile(outputFilePath, individualPrinter.getPrettyIndividual(bestIndividual));
			ada.writeAssignments(outputAssignmentsFilePath, assignmentsMap, subjectList, fm);

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

			currentTime = System.currentTimeMillis();
			totalTime = currentTime - startTime;

			Duration d = Duration.ofMillis(totalTime);
			long minutes = d.toMinutes();
			long seconds = d.minusMinutes(minutes).getSeconds();

			cli.showNewLine();
			if (minutes > 0)
				cli.showMessage(String.format("Finished execution in %02d minutes and %02d seconds", minutes, seconds));
			else
				cli.showMessage(String.format("Finished execution in %02d seconds", seconds));

			if (errh.anyErrors()) {
				errh.getCustomErrorMessages().forEach(e -> cli.showError(e));
				cli.showEndOfProgramWithErrors();
			} else
				cli.showEndOfProgram();

		}
	}

}

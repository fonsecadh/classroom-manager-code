package main;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import business.alg.gen.model.Metrics;
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
import business.classfinder.logic.Classfinder;
import business.classfinder.model.ClassfinderQuery;
import business.config.Config;
import business.errorhandler.exceptions.ArgumentException;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.errorhandler.logic.ErrorHandler;
import business.errorhandler.model.ErrorType;
import business.loghandler.LogHandler;
import business.problem.model.Classroom;
import business.problem.model.Group;
import business.problem.model.Subject;
import persistence.automation.InputFilesAutomation;
import persistence.automation.csv.EnrolledStudentsDataAccessCsv;
import persistence.automation.csv.PlanningDataAccessCsv;
import persistence.filemanager.FileManager;
import persistence.problem.AssignmentsDataAccess;
import persistence.problem.csv.AcademicWeeksDataAccessCsv;
import persistence.problem.csv.AssignmentDataAccessCsv;
import persistence.problem.csv.ClassfinderDataAccessCsv;
import persistence.problem.csv.ClassroomsDataAccessCsv;
import persistence.problem.csv.GroupScheduleDataAccessCsv;
import persistence.problem.csv.GroupsDataAccessCsv;
import persistence.problem.csv.PreferencesDataAccessCsv;
import persistence.problem.csv.RestrictionsDataAccessCsv;
import persistence.problem.csv.SubjectDataAccessCsv;
import ui.ArgOption;
import ui.CommandLineInterface;

public class Program {
	public static void main(String[] args)
	{
		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();

		// Handlers
		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();

		// Time
		long startTime = System.currentTimeMillis(), currentTime,
				totalTime;
		try {
			cli.showProgramDetails();
			ArgOption o = parseArgs(args);
			switch (o.getOption()) {
			case 0:
				executeAlgorithm(o.getConfigurationFiles());
				break;
			case 1:
				executeClassFinder(o.getConfigurationFiles());
				break;
			case 2:
				executeInputFileAutomation(
						o.getConfigurationFiles());
				break;
			case 3:
				executeExperiments(o.getConfigurationFiles());
				break;
			case 4:
				cli.showHelp();
				break;
			case 5:
				cli.showVersion();
				break;
			}
		} catch (ArgumentException e) {
			cli.showHelp();
			logh.log(Level.SEVERE, Program.class.getName(), "main",
					e.getLocalizedMessage(), e);
			errh.addError(new ErrorType(e));
		} catch (Exception e) {
			logh.log(Level.SEVERE, Program.class.getName(), "main",
					e.getLocalizedMessage(), e);
			errh.addError(new ErrorType(e));
		} finally {
			currentTime = System.currentTimeMillis();
			totalTime = currentTime - startTime;

			Duration d = Duration.ofMillis(totalTime);
			long minutes = d.toMinutes();
			long seconds = d.minusMinutes(minutes).getSeconds();

			cli.showNewLine();
			if (minutes > 0)
				cli.showMessage(String.format(
						"Finished execution in %02d minutes and %02d seconds",
						minutes, seconds));
			else
				cli.showMessage(String.format(
						"Finished execution in %02d seconds",
						seconds));
			if (errh.anyErrors()) {
				errh.getCustomErrorMessages()
						.forEach(e -> cli.showError(e));
				cli.showEndOfProgramWithErrors();
			} else
				cli.showEndOfProgram();
		}
	}

	private static ArgOption parseArgs(String[] args)
			throws ArgumentException
	{
		Map<String, List<String>> flagMap = new HashMap<String, List<String>>();
		List<String> configFilePaths = null;
		for (int i = 0; i < args.length; i++) {
			String a = args[i];
			if (a.charAt(0) == '-') {
				if (a.length() < 2) {
					String msg = String.format(
							"Wrong argument (%s) received",
							a);
					throw new ArgumentException(msg);
				}
				configFilePaths = new ArrayList<String>();
				flagMap.put(a.substring(1), configFilePaths);
			} else if (configFilePaths != null) {
				configFilePaths.add(a);
			} else {
				String msg = String.format(
						"Wrong argument (%s) received",
						a);
				throw new ArgumentException(msg);
			}
		}
		Set<String> flags = flagMap.keySet();
		if (flags.size() > 1 || flags.size() == 0) {
			String msg = "Utility must receive one (and only one) option flag as an argument";
			throw new ArgumentException(msg);
		}
		String flag = flags.iterator().next();
		switch (flag) {
		case "h":
		case "-help":
			return new ArgOption(4, flagMap.get(flag));
		case "v":
		case "-version":
			return new ArgOption(5, flagMap.get(flag));
		case "a":
		case "-algorithm":
			return new ArgOption(0, flagMap.get(flag));
		case "q":
		case "-query":
			return new ArgOption(1, flagMap.get(flag));
		case "t":
		case "-transform":
			return new ArgOption(2, flagMap.get(flag));
		case "e":
		case "-experiment":
			return new ArgOption(3, flagMap.get(flag));
		default:
			String msg = String.format("Wrong flag (%s) received",
					flag);
			throw new ArgumentException(msg);
		}
	}

	/*
	 * 
	 * 
	 * ALGORITHM
	 * 
	 * 
	 */

	public static void executeAlgorithm(List<String> confPaths)
			throws PersistenceException, InputValidationException
	{
		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();

		// Handlers
		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();

		// Configuration manager
		Config config = Config.getInstance();

		// Persistence
		cli.showMessage("START Processing input files...");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Persistence logic");

		// FileManager
		FileManager fm = new FileManager();

		// Config file
		cli.showMessageWithoutNewLine("Loading CONFIG files...");
		for (String conf : confPaths) {
			config.load(conf);
		}
		cli.showMessage(" DONE");

		// Input

		// Required
		String subjectsFilePath = config
				.getProperty("SUBJECTS_FILE_PATH");
		String classroomsFilePath = config
				.getProperty("CLASSROOMS_FILE_PATH");
		String groupsFilePath = config.getProperty("GROUPS_FILE_PATH");
		String groupScheduleFilePath = config
				.getProperty("GROUPSCHEDULE_FILE_PATH");
		String weeksFilePath = config.getProperty("WEEKS_FILE_PATH");

		// Optional
		boolean loadAssignments = Boolean.parseBoolean(
				config.getProperty("LOAD_ASSIGNMENTS"));
		String assignmentsFilePath = config
				.getProperty("ASSIGNMENTS_FILE_PATH");

		boolean loadPreferences = Boolean.parseBoolean(
				config.getProperty("LOAD_PREFERENCES"));
		String preferencesFilePath = config
				.getProperty("PREFERENCES_FILE_PATH");

		boolean loadRestrictions = Boolean.parseBoolean(
				config.getProperty("LOAD_RESTRICTIONS"));
		String restrictionsFilePath = config
				.getProperty("RESTRICTIONS_FILE_PATH");

		// Load files
		Map<String, Subject> subjects;
		Map<String, Classroom> classrooms;
		Map<String, Group> groups;
		Map<String, Assignment> assignments;
		Map<String, List<Preference>> preferences;
		Map<String, List<Restriction>> restrictions;

		cli.showMessageWithoutNewLine("Loading SUBJECTS file...");
		subjects = new SubjectDataAccessCsv()
				.loadSubjects(subjectsFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading CLASSROOMS file...");
		classrooms = new ClassroomsDataAccessCsv()
				.loadClassrooms(classroomsFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading GROUPS file...");
		groups = new GroupsDataAccessCsv().loadGroups(groupsFilePath,
				subjects, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading GROUPSCHEDULE file...");
		new GroupScheduleDataAccessCsv().loadGroupSchedule(
				groupScheduleFilePath, groups, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading WEEKS file...");
		new AcademicWeeksDataAccessCsv()
				.loadAcademicWeeks(weeksFilePath, groups, fm);
		cli.showMessage(" DONE");

		AssignmentsDataAccess ada = new AssignmentDataAccessCsv();
		assignments = new HashMap<String, Assignment>();
		if (loadAssignments) {
			cli.showMessageWithoutNewLine(
					"Loading ASSIGNMENTS file...");
			assignments = ada.loadAssignments(assignmentsFilePath,
					groups, classrooms, fm);
			cli.showMessage(" DONE");
		}
		preferences = new HashMap<String, List<Preference>>();
		if (loadPreferences) {
			cli.showMessageWithoutNewLine(
					"Loading PREFERENCES file...");
			preferences = new PreferencesDataAccessCsv()
					.loadPreferences(preferencesFilePath,
							classrooms, groups,
							subjects, fm);
			cli.showMessage(" DONE");
		}
		restrictions = new HashMap<String, List<Restriction>>();
		if (loadRestrictions) {
			cli.showMessageWithoutNewLine(
					"Loading RESTRICTIONS file...");
			restrictions = new RestrictionsDataAccessCsv()
					.loadRestrictions(restrictionsFilePath,
							classrooms, groups,
							subjects, fm);
			cli.showMessage(" DONE");
		}
		List<Subject> subjectList = new ArrayList<Subject>(
				subjects.values());
		List<Classroom> classroomList = new ArrayList<Classroom>(
				classrooms.values());
		List<Group> groupList = new ArrayList<Group>(groups.values());

		// Output
		String outputFolderPath = config
				.getProperty("OUTPUT_FOLDER_PATH");
		String outputAssignmentsFilename = config
				.getProperty("OUTPUT_ASSIGNMENTS_FILENAME");

		// Greedy parameters
		boolean performRepairs = Boolean.parseBoolean(
				config.getProperty("PERFORM_REPAIRS"));
		boolean sameClassroomBias = Boolean.parseBoolean(
				config.getProperty("SAME_CLASSROOM_BIAS"));
		boolean preferenceBias = Boolean.parseBoolean(
				config.getProperty("PREFERENCE_BIAS"));

		// Genetic parameters
		int individualLength = groups.size();
		int populationSize = Integer
				.parseInt(config.getProperty("POP_SIZE"));
		double crossoverProbability = Double
				.parseDouble(config.getProperty("CROSS_PROB"));
		double mutationProbability = Double
				.parseDouble(config.getProperty("MUTA_PROB"));
		long maxTimeMilliseconds = Long
				.parseLong(config.getProperty("MAX_TIME_MS"));
		int numberOfGenerations = Integer
				.parseInt(config.getProperty("NUM_GEN"));
		int freeLabs = Integer
				.parseInt(config.getProperty("FREE_LABS"));
		int showGenInfo = Integer
				.parseInt(config.getProperty("SHOW_GEN_INFO"));

		// Fitness weights
		double collisionsFnWeight = Double
				.parseDouble(config.getProperty("COL_WEIGHT"));
		double freeLabsFnWeight = Double.parseDouble(
				config.getProperty("FREE_LABS_WEIGHT"));
		double languageFnWeight = Double
				.parseDouble(config.getProperty("LANG_WEIGHT"));
		double sharedLabsFnWeight = Double.parseDouble(
				config.getProperty("SHARED_LABS_WEIGHT"));
		double sharedTheoryFnWeight = Double.parseDouble(
				config.getProperty("SHARED_THEORY_WEIGHT"));
		double prefsFnWeight = Double.parseDouble(
				config.getProperty("PREFS_WEIGHT"));

		// Persistence errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		cli.showNewLine();
		cli.showMessage("END Processing input files");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Persistence logic");

		// Business logic
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Business logic");

		List<ClassroomFilter> classroomFilters = new ArrayList<ClassroomFilter>();
		List<FitnessValue> fitnessValues = new ArrayList<FitnessValue>();

		// Classroom filters
		classroomFilters.add(new ClassTypeFilter());
		classroomFilters.add(new CapacityFilter());

		if (loadRestrictions)
			classroomFilters.add(
					new RestrictionFilter(restrictions));

		// Fitness values
		fitnessValues.add(
				new CollisionsFitnessValue(collisionsFnWeight));
		fitnessValues.add(new FreeLabsFitnessValue(freeLabsFnWeight,
				freeLabs));
		fitnessValues.add(new LanguageFitnessValue(languageFnWeight,
				subjectList));
		fitnessValues.add(new SharedLabsFitnessValue(sharedLabsFnWeight,
				subjectList, classroomList));
		fitnessValues.add(new SharedTheoryFitnessValue(
				sharedTheoryFnWeight, subjectList,
				classroomList));

		if (loadPreferences)
			fitnessValues.add(new PreferencesFitnessValue(
					prefsFnWeight, preferences));

		Decoder decoder = new Decoder();
		for (Group g : groupList) {
			if (assignments.get(g.getCode()) != null)
				decoder.putMasterAssignment(g.getCode(),
						new Assignment(assignments.get(
								g.getCode())));
			else
				decoder.putMasterAssignment(g.getCode(),
						new Assignment(g));
		}
		ClassroomFilterManager cfm = new ClassroomFilterManager(
				classroomFilters, classroomList, preferences,
				preferenceBias);
		CollisionManager cm = new CollisionManager();

		Map<String, Subject> groupSubjectMap = new HashMap<String, Subject>();
		for (Subject s : subjectList) {
			for (Group g : s.getGroups()) {
				groupSubjectMap.put(g.getCode(), s);
			}
		}
		GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(cfm, cm,
				groupSubjectMap, subjectList, performRepairs,
				sameClassroomBias);

		FitnessFunction fitnessFunction = new DefaultFitnessFunction(
				decoder, greedyAlgo, fitnessValues);

		List<String> groupCodes = groupList.stream()
				.map(g -> g.getCode())
				.collect(Collectors.toList());
		IndividualManager individualManager = new IndividualManager(
				groupCodes);

		GeneticAlgorithm genAlgo = new GeneticAlgorithm(
				individualLength, populationSize,
				mutationProbability, crossoverProbability,
				maxTimeMilliseconds, numberOfGenerations,
				fitnessFunction, individualManager,
				showGenInfo);

		Individual bestIndividual = genAlgo.geneticAlgorithm();

		// Output files
		List<Assignment> decoded = decoder.decode(bestIndividual);
		Map<String, Assignment> assignmentsMap = greedyAlgo
				.greedyAlgorithm(decoded);

		String aPath = outputFolderPath + outputAssignmentsFilename;
		IndividualPrinter individualPrinter = new IndividualPrinter(
				subjectList, assignmentsMap);

		// Assignments (Pretty format)
		fm.writeToFile(aPath + ".txt",
				individualPrinter.getPrettyIndividual());

		// Assignments (CSV format)
		ada.writeAssignments(aPath + ".csv", assignmentsMap,
				subjectList, fm);

		// Classroom timetables
		for (Classroom c : classroomList) {
			fm.writeToFile(outputFolderPath + c.getCode() + ".txt",
					individualPrinter.getTimetableFor(c));
		}
		// Business errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Business logic");
	}

	/*
	 * 
	 * 
	 * CLASSFINDER
	 * 
	 * 
	 */

	public static void executeClassFinder(List<String> confPaths)
			throws PersistenceException, InputValidationException
	{
		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();

		// Handlers
		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();

		// Configuration manager
		Config config = Config.getInstance();

		// Persistence
		cli.showMessage("START Processing input files...");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Persistence logic");

		// FileManager
		FileManager fm = new FileManager();

		// Config file
		cli.showMessageWithoutNewLine("Loading CONFIG files...");
		for (String conf : confPaths) {
			config.load(conf);
		}
		cli.showMessage(" DONE");

		// Input

		// Required
		String subjectsFilePath = config
				.getProperty("SUBJECTS_FILE_PATH");
		String classroomsFilePath = config
				.getProperty("CLASSROOMS_FILE_PATH");
		String groupsFilePath = config.getProperty("GROUPS_FILE_PATH");
		String groupScheduleFilePath = config
				.getProperty("GROUPSCHEDULE_FILE_PATH");
		String weeksFilePath = config.getProperty("WEEKS_FILE_PATH");
		String assignmentsFilePath = config
				.getProperty("ASSIGNMENTS_FILE_PATH");
		String queriesPath = config.getProperty("QUERIES_FILE_PATH");

		// Load files
		Map<String, Subject> subjects;
		Map<String, Classroom> classrooms;
		Map<String, Group> groups;
		Map<String, Assignment> assignments;
		List<ClassfinderQuery> queries;

		cli.showMessageWithoutNewLine("Loading SUBJECTS file...");
		subjects = new SubjectDataAccessCsv()
				.loadSubjects(subjectsFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading CLASSROOMS file...");
		classrooms = new ClassroomsDataAccessCsv()
				.loadClassrooms(classroomsFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading GROUPS file...");
		groups = new GroupsDataAccessCsv().loadGroups(groupsFilePath,
				subjects, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading GROUPSCHEDULE file...");
		new GroupScheduleDataAccessCsv().loadGroupSchedule(
				groupScheduleFilePath, groups, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading WEEKS file...");
		new AcademicWeeksDataAccessCsv()
				.loadAcademicWeeks(weeksFilePath, groups, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading ASSIGNMENTS file...");
		assignments = new AssignmentDataAccessCsv().loadAssignments(
				assignmentsFilePath, groups, classrooms, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading QUERIES file...");
		queries = new ClassfinderDataAccessCsv()
				.loadQueries(queriesPath, fm);
		cli.showMessage(" DONE");

		List<Classroom> classroomList = new ArrayList<Classroom>(
				classrooms.values());

		// Output
		String outputFolderPath = config
				.getProperty("OUTPUT_FOLDER_PATH");
		String outputQueriesFilename = config
				.getProperty("OUTPUT_QUERIES_FILENAME");

		// Persistence errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		cli.showNewLine();
		cli.showMessage("END Processing input files");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Persistence logic");

		// Business logic
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Business logic");

		cli.showMessageWithoutNewLine(
				"Searching for free classrooms...");

		Map<String, List<Assignment>> classroomAssignmentMap = new HashMap<String, List<Assignment>>();
		for (Classroom c : classroomList) {
			List<Assignment> aList = new ArrayList<Assignment>();
			for (Assignment a : assignments.values()) {
				if (a.getClassroom() != null && a.getClassroom()
						.getCode().equalsIgnoreCase(
								c.getCode())) {
					aList.add(a);
				}
			}
			classroomAssignmentMap.put(c.getCode(),
					new ArrayList<Assignment>(aList));
		}
		Classfinder classfinder = new Classfinder(queries,
				classroomList, classroomAssignmentMap);

		String res = classfinder.getAllQueryResults();

		cli.showMessage(" DONE");

		// Output files
		cli.showMessageWithoutNewLine("Storing the results...");

		String qPath = outputFolderPath + outputQueriesFilename;
		fm.writeToFile(qPath + ".txt", res);

		// Business errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		cli.showMessage(" DONE");

		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Business logic");
	}

	/*
	 * 
	 * 
	 * INPUT FILE AUTOMATION
	 * 
	 * 
	 */

	public static void executeInputFileAutomation(List<String> confPaths)
			throws PersistenceException, InputValidationException
	{
		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();

		// Handlers
		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();

		// Configuration manager
		Config config = Config.getInstance();

		// Persistence
		cli.showMessage("START Processing input files...");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Persistence logic");

		// FileManager
		FileManager fm = new FileManager();

		// Config file
		cli.showMessageWithoutNewLine("Loading CONFIG files...");
		for (String conf : confPaths) {
			config.load(conf);
		}
		cli.showMessage(" DONE");

		// Input

		// Required
		String planningFilePath = config
				.getProperty("PLANNING_FILE_PATH");
		String enrolledFilePath = config
				.getProperty("ENROLLED_FILE_PATH");

		// Load files
		Map<String, Group> groups;

		cli.showMessageWithoutNewLine("Loading PLANNING file...");
		groups = new PlanningDataAccessCsv()
				.loadGroupsFromPlanning(planningFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading ENROLLED file...");
		new EnrolledStudentsDataAccessCsv().loadEnrolledStudents(
				enrolledFilePath, groups, fm);
		cli.showMessage(" DONE");

		// Output
		String outputFolderPath = config
				.getProperty("OUTPUT_FOLDER_PATH");
		String outputGroupsFilename = config
				.getProperty("OUTPUT_GROUPS_FILENAME");
		String outputGroupScheduleFilename = config
				.getProperty("OUTPUT_GROUPSCHEDULE_FILENAME");
		String outputWeeksFilename = config
				.getProperty("OUTPUT_WEEKS_FILENAME");

		// Persistence errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		cli.showNewLine();
		cli.showMessage("END Processing input files");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Persistence logic");

		// Business logic
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Business logic");

		cli.showMessage("Generating input files...");
		cli.showNewLine();

		List<Group> groupList = new ArrayList<Group>(groups.values());

		InputFilesAutomation auto = new InputFilesAutomation(groupList);

		// Paths
		String gPath = outputFolderPath + outputGroupsFilename + ".csv";
		String gsPath = outputFolderPath + outputGroupScheduleFilename
				+ ".csv";
		String wPath = outputFolderPath + outputWeeksFilename + ".csv";

		cli.showMessageWithoutNewLine("Generating GROUPS file...");
		fm.writeToFile(gPath, auto.getGroupData());
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine(
				"Generating GROUPSCHEDULE file...");
		fm.writeToFile(gsPath, auto.getGroupScheduleData());
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Generating WEEKS file...");
		fm.writeToFile(wPath, auto.getAcademicWeeksData());
		cli.showMessage(" DONE");

		cli.showNewLine();
		cli.showMessage("All files generated.");

		// Business errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Business logic");
	}

	/*
	 * 
	 * 
	 * EXPERIMENTS : TODO - DELETE IN FINAL VERSION!
	 * 
	 * 
	 */

	public static void executeExperiments(List<String> confPaths)
			throws PersistenceException, InputValidationException
	{
		// CLI
		CommandLineInterface cli = CommandLineInterface.getInstance();

		// Handlers
		LogHandler logh = LogHandler.getInstance();
		ErrorHandler errh = ErrorHandler.getInstance();

		// Configuration manager
		Config config = Config.getInstance();

		// Persistence
		cli.showMessage("START Processing input files...");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Persistence logic");

		// FileManager
		FileManager fm = new FileManager();

		// Config file
		cli.showMessageWithoutNewLine("Loading CONFIG files...");
		for (int i = 0; i < confPaths.size() - 1; i++) {
			String conf = confPaths.get(i);
			config.load(conf);
		}
		String outputResultsFilename = confPaths.get(confPaths.size() - 1);
		cli.showMessage(" DONE");

		// Input

		// Required
		String subjectsFilePath = config
				.getProperty("SUBJECTS_FILE_PATH");
		String classroomsFilePath = config
				.getProperty("CLASSROOMS_FILE_PATH");
		String groupsFilePath = config.getProperty("GROUPS_FILE_PATH");
		String groupScheduleFilePath = config
				.getProperty("GROUPSCHEDULE_FILE_PATH");
		String weeksFilePath = config.getProperty("WEEKS_FILE_PATH");

		// Optional
		boolean loadAssignments = Boolean.parseBoolean(
				config.getProperty("LOAD_ASSIGNMENTS"));
		String assignmentsFilePath = config
				.getProperty("ASSIGNMENTS_FILE_PATH");

		boolean loadPreferences = Boolean.parseBoolean(
				config.getProperty("LOAD_PREFERENCES"));
		String preferencesFilePath = config
				.getProperty("PREFERENCES_FILE_PATH");

		boolean loadRestrictions = Boolean.parseBoolean(
				config.getProperty("LOAD_RESTRICTIONS"));
		String restrictionsFilePath = config
				.getProperty("RESTRICTIONS_FILE_PATH");

		// Load files
		Map<String, Subject> subjects;
		Map<String, Classroom> classrooms;
		Map<String, Group> groups;
		Map<String, Assignment> assignments;
		Map<String, List<Preference>> preferences;
		Map<String, List<Restriction>> restrictions;

		cli.showMessageWithoutNewLine("Loading SUBJECTS file...");
		subjects = new SubjectDataAccessCsv()
				.loadSubjects(subjectsFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading CLASSROOMS file...");
		classrooms = new ClassroomsDataAccessCsv()
				.loadClassrooms(classroomsFilePath, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading GROUPS file...");
		groups = new GroupsDataAccessCsv().loadGroups(groupsFilePath,
				subjects, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading GROUPSCHEDULE file...");
		new GroupScheduleDataAccessCsv().loadGroupSchedule(
				groupScheduleFilePath, groups, fm);
		cli.showMessage(" DONE");

		cli.showMessageWithoutNewLine("Loading WEEKS file...");
		new AcademicWeeksDataAccessCsv()
				.loadAcademicWeeks(weeksFilePath, groups, fm);
		cli.showMessage(" DONE");

		AssignmentsDataAccess ada = new AssignmentDataAccessCsv();
		assignments = new HashMap<String, Assignment>();
		if (loadAssignments) {
			cli.showMessageWithoutNewLine(
					"Loading ASSIGNMENTS file...");
			assignments = ada.loadAssignments(assignmentsFilePath,
					groups, classrooms, fm);
			cli.showMessage(" DONE");
		}
		preferences = new HashMap<String, List<Preference>>();
		if (loadPreferences) {
			cli.showMessageWithoutNewLine(
					"Loading PREFERENCES file...");
			preferences = new PreferencesDataAccessCsv()
					.loadPreferences(preferencesFilePath,
							classrooms, groups,
							subjects, fm);
			cli.showMessage(" DONE");
		}
		restrictions = new HashMap<String, List<Restriction>>();
		if (loadRestrictions) {
			cli.showMessageWithoutNewLine(
					"Loading RESTRICTIONS file...");
			restrictions = new RestrictionsDataAccessCsv()
					.loadRestrictions(restrictionsFilePath,
							classrooms, groups,
							subjects, fm);
			cli.showMessage(" DONE");
		}
		List<Subject> subjectList = new ArrayList<Subject>(
				subjects.values());
		List<Classroom> classroomList = new ArrayList<Classroom>(
				classrooms.values());
		List<Group> groupList = new ArrayList<Group>(groups.values());

		// Output
		String outputFolderPath = config
				.getProperty("OUTPUT_FOLDER_PATH");
		//String outputResultsFilename = config
		//		.getProperty("OUTPUT_RESULTS_FILENAME");

		// Greedy parameters
		boolean performRepairs = Boolean.parseBoolean(
				config.getProperty("PERFORM_REPAIRS"));
		boolean sameClassroomBias = Boolean.parseBoolean(
				config.getProperty("SAME_CLASSROOM_BIAS"));
		boolean preferenceBias = Boolean.parseBoolean(
				config.getProperty("PREFERENCE_BIAS"));

		// Genetic parameters
		int individualLength = groups.size();
		int populationSize = Integer
				.parseInt(config.getProperty("POP_SIZE"));
		double crossoverProbability = Double
				.parseDouble(config.getProperty("CROSS_PROB"));
		double mutationProbability = Double
				.parseDouble(config.getProperty("MUTA_PROB"));
		long maxTimeMilliseconds = Long
				.parseLong(config.getProperty("MAX_TIME_MS"));
		int numberOfGenerations = Integer
				.parseInt(config.getProperty("NUM_GEN"));
		int freeLabs = Integer
				.parseInt(config.getProperty("FREE_LABS"));
		int showGenInfo = Integer
				.parseInt(config.getProperty("SHOW_GEN_INFO"));

		// Fitness weights
		double collisionsFnWeight = Double
				.parseDouble(config.getProperty("COL_WEIGHT"));
		double freeLabsFnWeight = Double.parseDouble(
				config.getProperty("FREE_LABS_WEIGHT"));
		double languageFnWeight = Double
				.parseDouble(config.getProperty("LANG_WEIGHT"));
		double sharedLabsFnWeight = Double.parseDouble(
				config.getProperty("SHARED_LABS_WEIGHT"));
		double sharedTheoryFnWeight = Double.parseDouble(
				config.getProperty("SHARED_THEORY_WEIGHT"));
		double prefsFnWeight = Double.parseDouble(
				config.getProperty("PREFS_WEIGHT"));

		// Persistence errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		cli.showNewLine();
		cli.showMessage("END Processing input files");
		cli.showNewLine();
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Persistence logic");

		// Business logic
		logh.log(Level.FINE, Program.class.getName(), "main",
				"START Business logic");

		List<ClassroomFilter> classroomFilters = new ArrayList<ClassroomFilter>();
		List<FitnessValue> fitnessValues = new ArrayList<FitnessValue>();

		// Classroom filters
		classroomFilters.add(new ClassTypeFilter());
		classroomFilters.add(new CapacityFilter());

		if (loadRestrictions)
			classroomFilters.add(
					new RestrictionFilter(restrictions));

		// Fitness values
		fitnessValues.add(
				new CollisionsFitnessValue(collisionsFnWeight));
		fitnessValues.add(new FreeLabsFitnessValue(freeLabsFnWeight,
				freeLabs));
		fitnessValues.add(new LanguageFitnessValue(languageFnWeight,
				subjectList));
		fitnessValues.add(new SharedLabsFitnessValue(sharedLabsFnWeight,
				subjectList, classroomList));
		fitnessValues.add(new SharedTheoryFitnessValue(
				sharedTheoryFnWeight, subjectList,
				classroomList));

		if (loadPreferences)
			fitnessValues.add(new PreferencesFitnessValue(
					prefsFnWeight, preferences));

		Decoder decoder = new Decoder();
		for (Group g : groupList) {
			if (assignments.get(g.getCode()) != null)
				decoder.putMasterAssignment(g.getCode(),
						new Assignment(assignments.get(
								g.getCode())));
			else
				decoder.putMasterAssignment(g.getCode(),
						new Assignment(g));
		}
		ClassroomFilterManager cfm = new ClassroomFilterManager(
				classroomFilters, classroomList, preferences,
				preferenceBias);
		CollisionManager cm = new CollisionManager();

		Map<String, Subject> groupSubjectMap = new HashMap<String, Subject>();
		for (Subject s : subjectList) {
			for (Group g : s.getGroups()) {
				groupSubjectMap.put(g.getCode(), s);
			}
		}
		GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(cfm, cm,
				groupSubjectMap, subjectList, performRepairs,
				sameClassroomBias);

		FitnessFunction fitnessFunction = new DefaultFitnessFunction(
				decoder, greedyAlgo, fitnessValues);

		List<String> groupCodes = groupList.stream()
				.map(g -> g.getCode())
				.collect(Collectors.toList());
		IndividualManager individualManager = new IndividualManager(
				groupCodes);

		GeneticAlgorithm genAlgo = new GeneticAlgorithm(
				individualLength, populationSize,
				mutationProbability, crossoverProbability,
				maxTimeMilliseconds, numberOfGenerations,
				fitnessFunction, individualManager,
				showGenInfo);

		Individual bestIndividual = genAlgo.geneticAlgorithm();

		// Output files
		List<Assignment> decoded = decoder.decode(bestIndividual);
		Map<String, Assignment> assignmentsMap = greedyAlgo
				.greedyAlgorithm(decoded);

		String rPath = outputFolderPath + outputResultsFilename;
		IndividualPrinter individualPrinter = new IndividualPrinter(
				subjectList, assignmentsMap);

		String result = "#### START EXECUTION ####\n"
				+ Metrics.getInstance()
						.getAllGenerationMetrics()
				+ "\n"
				+ individualPrinter
						.getSummaryMetricsForBestIndividual(
								new FreeLabsFitnessValue(
										freeLabsFnWeight,
										freeLabs),
								new LanguageFitnessValue(
										languageFnWeight,
										subjectList),
								new PreferencesFitnessValue(
										prefsFnWeight,
										preferences),
								new SharedLabsFitnessValue(
										sharedLabsFnWeight,
										subjectList,
										classroomList),
								new SharedTheoryFitnessValue(
										sharedTheoryFnWeight,
										subjectList,
										classroomList))
				+ "##### END EXECUTION #####\n";

		// Results
		fm.appendToFile(rPath + ".txt", result);

		// Business errors
		if (errh.anyErrors()) {
			errh.getCustomErrorMessages()
					.forEach(e -> cli.showError(e));
			cli.showEndOfProgramWithErrors();
			return;
		}
		logh.log(Level.FINE, Program.class.getName(), "main",
				"END Business logic");
	}

	/*
	 * 
	 * 
	 * TODO -> DELETE THIS AUX METHODS IN THE FINAL VERSION!
	 * 
	 * 
	 * 
	 */

	private static void executeAutomationOfAllInstances()
			throws PersistenceException
	{
		String filenameFormat = "files/usecases/experiments/scenarios/%s/%s/io.properties";

		String scenarioName = "ChargedGroups_ChargedConstraints";

		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s1"),
				scenarioName, "charged", "charged",
				"course_20_21", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s2"),
				scenarioName, "charged", "charged",
				"course_20_21", "s2");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s1"),
				scenarioName, "charged", "charged",
				"course_21_22", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s2"),
				scenarioName, "charged", "charged",
				"course_21_22", "s2");

		scenarioName = "ChargedGroups_RegularConstraints";

		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s1"),
				scenarioName, "charged", "regular",
				"course_20_21", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s2"),
				scenarioName, "charged", "regular",
				"course_20_21", "s2");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s1"),
				scenarioName, "charged", "regular",
				"course_21_22", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s2"),
				scenarioName, "charged", "regular",
				"course_21_22", "s2");

		scenarioName = "RegularGroups_ChargedConstraints";

		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s1"),
				scenarioName, "regular", "charged",
				"course_20_21", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s2"),
				scenarioName, "regular", "charged",
				"course_20_21", "s2");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s1"),
				scenarioName, "regular", "charged",
				"course_21_22", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s2"),
				scenarioName, "regular", "charged",
				"course_21_22", "s2");

		scenarioName = "RegularGroups_RegularConstraints";

		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s1"),
				scenarioName, "regular", "regular",
				"course_20_21", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_20_21_s2"),
				scenarioName, "regular", "regular",
				"course_20_21", "s2");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s1"),
				scenarioName, "regular", "regular",
				"course_21_22", "s1");
		executeAutomationOfInstance(
				String.format(filenameFormat, scenarioName,
						"course_21_22_s2"),
				scenarioName, "regular", "regular",
				"course_21_22", "s2");
	}

	private static void executeAutomationOfInstance(String filename,
			String scenarioName, String groupLoad,
			String constraintsLoad, String course, String semester)
			throws PersistenceException
	{
		FileManager fm = new FileManager();

		String format = "SUBJECTS_FILE_PATH = %s\n"
				+ "CLASSROOMS_FILE_PATH = %s\n"
				+ "GROUPS_FILE_PATH = %s\n"
				+ "GROUPSCHEDULE_FILE_PATH = %s\n"
				+ "WEEKS_FILE_PATH = %s\n"
				+ "LOAD_ASSIGNMENTS = true\n"
				+ "ASSIGNMENTS_FILE_PATH = %s\n"
				+ "LOAD_PREFERENCES = true\n"
				+ "PREFERENCES_FILE_PATH = %s\n"
				+ "LOAD_RESTRICTIONS = true\n"
				+ "RESTRICTIONS_FILE_PATH = %s\n"
				+ "OUTPUT_FOLDER_PATH = %s\n"
				+ "OUTPUT_RESULTS_FILENAME = %s\n";

		String subjectPath = String.format(
				"./experiments/general/%s/1_CSV_Asignatura.csv",
				semester);
		String classroomsPath = "./experiments/general/2_CSV_Aula.csv";

		String groupsPath = String.format(
				"./experiments/groups/%s/%s/%s/3_CSV_Grupo.csv",
				groupLoad, course, semester);
		String groupSchedulePath = String.format(
				"./experiments/groups/%s/%s/%s/4_CSV_Horario.csv",
				groupLoad, course, semester);
		String weeksPath = String.format(
				"./experiments/groups/%s/%s/%s/5_CSV_SemanaLectiva.csv",
				groupLoad, course, semester);

		String assignmentsPath = String.format(
				"./experiments/general/%s/6_CSV_Asignaciones.csv",
				semester);

		String preferencesPath = String.format(
				"./experiments/restrictions/%s/%s/7_CSV_Preferencias.csv",
				constraintsLoad, semester);
		String restrictionsPath = String.format(
				"./experiments/restrictions/%s/%s/8_CSV_Restricciones.csv",
				constraintsLoad, semester);

		String outputFolder = String.format(
				"./experiments/scenarios/%s/%s_%s/",
				scenarioName, course, semester);

		String outputResultName = "results";

		String conf = String.format(format, subjectPath, classroomsPath,
				groupsPath, groupSchedulePath, weeksPath,
				assignmentsPath, preferencesPath,
				restrictionsPath, outputFolder,
				outputResultName);

		fm.writeToFile(filename, conf);
	}

	private static void executeAutomationOfConfigFiles()
			throws PersistenceException
	{
		FileManager fm = new FileManager();

		String[] crossProb = { "0.8", "0.9", "1.0" };
		String[] mutaProb = { "0.05", "0.1", "0.2" };
		String[] popsize = { "100", "250", "500" };

		String format = "POP_SIZE = %s\nCROSS_PROB = %s\nMUTA_PROB = %s\n";

		String pathFormat = "files/usecases/experiments/params/combination%02d.properties";

		int count = 1;
		for (String cp : crossProb) {
			for (String mp : mutaProb) {
				for (String ps : popsize) {
					String conf = String.format(format, ps,
							cp, mp);
					String path = String.format(pathFormat,
							count);
					fm.writeToFile(path, conf);
					++count;
				}
			}
		}
	}
}

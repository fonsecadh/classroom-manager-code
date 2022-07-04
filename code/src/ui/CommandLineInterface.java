package ui;

public class CommandLineInterface {
	public static final String VERSION = "1.0.0";
	private static final CommandLineInterface INSTANCE = new CommandLineInterface();

	private CommandLineInterface() {
	}

	public static CommandLineInterface getInstance()
	{
		return INSTANCE;
	}

	public void showProgramDetails()
	{
		String msg = String.format("CLASSMANAGER v%s", VERSION);
		System.out.println(msg);
		System.out.println("Author: Hugo Fonseca Diaz");
		System.out.println(
				"Supervisors: Raul Mencia Cascallana, Carlos Mencia Cascallana");
		System.out.println(
				"Entity: School of Computing Engineering of the University of Oviedo");
		showNewLine();
	}

	public void showVersion()
	{
		String msg = String.format("CLASSMANAGER v%s", VERSION);
		System.out.println(msg);
	}

	public void showHelp()
	{
		// TODO
	}

	public void showMessage(String msg)
	{
		System.out.println(msg);
	}

	public void showMessageWithoutNewLine(String msg)
	{
		System.out.print(msg);
	}

	public void showError(String err)
	{
		System.err.println(err);
	}

	public void showEndOfProgram()
	{
		showNewLine();
		System.out.println("Program terminated with OK status.");
		System.out.println("See the LOG file for details.");
	}

	public void showEndOfProgramWithErrors()
	{
		showNewLine();
		System.out.println("Program terminated with ERROR status.");
		System.out.println("See the LOG file for details.");
	}

	public void showNewLine()
	{
		System.out.println();
	}
}

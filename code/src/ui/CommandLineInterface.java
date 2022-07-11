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
		StringBuilder sb = new StringBuilder();
		appendLine(sb, "classmanager - manages the classrooms of the School "
				+ "of Computer Engineering of the University of Oviedo");
		appendNewLine(sb);
		appendLine(sb, "classmanager OPTION [FILE...]");
		appendNewLine(sb);
		appendLine(sb, "OPTIONS");
		appendNewLine(sb);
		appendLine(sb, "\t-h,--help");
		appendLine(sb, "\t\tOutput a usage message and exit");
		appendLine(sb, "\t-v,--version");
		appendLine(sb, "\t\tOutput the version of classmanager and exit");
		appendLine(sb, "\t-a,--algorithm");
		appendLine(sb, "\t\tPerform the assignments, output the result into the expected "
				+ "files and exit");
		appendLine(sb, "\t-q,--query");
		appendLine(sb, "\t\tSearch for available classrooms, output the result into the expected "
				+ "files and exit");
		appendLine(sb, "\t-t,--transform");
		appendLine(sb, "\t\tTransform the School files into compatible files, output the result "
				+ "into the expected files and exit");
		appendNewLine(sb);

		System.out.println(sb.toString());
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

	private void appendLine(StringBuilder sb, String msg)
	{
		sb.append(msg + "\n");
	}

	private void appendNewLine(StringBuilder sb)
	{
		sb.append("\n");
	}
}

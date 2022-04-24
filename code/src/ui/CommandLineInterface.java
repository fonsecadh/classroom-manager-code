package ui;

import business.config.Config;

public class CommandLineInterface {

	private static final CommandLineInterface INSTANCE = new CommandLineInterface();

	private CommandLineInterface() {

	}

	public static CommandLineInterface getInstance() {
		return INSTANCE;
	}

	public void showProgramDetails() {
		String version = Config.getInstance().getProperty("VERSION");
		String msg = "CLASSROOM MANAGER";
		if (version != null)
			msg += " v" + version;
		System.out.println(msg);
	}

	public void showMessage(String msg) {
		System.out.println(msg);
	}

	public void showError(String err) {
		System.err.println(err);
	}

	public void showEndOfProgram() {
		System.out.println("Program terminated with OK status.");
		System.out.println("See the LOG file for details.");
	}

	public void showEndOfProgramWithErrors() {
		System.out.println("Program terminated with ERROR status.");
		System.out.println("See the LOG file for details.");
	}

}

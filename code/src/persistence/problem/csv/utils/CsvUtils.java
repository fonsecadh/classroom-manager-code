package persistence.problem.csv.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import business.errorhandler.exceptions.PersistenceException;
import business.loghandler.LogHandler;

public class CsvUtils {

	public static List<String> readLinesFromFile(String filename) throws IOException {
		List<String> lines = new ArrayList<String>();
		String line;
		BufferedReader file = new BufferedReader(new FileReader(filename));
		while (file.ready()) {
			line = file.readLine();
			lines.add(line);
		}
		file.close();
		return lines;
	}

	public static List<String> readLinesFromCsv(String filename, String classname, String methodname, String csvname)
			throws PersistenceException {

		List<String> lines = null;

		try {

			lines = CsvUtils.readLinesFromFile(filename);

		} catch (FileNotFoundException e) {

			LogHandler.getInstance().log(Level.SEVERE, classname, methodname, e.getLocalizedMessage(), e);
			throw new PersistenceException(String.format("%s csv file not found", csvname));

		} catch (IOException e) {

			LogHandler.getInstance().log(Level.SEVERE, classname, methodname, e.getLocalizedMessage(), e);
			throw new PersistenceException(String.format("Error encountered while loading the %s csv file.", csvname));

		}

		return lines;

	}

}

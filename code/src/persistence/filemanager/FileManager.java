package persistence.filemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import business.errorhandler.exceptions.PersistenceException;
import business.loghandler.LogHandler;

public class FileManager {

	public void writeToFile(String filename, String content)
			throws PersistenceException
	{
		try {
			BufferedWriter file = new BufferedWriter(
					new FileWriter(filename));
			file.write(content);
			file.close();
		} catch (FileNotFoundException e) {
			LogHandler.getInstance().log(Level.SEVERE,
					FileManager.class.getName(),
					"writeToFile", e.getLocalizedMessage(),
					e);
			throw new PersistenceException(String.format(
					"File not found (%s)", filename));
		} catch (IOException e) {
			LogHandler.getInstance().log(Level.SEVERE,
					FileManager.class.getName(),
					"writeToFile", e.getLocalizedMessage(),
					e);
			throw new PersistenceException(String.format(
					"Could not write to file (%s)",
					filename));
		}
	}

	public List<String> readLinesFromFile(String filename)
			throws PersistenceException
	{
		List<String> lines = new ArrayList<String>();
		try {
			String line;
			BufferedReader file = new BufferedReader(
					new FileReader(filename));
			while (file.ready()) {
				line = file.readLine();
				if (line.trim() != "")
					lines.add(line);
			}
			file.close();
		} catch (FileNotFoundException e) {
			LogHandler.getInstance().log(Level.SEVERE,
					FileManager.class.getName(),
					"readLinesFromFile",
					e.getLocalizedMessage(), e);
			throw new PersistenceException(String.format(
					"File not found (%s)", filename));
		} catch (IOException e) {
			LogHandler.getInstance().log(Level.SEVERE,
					FileManager.class.getName(),
					"readLinesFromFile",
					e.getLocalizedMessage(), e);
			throw new PersistenceException(String.format(
					"Could not read from file (%s)",
					filename));
		}
		return lines;
	}

}

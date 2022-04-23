package persistence.problem.csv.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
	
	public static List<String> readLinesFromCsv(String filename) throws IOException {
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

}

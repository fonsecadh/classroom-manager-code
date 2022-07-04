package ui;

import java.util.ArrayList;
import java.util.List;

public class ArgOption {
	private int option;
	private List<String> configurationFiles;

	public ArgOption(int option, List<String> configurationFiles) {
		super();
		this.option = option;
		this.configurationFiles = new ArrayList<String>(
				configurationFiles);
	}

	public int getOption()
	{
		return option;
	}

	public List<String> getConfigurationFiles()
	{
		return configurationFiles;
	}

	public void setOption(int option)
	{
		this.option = option;
	}

	public void setConfigurationFiles(List<String> configurationFiles)
	{
		this.configurationFiles = configurationFiles;
	}
}

package ShellCommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class BuildIndexCommand 
{
	private String _commandName;
	private String _parameterFileName;
	private static final String LOCATION = "c:" + File.separator + "ir" ;//the location of the command
	private String _command;//command to execute;
	
	public BuildIndexCommand(String commandName, String parameterFileName )
	{
		_commandName = commandName;
		_parameterFileName = parameterFileName;
		set_command(_commandName + " " + _parameterFileName);
		
	}
	
	public String executeCommand (String command)
	{
		StringBuffer output = new StringBuffer();
		
		Process p;
		try 
		{
			p = Runtime.getRuntime().exec(command,null,new File(LOCATION));
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			//read line by line until arrive to the end, presented as "null"
			String line ="";
			while ((line = reader.readLine()) != null)
					{
						output.append(line +"\n");
					}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return output.toString();
		
	}

	public String get_command() {
		return _command;
	}

	public void set_command(String _command) {
		this._command = _command;
	}
	
	
	
}

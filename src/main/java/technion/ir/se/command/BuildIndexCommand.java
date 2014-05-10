package technion.ir.se.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import technion.ir.se.exception.LocationNotFoundException;

public class BuildIndexCommand extends AbstractCommand
{
	private static final String LOCATION_KEY = "buildIndex.location";
	private String command;//command to execute;
	
	public BuildIndexCommand(String commandName, String parameterFileName ) throws LocationNotFoundException
	{
		this.setlocation(LOCATION_KEY);
		command = String.format("%s %s", commandName, parameterFileName); 
	}
	
	public String executeCommand( )
	{
		StringBuffer output = new StringBuffer();
		
		Process p;
		try 
		{
			p = Runtime.getRuntime().exec(command, null, new File(this.location));
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

}

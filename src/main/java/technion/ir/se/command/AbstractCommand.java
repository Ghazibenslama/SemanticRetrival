package technion.ir.se.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import technion.ir.se.exception.LocationNotFoundException;

public abstract class AbstractCommand {
	private static final String FILE_SEPERATOR = "|";
	private Properties props;
	protected String location;

	private void readProperties() {
		props = new Properties();
		InputStream resourceAsStream = this.getClass().getResourceAsStream("/commands.properties"); 
		try {
			props.load(resourceAsStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void setlocation(String commandKey) throws LocationNotFoundException {
		readProperties();
		String property = props.getProperty(commandKey);
		if ( !StringUtils.isEmpty(property) ) {
			location = setSystemFileSeperator(property);
		} else {
			String errorMessage = String.format("Couldn't find property with key:[%s]", commandKey);
			LocationNotFoundException e = new LocationNotFoundException(errorMessage);
			throw e;
		}
	}

	private String setSystemFileSeperator(String property) {
			
		return property.replace(FILE_SEPERATOR, File.separator);
	}
}

package technion.ir.se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class TestUtils {
	public static String getFileContent (String pathToFile) throws IOException
	{
		File createdFile = new File(pathToFile);
		createdFile.deleteOnExit();
		BufferedReader br = new BufferedReader(new FileReader(createdFile));
		String fileContnet = IOUtils.toString(br);
		br.close();
		return fileContnet;
	}
}

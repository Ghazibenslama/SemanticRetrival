package technion.ir.se;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class TestUtils {
	public static String getFileContent (String pathToFile) throws IOException {
		File createdFile = new File(pathToFile);
		createdFile.deleteOnExit();
		BufferedReader br = new BufferedReader(new FileReader(createdFile));
		String fileContnet = IOUtils.toString(br);
		br.close();
		return fileContnet;
	}
	
	public static List<String> getFileLines(String pathToFile) throws IOException, URISyntaxException {
		File file = new File(pathToFile);
		List<String> allLines = Files.readAllLines(Paths.get(file.toURI()), Charset.defaultCharset());
		return allLines;
	}

	public static void deleteFile(String pathToFile) {
		File file = new File(pathToFile);
		file.delete();
	}
}

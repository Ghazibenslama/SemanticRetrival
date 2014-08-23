package technion.ir.se.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;

public class Utils {
	private static final String MAP_FORMAT = "%s Q0 %s %d %.4f Indri";
	private static Properties props = null;
	
	private static final Logger logger = Logger.getLogger(Utils.class);
	public static String RESULT_LINE_FORMAT = "%s, %s\n";


	public static List<Query> readQueries() throws IOException, URISyntaxException {
		String queirsFile = "/queries.txt";
		InputStream queriesFile = Utils.class.getResourceAsStream(queirsFile);
		List<String> allLines = Utils.readAllLines(queriesFile);
		ArrayList<Query> queries = new ArrayList<Query>();
		for (String line : allLines) {
			String[] strings = line.split(":");
			queries.add(new Query(strings[0], strings[1]));
		}
		return queries;
	}
	
	private static List<String> readAllLines(InputStream queriesFile) throws IOException {
		List<String> queriesList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(queriesFile));
		String line = "";
		while ((line = reader.readLine()) != null) {
			queriesList.add(line);
		}
		return queriesList;
	}

	public static String convertPathToExistingPath(String path, String filePrefix) {
		String dir = StringUtils.substringBetween(path, "file:/", "target");
		dir += "target/test-classes" + filePrefix;
		return dir;
	}

	public static StringBuilder createMapFormatForQuery(String queryId, List<RetrivalResult> results){
		StringBuilder builder = new StringBuilder();
		int rank = 1;
		for (RetrivalResult retrivalResult : results) {
			String querySingleResult = String.format(MAP_FORMAT, queryId, retrivalResult.getDocumentId(), rank, retrivalResult.getScore());
			builder.append(querySingleResult);
			builder.append("\n");
			rank++;
		}
		return builder;
	}
	
	public static StringBuilder createMapFormatForQuery(List<ResultFormat> results){
		StringBuilder builder = new StringBuilder();
		for (ResultFormat resultFormat : results) {
			String querySingleResult = String.format(MAP_FORMAT, resultFormat.getQueryID(), resultFormat.getDocumentID(), resultFormat.getRank(), resultFormat.getScore());
			builder.append(querySingleResult);
			builder.append("\n");
		}
		return builder;
	}

	public static File writeFile(StringBuilder trecMap, String filePrefix, String fileExtension) throws IOException {
		File file = new File(filePrefix + fileExtension);
		return Utils.writeFile(trecMap, file);
	}
	
	public static File writeFile(StringBuilder trecMap, File file) throws IOException {
		verifyFileWasCreated(file);
		FileUtils.writeStringToFile(file,trecMap.toString());
		return file;
	}

	private static void verifyFileWasCreated(File file) throws IOException,
			FileNotFoundException {
		if (!file.exists()) {
			boolean wasFileCreated = file.createNewFile();
			if (!wasFileCreated) {
				throw new FileNotFoundException("Failed to create MAP file");
			}
		}
	}
	
	public static String readProperty(String propertyName) {
		String propertyValue = null;
		if (props == null) {
			initPropertyFile();
		}
		propertyValue = props.getProperty(propertyName);
		return propertyValue;
	}

	private static void initPropertyFile() {
		props = new Properties();
		InputStream resourceAsStream = Utils.class.getResourceAsStream("/application.properties"); 
		try {
			props.load(resourceAsStream);
			
		} catch (IOException e) {
			System.err.println("Failed to load property File");
			e.printStackTrace();
		}
	}

	public static List<String> getUniqueValues(List<String> rowTermVector) {
		Set<String> set = new HashSet<String>(rowTermVector);
		return new ArrayList<String>(set);
	}
	
	public static double[] convertIntArrtoDoubleArr(int[] intArr) {
		double[] dest = new double[intArr.length];
		for (int i=0; i< intArr.length; i++) {
			dest[i] = intArr[i];
		}
		return dest;
	}

	public static List<ResultFormat> convertRetrivalResultListToResultFormatList 
				(List<RetrivalResult> retrievalResult, Query query) {

		List<ResultFormat> resultFormatList = new ArrayList<ResultFormat>();
		int i=1;
		for (RetrivalResult retResult : retrievalResult) {
			ResultFormat retFormat = new ResultFormat(query.getId(),
							retResult.getDocumentId(), i, retResult.getScore());
			resultFormatList.add(retFormat);
			i++;
		}
		return resultFormatList;
	}

	public static void writeTrainingResultsInCsv(Map<Integer, Double> trainingResult, 
			StringBuilder builder, String fileName) {
		for (Entry<Integer, Double> entry : trainingResult.entrySet()) {
			builder.append(String.format(RESULT_LINE_FORMAT, entry.getKey(), entry.getValue()));
		}
		try {
			writeFile(builder, fileName, ".csv");
		} catch (IOException e) {
			logger.fatal("Failed to run training results to disk", e);
		}
	}
}

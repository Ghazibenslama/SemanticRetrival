package technion.ir.se.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;

public class Utils {
	private static final String MAP_FORMAT = "%s Q0 %s %d %.4f Indri";
	private static Properties props = null;

	public static List<Query> readQueries() throws IOException, URISyntaxException {
		URL queriesFileUrl = Utils.class.getResource("/queries.txt");
		List<String> allLines = Files.readAllLines(Paths.get(queriesFileUrl.toURI()), Charset.defaultCharset());
		ArrayList<Query> queries = new ArrayList<Query>();
		for (String line : allLines) {
			String[] strings = line.split(":");
			queries.add(new Query(strings[0], strings[1]));
		}
		return queries;
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

	public static void writeMapFile(StringBuilder trecMap, String filePrefix) throws IOException {
		File mapFile = new File(filePrefix + "_baseLineMap.res");
		if (!mapFile.exists()) {
			boolean wasFileCreated = mapFile.createNewFile();
			if (!wasFileCreated) {
				throw new FileNotFoundException("Failed to create MAP file");
			}
		}
		FileUtils.writeStringToFile(mapFile,trecMap.toString());
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
	
	public static List<ResultFormat> convertRetrivalResultListToResultFormatList 
									(List<RetrivalResult> retrievalResult, Query query)
	{
		List<ResultFormat> resultFormatList = new ArrayList<ResultFormat>();
		int i=1;
		for (RetrivalResult retResult : retrievalResult)
		{
			ResultFormat retFormat = new ResultFormat(query.getId(),
														retResult.getDocumentId(),i,
																	retResult.getScore());
			resultFormatList.add(retFormat);
			i++;
		}
		return resultFormatList;
	}
}

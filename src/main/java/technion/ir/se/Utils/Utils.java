package technion.ir.se.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;

public class Utils {
	private static final String MAP_FORMAT = "%s Q0 %d %d %.4f Indri";


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

	public static void writeMapFile(StringBuilder trecMap) throws IOException {
		File mapFile = new File("baseLineMap.res");
		if (!mapFile.exists()) {
			boolean wasFileCreated = mapFile.createNewFile();
			if (!wasFileCreated) {
				throw new FileNotFoundException("Failed to create MAP file");
			}
			FileUtils.writeStringToFile(mapFile,trecMap.toString());
		}
		
	}
}

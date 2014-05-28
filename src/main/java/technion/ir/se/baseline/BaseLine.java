package technion.ir.se.baseline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class BaseLine {
	
	private static final int NUMBER_OF_DOCUMNETS_TO_RETRIVE = 1000;
	private SearchEngine engine;
	
	public BaseLine() {
		engine = new SearchEngine();
	}
	public void createBaseLine() {
		String[] rules = new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		List<Query> queries;
		try {
			queries = Utils.readQueries();
			StringBuilder trecMap = new StringBuilder();
			for (Query query : queries) {
				List<RetrivalResult> results = engine.runQuery(NUMBER_OF_DOCUMNETS_TO_RETRIVE, rules, query.getQueryText());
				StringBuilder mapFormatForQuery = Utils.createMapFormatForQuery(query.getId(), results);
				trecMap.append(mapFormatForQuery.toString());
			}
			Utils.writeMapFile(trecMap);
		} catch (IOException e) {
			System.err.println("failed to read queris or write file");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.err.println("failed to read queris");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("failed to run query");
			e.printStackTrace();
		}
		
		
	}
}

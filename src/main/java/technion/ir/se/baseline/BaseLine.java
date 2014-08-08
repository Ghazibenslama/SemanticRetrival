package technion.ir.se.baseline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class BaseLine {
	
	public static final int NUMBER_OF_DOCUMNETS_TO_RETRIVE = 5;
	private SearchEngine engine;
	private List<Query> queries;
	
	public BaseLine() {
		engine = SearchEngine.getInstance();
		queries = null;
	}
	
	public void createBaseLine() {
		//http://iew3.technion.ac.il/~kurland/sigir12-tutorial.pdf
		String[] rules = new String[]{ "method:dir", "mu:1000", "fbDocs:25", "fbTerms:50", "fbOrigWeight:0.3", "fbMu:0"};
		try {
			queries = Utils.readQueries();
			StringBuilder trecMap = new StringBuilder();
			for (Query query : queries) {
				List<RetrivalResult> results = engine.runQuery(NUMBER_OF_DOCUMNETS_TO_RETRIVE, rules, query.getQueryText());
				StringBuilder mapFormatForQuery = Utils.createMapFormatForQuery(query.getId(), results);
				trecMap.append(mapFormatForQuery.toString());
			}
			String fileName = "baseLineMap";
			Utils.writeMapFile(trecMap, fileName);
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
	
	private String createFileName(String[] rules) {
		StringBuilder builder = new StringBuilder(30);
		for (int i = 0; i < rules.length; i++) {
			String token = rules[i];
			token = token.trim();
			token = token.replace(":", "-");
			builder.append(token);
			builder.append("_");
		}
		String fileName = builder.toString();
		
		return fileName.substring(0, fileName.length() - 1);
	}
	
	private String createFileName(String[] rules, String suffix) {
		String fileName = this.createFileName(rules);
		fileName += "_" + suffix;
		return fileName;
	}
	
	public void createAlternatives() {
		try {
			if (queries == null) {
				queries = Utils.readQueries();
			}
			StringBuilder trecMap = new StringBuilder();
			String[] rules = new String[]{ "method:dir", "mu:1000", "fbDocs:25", "fbTerms:50", "fbOrigWeight:0.3", "fbMu:0"};
			for (Query query : queries) {
				SemanticLogic logic = new SemanticLogic();
				List<RetrivalResult> results = engine.runQuery(NUMBER_OF_DOCUMNETS_TO_RETRIVE, rules, query.getQueryText());
				if (results.isEmpty()){
					System.out.println("didnt find any feedback document at first retrieval.query:" + query.getQueryText());
				} else {
					Map<String, Map<String, Short>> similarityVectors = logic.createSimilarityVectors(results, query);
					List<Query> alternativeQuries = logic.createAlternativeQuries(similarityVectors, query);
					List<ResultFormat> resultFormatsList = logic.submitAlternativeQuries(alternativeQuries);
					if (resultFormatsList != null) {
						StringBuilder builder = Utils.createMapFormatForQuery(resultFormatsList);
						trecMap.append(builder.toString());
					}
				}
			}
			
			String fileName = createFileName(rules, "alternative");
			Utils.writeMapFile(trecMap, fileName);
			
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

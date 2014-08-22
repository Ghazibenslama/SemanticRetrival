package technion.ir.se.baseline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;
import technion.ir.se.trainers.IParamTrainer;
import technion.ir.se.trainers.ParamTrainerFactory;

public class BaseLine {
	
    private static final String PARAM_VALUE = "For %s value of '%d' MAP score is: '%s'";

	private final Logger logger = Logger.getLogger(BaseLine.class);

	public static final int NUMBER_OF_DOCUMNETS_TO_RETRIVE = 1000;
	private SearchEngine engine;
	private List<Query> queries;

	private FusionMutualInformationLogic fusionMutualLogic;
	private SemanticLogic semanticLogic;
	
	public BaseLine() {
		engine = SearchEngine.getInstance();
		queries = null;
	}
	
	public void trainBaseLine(String parameterType) {
		try {
			queries = Utils.readQueries();
			IParamTrainer trainer = ParamTrainerFactory.factory(parameterType, queries, NUMBER_OF_DOCUMNETS_TO_RETRIVE);
			Map<Integer, Double> trainingResult = trainer.train();
			
			for (Map.Entry<Integer, Double> entry : trainingResult.entrySet()) {
				logger.debug(String.format(PARAM_VALUE, parameterType, entry.getKey(), entry.getValue()));
			}
			
		} catch (IOException e) {
			logger.error("failed to read queris", e);
		} catch (URISyntaxException e) {
			logger.error("failed to read queris", e);
		}

		
	}
	public void createBaseLine() {
		//http://iew3.technion.ac.il/~kurland/sigir12-tutorial.pdf
		String[] rules = new String[]{ "method:dir", "mu:1000"};
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
			logger.error("failed to read queris or write file", e);
		} catch (URISyntaxException e) {
			logger.error("failed to read queris", e);
		} catch (Exception e) {
			logger.error("failed to run query", e);
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
			//get mutual related pairs from mutual information
			MutualInformationLogic mil = new MutualInformationLogic(engine);
			PairsRelatedLogic prLogic = new PairsRelatedLogic(mil, queries);
			Map<String, List<List<String>>> pairRelatedMap = prLogic.clacRelatedPairs();
			
			for (Query query : queries) {
				semanticLogic = new SemanticLogic();
				List<RetrivalResult> results = engine.runQuery(NUMBER_OF_DOCUMNETS_TO_RETRIVE, rules, query.getQueryText());
				if (results.isEmpty()){
					logger.info("didnt find any feedback document at first retrieval.query:" + query.getQueryText());
					continue;
				} 
				Map<String, Map<String, Short>> similarityVectors = semanticLogic.createSimilarityVectors(results, query);
				fusionMutualLogic = new FusionMutualInformationLogic(similarityVectors);
				
				updateSimilarityVectorsWithPhrases(pairRelatedMap, similarityVectors);
				
				List<Query> pharseQueryList = fusionMutualLogic.createPharseQuery(pairRelatedMap, query);
				List<Query> alternativeQuries = createAlternativeQuries(similarityVectors, pharseQueryList);
				alternativeQuries.add(query);
				
				List<ResultFormat> resultFormatsList = semanticLogic.submitAlternativeQuries(alternativeQuries);
				if (resultFormatsList != null) {
					StringBuilder builder = Utils.createMapFormatForQuery(resultFormatsList);
					trecMap.append(builder.toString());
				}
			}
			
			String fileName = createFileName(rules, "alternative");
			Utils.writeMapFile(trecMap, fileName);
			
		} catch (IOException e) {
			logger.fatal("failed to read queris or write file", e);
		} catch (URISyntaxException e) {
			logger.fatal("failed to read queris", e);
		} catch (Exception e) {
			logger.fatal("failed to run query", e);
		}
		

	}

	private List<Query> createAlternativeQuries( Map<String, Map<String, Short>> similarityVectors,
			List<Query> pharseQueryList) {
		
		List<Query> alternativeQuries = new ArrayList<Query>();
		
		for (Query pharseQuery : pharseQueryList) {
			List<Query> alternativeQuriesForPhrase = semanticLogic.createAlternativeQuries(similarityVectors, pharseQuery);
			alternativeQuries.addAll(alternativeQuriesForPhrase);
		}
		return alternativeQuries;
	}

	private void updateSimilarityVectorsWithPhrases(
			Map<String, List<List<String>>> pairRelatedMap,
			Map<String, Map<String, Short>> similarityVectors) {
		for (Entry<String, List<List<String>>> entry : pairRelatedMap.entrySet()) {
			Map<String, Map<String, Short>> fusionRelatedSimilarityResult = fusionMutualLogic.fusionRelatedTermsSimilarity( entry.getValue() );
			similarityVectors.putAll(fusionRelatedSimilarityResult);
		}
	}

}

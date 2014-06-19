package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import technion.ir.se.Model.Model;
import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogic {
	
	private static final int NUMBER_OF_ALTERNATIVES_PER_QUERT_TERM = 1;
	private int numberOfAlternativesPerTerm;
	private SearchEngine engine;

	public SemanticLogic() {
		this.numberOfAlternativesPerTerm = NUMBER_OF_ALTERNATIVES_PER_QUERT_TERM;
	}
	
	public Map<String, Map<String, Short>> createSimilarityVectors(List<RetrivalResult> retrivalResult, Query query) {
		SimilarityVectors similarityVectors = new SimilarityVectors();
		List<String> rowTermVector = similarityVectors.buildRowTermVector(retrivalResult);
		Model.getInstance().setModel(rowTermVector);
		Map<String, Map<String, Short>> vectors = similarityVectors.buildVectors(query);
		return vectors;
	}
	
	public List<Query> createAlternativeQuries(Map<String, Map<String, Short>> similarityVectors, Query query) {
		List<Query> alternativeQuries = new ArrayList<Query>();
		List<String> queryTerms = query.getQueryTerms();
		
		removeTermsFromVectors(similarityVectors, queryTerms);
		alternativeQuries = findQueryAlternatives(similarityVectors, query);
		
		return alternativeQuries;
	}
	
	public List<ResultFormat> submitAlternativeQuries(List<Query> alternativeQueries) throws Exception {
		List<List<ResultFormat>> alternativesResult = submitAlternatives(alternativeQueries);
		List<ResultFormat> alternativesResultMerged = mergeResults(alternativesResult);
		return alternativesResultMerged;
	}

	private List<ResultFormat> mergeResults(
			List<List<ResultFormat>> alternativesResult) {
		FusionLogic fusionLogic = new FusionLogic();
		List<ResultFormat> mergeResults = fusionLogic.mergeResults(alternativesResult);
		return mergeResults;
	}

	private List<List<ResultFormat>> submitAlternatives(List<Query> alternativeQueries) throws Exception {
		List<List<ResultFormat>> alternativesResults = new ArrayList<List<ResultFormat>>();
		for (Query query : alternativeQueries) {
			String[] rules = new String[]{ "method:dir", "mu:1000", "fbDocs:50", "fbTerms:50", "fbOrigWeight:0.3", "fbMu:0"};
			List<RetrivalResult> queryResults = engine.runQuery(BaseLine.NUMBER_OF_DOCUMNETS_TO_RETRIVE, rules, query.getQueryText());
			List<ResultFormat> list = Utils.convertRetrivalResultListToResultFormatList(queryResults, query);
			alternativesResults.add(list);
		}
		return alternativesResults;
	}

	private List<Query> findQueryAlternatives(Map<String, Map<String, Short>> similarityVectors, Query query) {
		List<String> queryTerms = query.getQueryTerms();
		List<Query> alternatives = new ArrayList<Query>();
		
		for (String queryTermToReplace : queryTerms) {
			List<SemanticTermScore> similarity = this.findSimilarity(similarityVectors, queryTermToReplace);
			List<String> termAlternatives = this.getTermAlternatives(similarity);
			List<Query> queryAlternatives = this.createQueryAlternatives(query, queryTermToReplace, termAlternatives);
			alternatives.addAll(queryAlternatives);
		}
		return alternatives;
	}
	
	private List<SemanticTermScore> findSimilarity(Map<String, Map<String, Short>> similarityVectors, String queryTerm) {
		TermEquivalentLogic equivalentLogic = new TermEquivalentLogic();
		List<SemanticTermScore> similarVectors = null;
		
//		Map<String, double[]> termVectors = convertMaps(similarityVectors);
//		double[] queryTermVector = termVectors.get(queryTerm);
		try {
			similarVectors = equivalentLogic.similarVectors(similarityVectors , queryTerm);
		} catch (VectorLengthException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return similarVectors;
	}
	
	private List<Query> createQueryAlternatives(Query originalQuery,
			String queryTermToReplace, List<String> alternativesTerms) {
		List<Query> result = new ArrayList<Query>();
		
		for (String term : alternativesTerms) {
			List<String> newQueryTerms = new ArrayList<String>(originalQuery.getQueryTerms());
			int replacmentIndex = newQueryTerms.indexOf(queryTermToReplace);
			newQueryTerms.set(replacmentIndex, term);
			Query q = new Query(originalQuery.getId(), newQueryTerms);
			result.add(q);
		}
		return result;
		
	}

	private void removeTermsFromVectors(Map<String, Map<String, Short>> similarityVectors,
			List<String> queryTerms) {
		for (String term : queryTerms) {
			similarityVectors.remove(term);
		}
	}

	private List<String> getTermAlternatives(List<SemanticTermScore> termScores) {
		ArrayList<String> resultList = new ArrayList<String>(numberOfAlternativesPerTerm);
		List<SemanticTermScore> subList = termScores.subList(0, numberOfAlternativesPerTerm);
		for (SemanticTermScore semanticTermScore : subList) {
			resultList.add( semanticTermScore.getTerm() );
		}
		return resultList;
	}
	
	private Map<String, double[]> convertMaps(Map<String, int[]> intMap) {
		
		HashMap<String, double[]> doubleMap = new HashMap<String, double[]>();
		for (Entry<String, int[]> entry : intMap.entrySet()) {
			String key = entry.getKey();
			double[] value = Utils.convertIntArrtoDoubleArr(entry.getValue()); 
			doubleMap.put(key, value);
		}
		return doubleMap;
	}
}

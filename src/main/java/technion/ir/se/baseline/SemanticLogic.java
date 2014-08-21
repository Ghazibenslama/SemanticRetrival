package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import technion.ir.se.Model.Model;
import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogic {
	private final Logger logger = Logger.getLogger(SemanticLogic.class);
	public SemanticLogic() {
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
		
		alternativeQuries = findQueryAlternatives(similarityVectors, query);
		return alternativeQuries;
	}
	
	public List<ResultFormat> submitAlternativeQuries(List<Query> alternativeQueries) throws Exception {
		List<List<ResultFormat>> alternativesResult = submitAlternatives(alternativeQueries);
		List<ResultFormat> alternativesResultMerged = mergeResults(alternativesResult);
		return alternativesResultMerged;
	}

	private List<ResultFormat> mergeResults(List<List<ResultFormat>> alternativesResult) {
		FusionLogic fusionLogic = new FusionLogic();
		List<ResultFormat> mergeResults = fusionLogic.mergeResults(alternativesResult);
		return mergeResults;
	}

	private List<List<ResultFormat>> submitAlternatives(List<Query> alternativeQueries) throws Exception {
		List<List<ResultFormat>> alternativesResults = new ArrayList<List<ResultFormat>>();
		for (Query query : alternativeQueries) {
			String[] rules = new String[]{ "method:dir", "mu:1000", "fbDocs:50", "fbTerms:50", "fbOrigWeight:0.3", "fbMu:0"};
			SearchEngine engine = SearchEngine.getInstance();
			List<RetrivalResult> queryResults = engine.runQuery(BaseLine.NUMBER_OF_DOCUMNETS_TO_RETRIVE, rules, query.getQueryText());
			List<ResultFormat> list = Utils.convertRetrivalResultListToResultFormatList(queryResults, query);
			alternativesResults.add(list);
		}
		return alternativesResults;
	}

	private List<Query> findQueryAlternatives(Map<String, Map<String, Short>> similarityVectors, Query query) {
		List<String> queryTerms = query.getQueryTerms();
		List<Query> alternativesList = new ArrayList<Query>();
		AlternativesLogic alternatives = new AlternativesLogic();
		
		for (String queryTermToReplace : queryTerms) {
			try {
				List<SemanticTermScore> similarity = this.findSimilarity(similarityVectors, queryTermToReplace);
				List<String> termAlternatives = alternatives.getTermAlternatives(similarity, query.getQueryTerms().size());
				List<Query> queryAlternatives = this.createQueryAlternatives(query, queryTermToReplace, termAlternatives);
				alternativesList.addAll(queryAlternatives);
			} catch (IllegalArgumentException e) {
				System.err.println(String.format("Faield to execute finding alternative for term '%s' of query '%s'", queryTermToReplace, queryTerms));
			}
		}
		return alternativesList;
	}
	
	private List<SemanticTermScore> findSimilarity(Map<String, Map<String, Short>> similarityVectors, String queryTerm) {
		TermEquivalentLogic equivalentLogic = new TermEquivalentLogic();
		List<SemanticTermScore> similarVectors = null;
		
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
			logger.info("QueryID:"+" "+originalQuery.getId()+" "+"alternative: "+ newQueryTerms.toString());
			result.add(q);
		}
		return result;
		
	}

}

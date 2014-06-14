package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;

public class SemanticLogic {
	
	private int numberOfAlternativesPerTerm = 1;

	public Map<String, int[]> createSimilarityVectors(List<RetrivalResult> retrivalResult, Query query) {
		SimilarityVectors similarityVectors = new SimilarityVectors();
		similarityVectors.buildRowTermVector(retrivalResult);
		Map<String, int[]> vectors = similarityVectors.buildVectors(query);
		return vectors;
	}
	
	public List<SemanticTermScore> findSimilarity(Map<String, int[]> vectors, String queryTerm) {
		TermEquivalentLogic equivalentLogic = new TermEquivalentLogic();
		List<SemanticTermScore> similarVectors = null;
		
		Map<String, double[]> termVectors = convertMaps(vectors);
		double[] queryTermVector = termVectors.get(queryTerm);
		try {
			similarVectors = equivalentLogic.similarVectors(termVectors , queryTermVector);
		} catch (VectorLengthException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return similarVectors;
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

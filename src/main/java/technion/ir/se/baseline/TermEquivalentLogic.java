package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import technion.ir.se.Model.Model;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;

public class TermEquivalentLogic 
{
	/**
	 * Returns a list of {@link SemanticTermScore}. The list is sorted, so the first element in it 
	 * has the highest similarity score.
	 * @param map
	 * @param queryTerm
	 * @return
	 * @throws VectorLengthException
	 */
	public List<SemanticTermScore> similarVectors (Map<String, Map<String, Short>> 
		map, String queryTerm) throws VectorLengthException
	
	{
		List<SemanticTermScore> sortedSimilarityList = new ArrayList <SemanticTermScore>();
		SimilarityLogic logic = new SimilarityLogic();
		
		double[] queryVector = convertMapToVector(map.get(queryTerm));
		for (Map.Entry<String, double[]> entry : map.entrySet())
		{
			try {
				double similarityScore = logic.calculateSimilarity(queryTerm, entry.getValue());
				SemanticTermScore semanticTermScore = new SemanticTermScore(entry.getKey(),similarityScore);
				sortedSimilarityList.add(semanticTermScore);
			} catch (VectorLengthException e) {
				String message = e.getMessage() + "\n";
				message+= String.format("vector of '%s' is not in same size as query vector", entry.getKey());
				VectorLengthException exception = new VectorLengthException(message);
				throw exception;
			}
		}

		Collections.sort(sortedSimilarityList);
		return sortedSimilarityList;
	}

	private double[] convertMapToVector(Map<String, Short> map) {
		List<String> model = Model.getInstance().getModel();
		double[] vector = new double[model.size()];
		for (int i = 0; i < model.size(); i++) {
			String term = model.get(i);
			Short frequency = map.get(term);
			vector[i] = (frequency != null) ? frequency : 0;
		}
		return vector;
		
	}
	
	
	
}
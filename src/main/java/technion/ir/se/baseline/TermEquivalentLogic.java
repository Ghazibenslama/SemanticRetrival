package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;

public class TermEquivalentLogic 
{
	public List<SemanticTermScore> similarVectors (Map<String,double[]> 
		termVectors, double[] qTermVector) throws VectorLengthException
	
	{
		List<SemanticTermScore> sortedSimilarityList = new ArrayList <SemanticTermScore>();
		SimilarityLogic logic = new SimilarityLogic();
		for (Map.Entry<String, double[]> entry : termVectors.entrySet())
		{
			try {
				double similarityScore = logic.calculateSimilarity(qTermVector, entry.getValue());
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
	
	
	
}
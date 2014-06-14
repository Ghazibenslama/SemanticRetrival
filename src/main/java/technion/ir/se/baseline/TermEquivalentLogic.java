package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
			double similarityScore = logic.calculateSimilarity(qTermVector, entry.getValue());
			SemanticTermScore semanticTermScore = new SemanticTermScore(entry.getKey(),similarityScore);
			sortedSimilarityList.add(semanticTermScore);
		}
		Collections.sort(sortedSimilarityList);
		
		return sortedSimilarityList;
				
	}
	
	
	
}
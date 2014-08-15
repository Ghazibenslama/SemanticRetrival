package technion.ir.se.baseline;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FusionMutualInformationLogic {
	ArrayList<List<String>> relatedTermsList;
	Map<String, Map<String, Short>> similarityVectors;

	public FusionMutualInformationLogic (ArrayList<List<String>> resultLists, Map<String, Map<String, Short>> similarityVectors )
	{
		this.relatedTermsList = resultLists;
		this.similarityVectors = similarityVectors;
	}
	
	public Map<String,Map<String,Short>> fusionRelatedTermsSimilarity ()
	{
		Map<String, Map<String, Short>> FusionSimilarityVectors = new THashMap<String, Map<String, Short>>();
		
		for (List<String> pairTermsList : relatedTermsList) 
		{
			Map<String,Short> firstTermSimilarityVector = this.similarityVectors.get(pairTermsList.get(0));
			Map<String,Short> secondTermSimilarityVector = this.similarityVectors.get(pairTermsList.get(1));
			Map <String,Short> fusionResult = fusionTwoSimilarityVectors(firstTermSimilarityVector, secondTermSimilarityVector);
			String newKey = pairTermsList.get(0) + "+" + pairTermsList.get(1); 
			FusionSimilarityVectors.put(newKey, fusionResult);
			
			return FusionSimilarityVectors;
			
		}
		
		
		
		return FusionSimilarityVectors;
	}
	
	public Map<String,Short> fusionTwoSimilarityVectors(Map<String,Short> firstSimilarityVector, 
														 Map<String,Short> secondSimilarityVector)
						 
	{
		Map<String,Short> combinedResult = new HashMap<String,Short>(secondSimilarityVector);
		
		for (Entry<String, Short> entry : firstSimilarityVector.entrySet())
		{
			if (combinedResult.containsKey(entry.getKey()))
				
				combinedResult.put(entry.getKey(), (short) (combinedResult.get(entry.getKey()) + entry.getValue()));
			else
				
				combinedResult.put(entry.getKey(), (short)entry.getValue());
		}
		
		return combinedResult;
	}
}

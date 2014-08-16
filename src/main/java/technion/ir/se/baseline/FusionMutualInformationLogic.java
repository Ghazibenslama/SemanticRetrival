package technion.ir.se.baseline;
import gnu.trove.map.hash.THashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FusionMutualInformationLogic {
	private List<List<String>> relatedTermsList;
	private Map<String, Map<String, Short>> similarityVectors;
	
	private final String KEY_TEMPLATE = "%s %s";

	public FusionMutualInformationLogic (List<List<String>> resultLists, Map<String, Map<String, Short>> similarityVectors )
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
			// if only one null, return the map which is not null and the key will be the combined key of both maps
			if ( firstTermSimilarityVector != null || secondTermSimilarityVector != null )
			{
				Map <String,Short> fusionResult = fusionTwoSimilarityVectors(firstTermSimilarityVector, secondTermSimilarityVector);
				String newKey = String.format(KEY_TEMPLATE, pairTermsList.get(0), pairTermsList.get(1));
				FusionSimilarityVectors.put(newKey, fusionResult);
			}
		}
		
		
		
		return FusionSimilarityVectors;
	}
	
	public Map<String,Short> fusionTwoSimilarityVectors(Map<String,Short> firstSimilarityVector, 
														 Map<String,Short> secondSimilarityVector)
						 
	{
		if (firstSimilarityVector == null)
		{
			return secondSimilarityVector;
		}
		if (secondSimilarityVector == null)
		{
			return firstSimilarityVector;
		}
		
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

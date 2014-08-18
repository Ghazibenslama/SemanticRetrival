package technion.ir.se.baseline;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import technion.ir.se.dao.Query;

public class FusionMutualInformationLogic {
	static final Logger logger = Logger.getLogger(BaseLine.class);
	private Map<String, Map<String, Short>> similarityVectors;
	private final String KEY_TEMPLATE = "%s %s";

	public FusionMutualInformationLogic ( Map<String, Map<String, Short>> similarityVectors ) {
		this.similarityVectors = similarityVectors;
	}
	
	public Map<String,Map<String,Short>> fusionRelatedTermsSimilarity (List<List<String>> fusionLists)
	{
		Map<String, Map<String, Short>> FusionSimilarityVectors = new THashMap<String, Map<String, Short>>();
		
		for (List<String> pairTermsList : fusionLists) 
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
	
	public Map<String, Short> fusionTwoSimilarityVectors(
			Map<String, Short> firstSimilarityVector,
			Map<String, Short> secondSimilarityVector) {
		
		if (firstSimilarityVector == null) {
			return secondSimilarityVector;
		}
		if (secondSimilarityVector == null) {
			return firstSimilarityVector;
		}

		Map<String, Short> combinedResult = new HashMap<String, Short>(secondSimilarityVector);

		for (Entry<String, Short> entry : firstSimilarityVector.entrySet()) {
			if (combinedResult.containsKey(entry.getKey())) {
				combinedResult.put(entry.getKey(), (short) (combinedResult.get(entry.getKey()) + entry.getValue()));
			} else {
				combinedResult.put(entry.getKey(), (short) entry.getValue());
			}
		}
		return combinedResult;
	}
	
	public List<Query> createPharseQuery (Map<String, List<List<String>>> pairRelatedMap , Query query) {
		ArrayList<Query> pharseQueryList = new ArrayList<Query>();
		String queryID = query.getId();
		if (!pairRelatedMap.containsKey(queryID)) {
			pharseQueryList.add(query);
		} else {
			List<List<String>> list = pairRelatedMap.get(queryID);
			for (List<String> pair : list) {
				List<String> originalQueryTerms = new ArrayList<String>( query.getQueryTerms() );
				originalQueryTerms.removeAll(pair);
				ArrayList<String> newQueryTerms = new ArrayList<String>(originalQueryTerms);
				newQueryTerms.add(String.format(KEY_TEMPLATE, pair.get(0), pair.get(1)));
				pharseQueryList.add(new Query(queryID, newQueryTerms));
			}
		}
		
		return pharseQueryList;
	}
}

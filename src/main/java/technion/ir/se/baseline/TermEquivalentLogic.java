package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;

public class TermEquivalentLogic 
{
	private SimilarityLogic similarityLogic;
	
	public TermEquivalentLogic() {
		similarityLogic = new SimilarityLogic();
	}

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
		
		Map<String, Short> querySparseVector = map.get(queryTerm);
		for (Entry<String, Map<String, Short>> entry : map.entrySet())
		{
			//Do not compare vector of query Term to it self
			if (!entry.getKey().equals(queryTerm)) {
				Map<String, Short> termSparseVector = entry.getValue();
				double similarityScore = similarityLogic.calculateSimilarity(querySparseVector, termSparseVector);
				if (!Double.isNaN(similarityScore)) {
					SemanticTermScore semanticTermScore = new SemanticTermScore(entry.getKey(), similarityScore);
					sortedSimilarityList.add(semanticTermScore);
				}
			}
		}

		Collections.sort(sortedSimilarityList);
		Collections.reverse(sortedSimilarityList);
		return sortedSimilarityList;
	}
}
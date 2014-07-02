package technion.ir.se.baseline;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class SimilarityLogic {
	
	/**
	 * Calculates <b>cosine</b> similarity of two vectors that are saved as Map
	 * <br>
	 * COSINE similarity - normalized dot product
	 * 
	 * @param vectorOne Map representation  of vector one
	 * @param vectorTwo Map representation  of vector two
	 * @return Similarity of two vectors
	 */
	public double calculateSimilarity(Map<String, Short> vectorOne, Map<String, Short> vectorTwo) {
		Set<String> both = Sets.newHashSet(vectorOne.keySet());
        both.retainAll(vectorTwo.keySet());
        
        double sclar = 0, norm1 = 0, norm2 = 0;
        
        for (String k : both) {
        	sclar += vectorOne.get(k) * vectorTwo.get(k);
        }
        norm1 = calculateNorm(vectorOne);
        norm2 = calculateNorm(vectorTwo);
        
        return sclar / (norm1 * norm2);
	}
	
	/**
	 * Calculates the Norm of a given vector which is represented by {@link Map} of <{@link String}, {@link Short}>
	 * @param {@link Map} of <{@link String}, {@link Short}> which is actually a vector
	 * @return norm of a vector
	 * 
	 */

	private double calculateNorm(Map<String, Short> vectorOne) {
		double norm = 0;
		for (String k : vectorOne.keySet()) {
			norm += vectorOne.get(k) * vectorOne.get(k);
        }
		return Math.sqrt(norm);
	}
}

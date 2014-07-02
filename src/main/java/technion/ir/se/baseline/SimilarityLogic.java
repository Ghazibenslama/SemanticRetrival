package technion.ir.se.baseline;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import technion.ir.se.exception.VectorLengthException;

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
	
	/**
	 * Calculates similarity of type given between two vectors
	 * <br>
	 * COSINE similarity - normalized dot product
	 * 
	 * @param vec1 Array of doubles for first vector
	 * @param vec2 Array of doubles for second vector
	 * @return the distance between the two vectors
	 * @throws VectorLengthException 
	 */
	public double calculateSimilarity(double[] vec1, double[] vec2) throws VectorLengthException {
		double similarity = 0;
		if (vec1.length != vec2.length)
		{
			throw new VectorLengthException("Vectors Lengths are not equals");
		}
		for (int i = 0; i < vec1.length; i++) 
			{
				similarity += vec1[i] * vec2[i];
			}
	    
		similarity = similarity / (vectorLength(vec1) * vectorLength(vec2));

		
		return similarity;
	}
	
	/**
	 * Calculates the length of the given vector
	 * @param vec double array of the vector
	 * @return length of the vector as a double
	 * 
	 */
	private static double vectorLength(double[] vec) {
		double len = 0;
		for (int i = 0; i < vec.length; i++) {
			len += vec[i] * vec[i];
		}
		len = Math.sqrt(len);
		return len;			
	}
	
}

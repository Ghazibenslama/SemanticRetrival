package technion.ir.se.baseline;

import technion.ir.se.exception.VectorLengthException;

public class SimilarityLogic
{
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

package technion.ir.se.baseline;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.ImmutableMap;

import technion.ir.se.exception.VectorLengthException;

public class SimilarityLogicTest {
	
	private SimilarityLogic classUnderTest;
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SimilarityLogic();
	}

	@After
	public void tearDown() throws Exception {
	}

/*	@Test
	public void testCalculateSimilarity_equalVectors() throws VectorLengthException {
		double[] vec1 = new double[]{1,2,3,4,5,6};
		double[] vec2 = new double[]{1,2,3,4,5,6};
		double similarity = classUnderTest.calculateSimilarity(vec1, vec2);
		Assert.assertEquals("cosine of equal vectors should be 1", 1, similarity, 0);
	}
	
	@Test
	public void testCalculateSimilarity_differentVectors() throws VectorLengthException {
		double[] vec1 = new double[]{1,2,3,0,0,0};
		double[] vec2 = new double[]{0,0,0,4,5,6};
		double similarity = classUnderTest.calculateSimilarity(vec1, vec2);
		Assert.assertEquals("cosine of different vectors should be 0", 0, similarity, 0);
	}
	
	@Test
	public void testCalculateSimilarity_someVectors() throws VectorLengthException {
		double[] vec1 = new double[]{1,2,0,0,9.4,0};
		double[] vec2 = new double[]{0,0.3,0,4,33.1,6};
		double expected = 311.74 / (9.66229786334 * 33.8777212929);
		double similarity = classUnderTest.calculateSimilarity(vec1, vec2);
		Assert.assertEquals("cosine value is not as expected", expected, similarity, 0.0001);
	}*/
	
	@Test
	public void testCalculateSimilarity_equalVectors() throws VectorLengthException {
		Map<String, Short> vector = ImmutableMap.of("a", (short)1, "b", (short)2, "c", (short)3, "d", (short)4, "e", (short)5);
//		double[] vec1 = new double[]{1,2,3,4,5,6};
//		double[] vec2 = new double[]{1,2,3,4,5,6};
		double similarity = classUnderTest.calculateSimilarity(vector, vector);
		Assert.assertEquals("cosine of equal vectors should be 1", 1, similarity, 0);
	}
	
	@Test
	public void testCalculateSimilarity_differentVectors() throws VectorLengthException {
		Map<String, Short> vectorOne = ImmutableMap.of("a", (short)1, "b", (short)2, "c", (short)3, "d", (short)0, "e", (short)0);
		Map<String, Short> vectorTwo = ImmutableMap.of("a", (short)0, "b", (short)0, "c", (short)0, "d", (short)4, "e", (short)5);

		double similarity = classUnderTest.calculateSimilarity(vectorOne, vectorTwo);
		Assert.assertEquals("cosine of different vectors should be 0", 0, similarity, 0);
	}
	
	@Test
	public void testCalculateSimilarity_someVectorsWithZeroes() throws VectorLengthException {
		Map<String, Short> vectorOne = ImmutableMap.of("a", (short)1, "b", (short)2, "c", (short)0, "d", (short)9, "e", (short)0);
		Map<String, Short> vectorTwo = ImmutableMap.of("a", (short)0, "b", (short)3, "c", (short)4, "d", (short)33, "e", (short)6);
		
		double expected = 303 / (9.2736184955 * 33.9116499156);
		double similarity = classUnderTest.calculateSimilarity(vectorOne, vectorTwo);
		Assert.assertEquals("cosine value is not as expected", expected, similarity, 0.0001);
	}
	
	@Test
	public void testCalculateSimilarity_someVectorsWithOutZeroes() throws VectorLengthException {
		Map<String, Short> vectorOne = ImmutableMap.of("a", (short)1, "b", (short)2, "d", (short)9);
		Map<String, Short> vectorTwo = ImmutableMap.of("b", (short)3, "c", (short)4, "d", (short)33, "e", (short)6);
		
		double expected = 303 / (9.2736184955 * 33.9116499156);
		double similarity = classUnderTest.calculateSimilarity(vectorOne, vectorTwo);
		Assert.assertEquals("cosine value is not as expected", expected, similarity, 0.0001);
	}
	
	@Test
	public void tetsCalculateNorm() throws Exception {
		Map<String, Short> vector = ImmutableMap.of("a", (short)2, "b", (short)1, "c", (short)0, "d", (short)7);
		Double norm = Whitebox.<Double>invokeMethod(classUnderTest, "calculateNorm", vector);
		Assert.assertEquals("norm of vector is wrong", 7.34847, norm, 0.0001);
	}


}

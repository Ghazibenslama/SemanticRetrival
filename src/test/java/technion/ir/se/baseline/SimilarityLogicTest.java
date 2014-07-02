package technion.ir.se.baseline;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

	@Test
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
	}


}

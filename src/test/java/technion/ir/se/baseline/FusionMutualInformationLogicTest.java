package technion.ir.se.baseline;

import static org.junit.Assert.*;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FusionMutualInformationLogicTest {

	private FusionMutualInformationLogic classUnderTest;
	private Map<String,Short> firstSimilarityVector = new THashMap<String,Short>();
	private Map<String,Short> secondSimilarityVector = new THashMap<String,Short>();
	private Map<String,Short> thirdSimilarityVector = new THashMap<String,Short>();
	private ArrayList<List<String>> resultLists = new ArrayList<List<String>>();
	private Map<String,Map<String,Short>> similarityVectors = new THashMap<String, Map<String,Short>>();
	
	
	@Before
	public void setUp() throws Exception {
		InitializeParameters();
		classUnderTest = new FusionMutualInformationLogic(resultLists, similarityVectors);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void fusionTwoSimilarityVectorsTest() 
	{
		Map<String,Short> fusionTwoSimilarityVectorsResult1 = classUnderTest.fusionTwoSimilarityVectors(firstSimilarityVector, secondSimilarityVector);
		Map<String,Short> fusionTwoSimilarityVectorsResult2 = classUnderTest.fusionTwoSimilarityVectors(secondSimilarityVector,thirdSimilarityVector);
		
		Assert.assertEquals("size of merged list is not correct", 5, fusionTwoSimilarityVectorsResult1.size());
		Assert.assertEquals("the value of Technion is not correct", Short.valueOf((short) 6), fusionTwoSimilarityVectorsResult1.get("Technion"));
		Assert.assertEquals("the value of Haifa is not correct", Short.valueOf((short) 5), fusionTwoSimilarityVectorsResult1.get("Haifa"));
		Assert.assertEquals("the value of army is not correct", Short.valueOf((short) 5), fusionTwoSimilarityVectorsResult1.get("Army"));
		Assert.assertEquals("the value of Sap is not correct", Short.valueOf((short) 4), fusionTwoSimilarityVectorsResult1.get("Sap"));
		Assert.assertEquals("the value of Thailand is not correct", Short.valueOf((short) 1), fusionTwoSimilarityVectorsResult1.get("Tailand"));
		
		Assert.assertEquals("size of merged list is not correct", 6, fusionTwoSimilarityVectorsResult2.size());
		Assert.assertEquals("the value of Technion is not correct", Short.valueOf((short) 2), fusionTwoSimilarityVectorsResult2.get("Technion"));
		Assert.assertEquals("the value of Haifa is not correct", Short.valueOf((short) 2), fusionTwoSimilarityVectorsResult2.get("Haifa"));
		Assert.assertEquals("the value of Sap is not correct", Short.valueOf((short) 4), fusionTwoSimilarityVectorsResult2.get("Sap"));
		Assert.assertEquals("the value of Tailand is not correct", Short.valueOf((short) 1), fusionTwoSimilarityVectorsResult2.get("Tailand"));
		Assert.assertEquals("the value of Army is not correct", Short.valueOf((short) 4), fusionTwoSimilarityVectorsResult2.get("Army"));
		Assert.assertEquals("the value of Delek is not correct", Short.valueOf((short) 1), fusionTwoSimilarityVectorsResult2.get("Delek"));
		
	}
	
	public void fusionRelatedTermsSimilarity() 
	{
		Map<String,Map<String,Short>> fusionRelatedTermsSimilarityResult = classUnderTest.fusionRelatedTermsSimilarity();
		
		Assert.assertEquals("size of merged list is not correct", 2, fusionRelatedTermsSimilarityResult.size());
		Assert.assertFalse("The key name is not corrrect", !fusionRelatedTermsSimilarityResult.containsKey("Eilon+Sapir"));
		Assert.assertFalse("The key name is not corrrect", !fusionRelatedTermsSimilarityResult.containsKey("Sapir+Topaz"));
	}
	
	
	private void InitializeParameters()
	{
		resultLists = new ArrayList<List<String>>();
		resultLists.add( new ArrayList<String>(Arrays.asList("Eilon", "Sapir")) );
		resultLists.add( new ArrayList<String>(Arrays.asList("Sapir", "Topaz")) );
		firstSimilarityVector.put("Technion", (short) 4);
		firstSimilarityVector.put("Haifa", (short) 3);
		firstSimilarityVector.put("Army", (short) 5);
		
		secondSimilarityVector.put("Technion", (short) 2);
		secondSimilarityVector.put("Haifa", (short) 2);
		secondSimilarityVector.put("Sap", (short) 4);
		secondSimilarityVector.put("Tailand", (short) 1);
		
		thirdSimilarityVector.put("Army", (short) 4);
		thirdSimilarityVector.put("Delek", (short) 1);

		similarityVectors.put("Eilon", firstSimilarityVector);
		similarityVectors.put("Sapir", secondSimilarityVector);
		similarityVectors.put("Topaz", thirdSimilarityVector);
	}
}

package technion.ir.se.baseline;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.dao.Query;

import com.google.common.collect.ImmutableMap;

public class FusionMutualInformationLogicTest {

	private FusionMutualInformationLogic classUnderTest;
	private Map<String,Short> firstSimilarityVector = new THashMap<String,Short>();
	private Map<String,Short> secondSimilarityVector = new THashMap<String,Short>();
	private Map<String,Short> thirdSimilarityVector = new THashMap<String,Short>();
	private Map<String,Short> forthSimilarityVector = new THashMap<String,Short>();
	private List<List<String>> resultLists = new ArrayList<List<String>>();
	private Map<String,Map<String,Short>> similarityVectors = new THashMap<String, Map<String,Short>>();
	
	
	@Before
	public void setUp() throws Exception {
		InitializeParameters();
		classUnderTest = new FusionMutualInformationLogic(similarityVectors);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void fusionTwoSimilarityVectorsTest() 
	{
		Map<String,Short> fusionTwoSimilarityVectorsResult1 = classUnderTest.fusionTwoSimilarityVectors(firstSimilarityVector, secondSimilarityVector);
		Map<String,Short> fusionTwoSimilarityVectorsResult2 = classUnderTest.fusionTwoSimilarityVectors(secondSimilarityVector,thirdSimilarityVector);
		Map<String,Short> fusionTwoSimilarityVectorsResult3 = classUnderTest.fusionTwoSimilarityVectors(thirdSimilarityVector,forthSimilarityVector);
		
		
		Assert.assertEquals("size of merged list is not correct", 5, fusionTwoSimilarityVectorsResult1.size());
		Assert.assertEquals("the value of Technion is not correct", Short.valueOf((short) 6), fusionTwoSimilarityVectorsResult1.get("Technion"));
		Assert.assertEquals("the value of Haifa is not correct", Short.valueOf((short) 5), fusionTwoSimilarityVectorsResult1.get("Haifa"));
		Assert.assertEquals("the value of army is not correct", Short.valueOf((short) 5), fusionTwoSimilarityVectorsResult1.get("Army"));
		Assert.assertEquals("the value of Sap is not correct", Short.valueOf((short) 4), fusionTwoSimilarityVectorsResult1.get("Sap"));
		Assert.assertEquals("the value of Thailand is not correct", Short.valueOf((short) 1), fusionTwoSimilarityVectorsResult1.get("Tailand"));
		
		Assert.assertEquals("size of merged list is not correct", 4, fusionTwoSimilarityVectorsResult2.size());
		Assert.assertEquals("the value of Technion is not correct", Short.valueOf((short) 2), fusionTwoSimilarityVectorsResult2.get("Technion"));
		Assert.assertEquals("the value of Haifa is not correct", Short.valueOf((short) 2), fusionTwoSimilarityVectorsResult2.get("Haifa"));
		Assert.assertEquals("the value of Sap is not correct", Short.valueOf((short) 4), fusionTwoSimilarityVectorsResult2.get("Sap"));
		Assert.assertEquals("the value of Tailand is not correct", Short.valueOf((short) 1), fusionTwoSimilarityVectorsResult2.get("Tailand"));
		
		Assert.assertEquals("size of merged list is not correct", 2, fusionTwoSimilarityVectorsResult3.size());
		Assert.assertEquals("the value of Army is not correct", Short.valueOf((short) 4), fusionTwoSimilarityVectorsResult3.get("Army"));
		Assert.assertEquals("the value of Delek is not correct", Short.valueOf((short) 1), fusionTwoSimilarityVectorsResult3.get("Delek"));
	}
	
	@Test
	public void fusionRelatedTermsSimilarityTest() 
	{
		Map<String,Map<String,Short>> fusionRelatedTermsSimilarityResult = classUnderTest.fusionRelatedTermsSimilarity( resultLists );
		
		Assert.assertEquals("size of merged list is not correct", 3, fusionRelatedTermsSimilarityResult.size());
		Assert.assertFalse("The key name is not corrrect", !fusionRelatedTermsSimilarityResult.containsKey("Eilon Sapir"));
		Assert.assertFalse("The key name is not corrrect", !fusionRelatedTermsSimilarityResult.containsKey("Sapir Topaz"));
		Assert.assertFalse("The key name is not corrrect", !fusionRelatedTermsSimilarityResult.containsKey("Topaz Eyal"));
	}
	
	
	private void InitializeParameters()
	{
		resultLists = new ArrayList<List<String>>();
		resultLists.add( new ArrayList<String>(Arrays.asList("Eilon", "Sapir")) );
		resultLists.add( new ArrayList<String>(Arrays.asList("Sapir", "Topaz")) );
		resultLists.add( new ArrayList<String>(Arrays.asList("Topaz", "Eyal")) );
		firstSimilarityVector.put("Technion", (short) 4);
		firstSimilarityVector.put("Haifa", (short) 3);
		firstSimilarityVector.put("Army", (short) 5);
		
		secondSimilarityVector.put("Technion", (short) 2);
		secondSimilarityVector.put("Haifa", (short) 2);
		secondSimilarityVector.put("Sap", (short) 4);
		secondSimilarityVector.put("Tailand", (short) 1);
		
		forthSimilarityVector.put("Army", (short) 4);
		forthSimilarityVector.put("Delek", (short) 1);

		similarityVectors.put("Eilon", firstSimilarityVector);
		similarityVectors.put("Sapir", secondSimilarityVector);
		similarityVectors.put("Topaz", thirdSimilarityVector);
		similarityVectors.put("Eyal", forthSimilarityVector);
	}
	
	@Test
	public void testCreatePharseQuery_queryNotFusion() {
		Map<String, List<List<String>>> pairRelatedMap = ImmutableMap.of("2", Arrays.asList(Arrays.asList("san", "fransisco")));
		Query query = new Query("5", "san Andress GTA");

		List<Query> list = classUnderTest.createPharseQuery(pairRelatedMap, query);
		Assert.assertEquals("List should have <1> members", Integer.valueOf(1), Integer.valueOf(list.size()));
		Assert.assertTrue("List doesn't contain the original query", list.contains(query));
	}
	
	@Test
	public void testCreatePharseQuery_queryShouldFusion() {
		Map<String, List<List<String>>> pairRelatedMap = ImmutableMap.of("2", Arrays.asList(Arrays.asList("san", "fransisco")));
		Query query = new Query("2", "san fransisco bridge");

		List<Query> list = classUnderTest.createPharseQuery(pairRelatedMap, query);
		Assert.assertEquals("List should have <1> members", Integer.valueOf(1), Integer.valueOf(list.size()));
		Query newQuery = list.get(0);
		Assert.assertEquals("List should contain query with key of original query", query.getId() ,newQuery.getId());
		Assert.assertFalse("List should't contain original query", list.contains(query));
	}
	
	@Test
	public void testCreatePharseQuery_queryShouldFusionTestContent() {
		Map<String, List<List<String>>> pairRelatedMap = ImmutableMap.of("2", Arrays.asList(Arrays.asList("san", "fransisco")));
		Query query = new Query("2", "san fransisco bridge");

		List<Query> list = classUnderTest.createPharseQuery(pairRelatedMap, query);
		Query newQuery = list.get(0);
		List<String> queryTerms = newQuery.getQueryTerms();
		Assert.assertTrue("new Query doesn't contain <bridge>", queryTerms.contains("bridge"));
		Assert.assertTrue("new Query doesn't contain <san fransisco>", queryTerms.contains("san fransisco"));
	}
	
	@Test
	public void testCreatePharseQuery_mixed() {
		Map<String, List<List<String>>> pairRelatedMap = ImmutableMap.of(
				"2", Arrays.asList(Arrays.asList("san", "fransisco"), Arrays.asList("los", "angeles")),
				"9", Arrays.asList(Arrays.asList("man", "united")));
		Query query = new Query("2", "san fransisco city of los angeles united");

		List<Query> list = classUnderTest.createPharseQuery(pairRelatedMap, query);
		Assert.assertEquals("List should have <2> members", Integer.valueOf(2), Integer.valueOf(list.size()));
		Assert.assertEquals("List should contain query with key of original query", query.getId(), list.get(0).getId());
		Assert.assertEquals("List should contain query with key of original query", query.getId(), list.get(1).getId());
		Assert.assertFalse("List should't contain original query", list.contains(query));
		List<String> queryOneTerms = list.get(0).getQueryTerms();
		Assert.assertTrue(queryOneTerms + " doesn't contain all terms" , queryOneTerms.contains("los") && 
				queryOneTerms.contains("angeles") &&
				queryOneTerms.contains("san fransisco") );
		Assert.assertFalse(queryOneTerms + " contains unwanted terms", queryOneTerms.contains("san") && 
				queryOneTerms.contains("fransisco") );
		
		List<String> queryTwoTerms = list.get(1).getQueryTerms();
		
		Assert.assertTrue(queryTwoTerms + " doesn't contain all terms" , queryTwoTerms.contains("los angeles") && 
				queryTwoTerms.contains("san") &&
				queryTwoTerms.contains("fransisco") );
		Assert.assertFalse(queryTwoTerms + " contains unwanted terms", queryTwoTerms.contains("los") && 
				queryTwoTerms.contains("angeles") );
		
	}
}

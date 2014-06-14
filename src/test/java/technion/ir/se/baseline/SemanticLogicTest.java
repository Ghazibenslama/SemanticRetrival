package technion.ir.se.baseline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.indri.SearchEngine;

@PrepareForTest(SemanticLogic.class)
public class SemanticLogicTest {

	private SemanticLogic classUnderTest;
	
	@Rule
	public PowerMockRule rule = new PowerMockRule();
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SemanticLogic();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertMaps() throws Exception {
		HashMap<String, int[]> testMap = new HashMap<String, int[]>();
		for (int i = 0; i < 30; i++) {
			int[] intArray = new int[30];
			for (int j = 0; j < 30; j++) {
				intArray[j] = i*j;
			}
			testMap.put("q"+String.valueOf(i), intArray);
		}
		
		Map<String, double[]> doubleMap = Whitebox.<Map<String, double[]>>invokeMethod(classUnderTest, "convertMaps", testMap);
		for (Entry<String, double[]> entry : doubleMap.entrySet()) {
			double[] doubleVector = entry.getValue();
			int[] intMap = testMap.get(entry.getKey());
			for (int i = 0; i < intMap.length; i++) {
				assertEquals(intMap[i], doubleVector[i], 0);
			}
		}
	}
	
	@Test
	public void testGetTermAlternatives() throws Exception {
		List<SemanticTermScore> termScores = new ArrayList<SemanticTermScore>();
		termScores.add(new SemanticTermScore("first", 0.88));
		termScores.add(new SemanticTermScore("second", 0.44));
		termScores.add(new SemanticTermScore("third", 0.33));
		List<String> list = Whitebox.<List<String>>invokeMethod(classUnderTest, "getTermAlternatives", termScores);
		assertEquals("list size not as expected", 1l, list.size());
		assertEquals("list doesn't contain expected element", "first", list.get(0));
	}
	
	@Test
	public void testRemoveTermsFromVectors() throws Exception {
		HashMap<String, int[]> map = new HashMap<String, int[]>();
		map.put("a", new int[] {1,2,3,4});
		map.put("b", new int[] {1,2,3,4});
		map.put("c", new int[] {1,2,3,4});
		map.put("d", new int[] {1,2,3,4});
		Whitebox.invokeMethod(classUnderTest, "removeTermsFromVectors", map, Arrays.asList("b","d"));
		
		assertTrue("map contains element that should have been removed", !map.containsKey("b"));
		assertTrue("map contains element that should have been removed", !map.containsKey("d"));
	}
	
	@Test
	public void testFindQueryAlternatives() throws Exception {
		Query originalQuery = new Query("00", "alef bet gimel");
		List<SemanticTermScore> alefList = new ArrayList<SemanticTermScore>();
		alefList.addAll(Arrays.asList(new SemanticTermScore("alef1", 0.9), new SemanticTermScore("alef2", 0.2) ));
		List<SemanticTermScore> bethList = new ArrayList<SemanticTermScore>();
		bethList.addAll(Arrays.asList(new SemanticTermScore("beth1", 0.8), new SemanticTermScore("beth2", 0.5) ));
		List<SemanticTermScore> gimelList = new ArrayList<SemanticTermScore>();
		gimelList.addAll(Arrays.asList(new SemanticTermScore("gimel2", 0.9), new SemanticTermScore("gimel1", 0.0) ));
		
		Map<String, int[]> map = null;
		SemanticLogic partialMock = PowerMock.createPartialMockAndInvokeDefaultConstructor(SemanticLogic.class, "findSimilarity");
		PowerMock.expectPrivate(partialMock, "findSimilarity", EasyMock.anyObject(Map.class), EasyMock.anyString()).
			andReturn(alefList).andReturn(bethList).andReturn(gimelList);
		PowerMock.replay(partialMock);
		
		
		List<Query> alternativeQueries = Whitebox.<List<Query>>invokeMethod(partialMock, "findQueryAlternatives", map, originalQuery);
		
		assertEquals("Number of created alternatives is not as expected", 3l, alternativeQueries.size());
		for (Query alternativeQuery : alternativeQueries) {
			assertEquals("new query id is not the same as original", originalQuery.getId(), alternativeQuery.getId());
		}
		Query q1 = alternativeQueries.get(0);
		assertTrue("query doesn't contain 'alef1", q1.getQueryTerms().contains("alef1"));
		assertTrue("query does contain 'alef2'", !q1.getQueryTerms().contains("alef2"));
		assertTrue("query doest contain 'alef'", !q1.getQueryTerms().contains("alef"));
		
		Query q2 = alternativeQueries.get(1);
		assertTrue("query doesn't contain 'beth1'", q2.getQueryTerms().contains("beth1"));
		assertTrue("query does contain 'beth2'", !q2.getQueryTerms().contains("beth2"));
		assertTrue("query doest contain 'beth'", !q2.getQueryTerms().contains("beth"));
		
		Query q3 = alternativeQueries.get(2);
		assertTrue("query doesn't contain 'gimel2'", q3.getQueryTerms().contains("gimel2"));
		assertTrue("query does contain 'gimel1'", !q3.getQueryTerms().contains("gimel1"));
		assertTrue("query doest contain 'gimel'", !q3.getQueryTerms().contains("gimel"));
	}
	
	@Test
	public void testCreateQueryAlternatives() throws Exception {
		
		Query originalQuery = new Query("01", "q a z");
		String queryTermToReplace = "z";
		List<String> alternativesTerms = new ArrayList<String>( Arrays.asList("z1", "z2", "z3") );
		List<Query> alternativeQueires = Whitebox.<List<Query>>invokeMethod(classUnderTest, "createQueryAlternatives", 
				originalQuery, queryTermToReplace, alternativesTerms);
		
		assertEquals("Number of created alternatives is not as expected", 3l, alternativeQueires.size());
		int i = 1;
		for (Query alternativeQueire : alternativeQueires) {
			List<String> queryTerms = alternativeQueire.getQueryTerms();
			assertTrue("alternativeQueire contains term that should have been replaced", !queryTerms.contains("z"));
			assertTrue("alternativeQueire not contains term that should be inserted", queryTerms.contains("z"+i));
			assertEquals("new query id is not the same as original", originalQuery.getId(), alternativeQueire.getId());
			i++;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSubmitAlternatives() throws Exception {
		List<RetrivalResult> q1List = createRetrivalResultList();
		List<RetrivalResult> q2List = createRetrivalResultList();
		
		SearchEngine engine = PowerMockito.mock(SearchEngine.class);
		PowerMockito.when(engine.runQuery(Mockito.anyInt(), Mockito.any(String[].class), Mockito.anyString()))
			.thenReturn(q1List, q2List);
		Whitebox.setInternalState(classUnderTest, "engine", engine);
		
		List<Query> alternativeQueries = Arrays.asList(new Query("001", "q1"), new Query("002", "q2"));
		List<List<ResultFormat>> list = Whitebox.<List<List<ResultFormat>>>invokeMethod(classUnderTest, "submitAlternatives", alternativeQueries);
		assertEquals("There should be two lists in result", 2l, list.size());
		List<ResultFormat> q1Results = list.get(0);
		assertEquals("number of results is not matched", q1List.size(), q1Results.size());
		for (ResultFormat resultFormat : q1Results) {
			assertEquals("queryId doesn't match", "001", resultFormat.getQueryID());
		}
		List<ResultFormat> q2Results = list.get(1);
		assertEquals("number of results is not matched", q2List.size(), q2Results.size());
		for (ResultFormat resultFormat : q2Results) {
			assertEquals("queryId doesn't match", "002", resultFormat.getQueryID());
		}
	}

	private List<RetrivalResult> createRetrivalResultList() {
		List<RetrivalResult> resultList = new ArrayList<RetrivalResult>();
		int numberOfresults = (int) Math.ceil(Math.random() * (-10));
		for (int i = 0; i < numberOfresults; i++) {
			double score = Math.random() * (-10); 
			RetrivalResult result = new RetrivalResult(score, i, 0, 0, 0, 0, 0, "doc_" + String.valueOf(i));
			resultList.add(result);
		}
		return resultList;
	}

}

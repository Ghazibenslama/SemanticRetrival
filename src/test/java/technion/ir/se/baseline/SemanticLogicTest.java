package technion.ir.se.baseline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.FileNameNotExtracted;
import technion.ir.se.indri.SearchEngine;

@PrepareForTest({SemanticLogic.class, SearchEngine.class})
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
		PowerMock.mockStatic(SearchEngine.class);
		EasyMock.expect(SearchEngine.getInstance()).andReturn(engine).anyTimes();
		PowerMock.replay(SearchEngine.class);
		
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

	@Test
	public void testMergeResults_sanity() throws Exception {
		List<List<ResultFormat>> alternativesResult = new ArrayList<List<ResultFormat>>();
		Query query = new Query("2342-2342", "wayne Ronny");
		for (int i = 0; i < 500; i++) {
			List<RetrivalResult> list = createRetrivalResultList();
			List<ResultFormat> resultFormatList = Utils.convertRetrivalResultListToResultFormatList(list, query);
			alternativesResult.add(resultFormatList);
		}
		List<ResultFormat> mergeResults = Whitebox.<List<ResultFormat>>invokeMethod(classUnderTest, "mergeResults", alternativesResult);
		assertTrue("merge results should contain less than 1000 results", mergeResults.size()<=1000);
	}
	
	@Test
	public void testMergeResults_eachResultIsDiffrent() throws Exception {
		List<List<ResultFormat>> simulateResultFormat = this.simulateResultFormat(700,3);
		List<ResultFormat> list = Whitebox.<List<ResultFormat>>invokeMethod(classUnderTest, "mergeResults", simulateResultFormat);
		assertEquals("There should be 1000 results", 1000l, list.size());
	}
	
	private List<List<ResultFormat>> simulateResultFormat(int numberOfResultForQuery, int numberOfQueries) {
		List<List<ResultFormat>> list = new ArrayList<List<ResultFormat>>(numberOfQueries);
		for (int j = 0; j < numberOfQueries; j++) {
			String queryId = "query-" + String.valueOf(j);
			double score = Math.random() * (-10);
			List<ResultFormat> resultFormats = new ArrayList<ResultFormat>(numberOfResultForQuery);
			for (int i = 0; i < numberOfResultForQuery; i++) {
				double currentScore = score - (0.1*i); 
				String documentID = "doc_" + String.valueOf(j) + "_" +String.valueOf(i);
				ResultFormat rf = new ResultFormat(queryId, documentID, i+1, currentScore);
				resultFormats.add(rf);
			}
			list.add(resultFormats);
		}
		return list;
	}

	private List<RetrivalResult> createRetrivalResultList() throws FileNameNotExtracted {
		List<RetrivalResult> resultList = new ArrayList<RetrivalResult>();
		int numberOfresults = (int) Math.ceil(Math.random() * (10));
		for (int i = 0; i < numberOfresults; i++) {
			double score = Math.random() * (-10); 
			RetrivalResult result = new RetrivalResult(score, i, 0, 0, 0, 0, 0, "doc_" + String.valueOf(i));
			resultList.add(result);
		}
		return resultList;
	}

}

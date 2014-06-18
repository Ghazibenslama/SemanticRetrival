package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Document;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.TextWindow;
import technion.ir.se.indri.SearchEngine;
import technion.ir.se.windows.AbstractStrategy;

@PrepareForTest(Utils.class)
@PowerMockIgnore({"lemurproject.*"})
public class SimilarityVectorsTest {
	private static final String DOC_ONE_CONTENT = "some window without query terms";
	private static final String DOC_TWO_CONTENT = "adir is amzing!";
	private static final String DOC_THREE_CONTENT = "I saw show last night in bordway";
	private static final String DOC_FOUR_CONTENT = "some window some query some king some burger some cloud ate burger";
	private SimilarityVectors classUnderTest;
	private SearchEngine searchEngine;
	
	@Rule
	public PowerMockRule rule = new PowerMockRule();
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SimilarityVectors();
		searchEngine = SearchEngine.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildTermVector() throws Exception {
		searchEngine = PowerMockito.mock(SearchEngine.class);
		ArrayList<Document> documentsList = new ArrayList<Document>();
		documentsList.add(new Document(Arrays.asList("aba", "papa")));
		documentsList.add(new Document(Arrays.asList("aba", "padre")));
		PowerMockito.when( searchEngine.getDocumentsContet(Mockito.anyListOf(Integer.class)) ).thenReturn(documentsList);
		Whitebox.setInternalState(classUnderTest, "serchEngine", searchEngine);

		classUnderTest.buildRowTermVector(new ArrayList<RetrivalResult>());
		List<String> buildRowTermVector = Whitebox.getInternalState(classUnderTest, "rowTermVector");
		Assert.assertTrue("vector doesn't contain all terms", 
				buildRowTermVector.containsAll(Arrays.asList("papa","aba","padre")));
		Assert.assertEquals("1st element is not as expected", "aba", buildRowTermVector.get(0));
		Assert.assertEquals("2nd element is not as expected", "padre", buildRowTermVector.get(1));
		Assert.assertEquals("3'd element is not as expected", "papa", buildRowTermVector.get(2));
	}

	@Test
	public void testBuildQueryVectors_BetweenQueryTermsStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("BetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, int[]> queryVectors = classUnderTest.buildVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		int[] someTrueVector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, queryVectors.get("some"));

		int[] adirTrueVector = new int[]{0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, queryVectors.get("adir"));
		
		int[] nightTrueVector = new int[]{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'night' is not as expected", nightTrueVector, queryVectors.get("night"));
		
		int[] showTrueVector = new int[]{1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, queryVectors.get("show"));
	}
	
	@Test
	public void testBuildQueryVectors_HalfDistanceBetweenQueryTermsStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("HalfDistanceBetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, int[]> queryVectors = classUnderTest.buildVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		int[] someTrueVector =  new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, queryVectors.get("some"));

		int[] adirTrueVector =  new int[]{0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, queryVectors.get("adir"));
		
		int[] showTrueVector =  new int[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, queryVectors.get("show"));
		
		int[] nightTrueVector = new int[]{0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'night' is not as expected", nightTrueVector, queryVectors.get("night"));
		
	}

	@Test
	public void testBuildQueryVectors_FixedWindowStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty("window.strategy")).andReturn("FixedWindowStrategy");
		EasyMock.expect(Utils.readProperty("window.size")).andReturn("7");
		
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, int[]> queryVectors = classUnderTest.buildVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		int[] someTrueVector  = new int[]{0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, queryVectors.get("some"));

		int[] adirTrueVector  = new int[]{0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, queryVectors.get("adir"));
		
		int[] showTrueVector  = new int[]{1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, queryVectors.get("show"));
		
		int[] nightTrueVector = new int[]{1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'night' is not as expected", nightTrueVector, queryVectors.get("night"));
	}
	
	@Test
	public void testBuildFeedbackTermsVector() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("HalfDistanceBetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");

		Map<String, int[]> map = classUnderTest.buildVectors(query);
		Assert.assertTrue(!map.isEmpty());
		
		int[] withoutTrueVector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0};
		Assert.assertArrayEquals("vector of 'without' is not as expected", withoutTrueVector, map.get("without"));

		int[] windowTrueVector  = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1};
		Assert.assertArrayEquals("vector of 'window' is not as expected", windowTrueVector, map.get("window"));
		
	}
	
	private void initDocumentsField(){
		Document documentOne = new Document(Arrays.asList(DOC_ONE_CONTENT.split(" ")));
		Document documentTwo = new Document(Arrays.asList(DOC_TWO_CONTENT.split(" ")));
		Document documentThree = new Document(Arrays.asList(DOC_THREE_CONTENT.split(" ")));
		Whitebox.setInternalState(classUnderTest, "documents", Arrays.asList(documentOne, documentTwo, documentThree));
	}
	
	private void initDocumentsField(String... args){
		List<Document> documentList = new ArrayList<Document>();
		for (int i = 0; i < args.length; i++) {
			String document = args[i];
			documentList.add( new Document(Arrays.asList(document.split(" "))) );
		}
		Whitebox.setInternalState(classUnderTest, "documents", documentList);
	}
	
	private void initRowTermVectorField() {
		initRowTermVectorField(DOC_ONE_CONTENT, DOC_TWO_CONTENT, DOC_THREE_CONTENT);
	}
	
	private void initRowTermVectorField(String... args) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i]);
			builder.append(" ");
		}
		List<String> rowTermsVector = Arrays.asList(builder.toString().split(" "));
		List<String> uniqueValues = Utils.getUniqueValues(rowTermsVector);
		Collections.sort(uniqueValues);
		Whitebox.setInternalState(classUnderTest, "rowTermVector", uniqueValues );
	}
	
	private void prepareForBuildQueryVectorsTest() {
		initDocumentsField();
		initRowTermVectorField();
	}

	@Test
	public void testCreateTermsMap() throws Exception {
		List<String> terms = Arrays.asList("russia", "adir", "putin", "adir");
		String[] rowTermVector = "a b c d e f r g".split(" ");
		Whitebox.setInternalState(classUnderTest, "rowTermVector", Arrays.asList(rowTermVector));
		Map<String,Map<String,Short>> map = Whitebox.<HashMap<String, Map<String, Short>>>invokeMethod(classUnderTest, "createTermsMap", terms);
		Assert.assertEquals("Didn't create map for all query terms", 3l, map.size());
	
		for (Entry<String, Map<String, Short>> entry : map.entrySet()) {
			Assert.assertTrue("Map of each term should be empty", entry.getValue().isEmpty());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPopulateFeedbackVectors_testNumberOfCreatedVectors() throws Exception {
		initRowTermVectorField();
		initDocumentsField();
		Feedback feedback = Whitebox.<Feedback>invokeMethod(classUnderTest, "getFeedback");
		
		List<String> listNoQuery = Arrays.asList(DOC_ONE_CONTENT.split(" "));
		List<String> listWithAdir = Arrays.asList(DOC_TWO_CONTENT.split(" "));
		List<String> listWithShow = Arrays.asList(DOC_THREE_CONTENT.split(" "));
		
		AbstractStrategy strategy = PowerMockito.mock(AbstractStrategy.class);
		PowerMockito.when(strategy.getTermsInWindow(Mockito.any(TextWindow.class)))
			.thenReturn(listNoQuery,listWithAdir,listWithShow);
		
		List<TextWindow> windows = Arrays.asList(new TextWindow(0, 0), new TextWindow(0, 0), new TextWindow(0, 0));

		Map<String, int[]> map = Whitebox.<Map<String, int[]>>invokeMethod(classUnderTest, "populateFeedbackVectors", feedback.getTerms(), strategy, windows);
		
		HashSet<String> set = new HashSet<String>();
		set.addAll(listNoQuery);
		set.addAll(listWithAdir);
		set.addAll(listWithShow);
		Assert.assertEquals("map of feedback terms vector doesn't contain vector for each term", set.size(), map.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPopulateFeedbackVectors_testContentOfCreatedVectors() throws Exception {
		initRowTermVectorField(DOC_FOUR_CONTENT);
		initDocumentsField(DOC_FOUR_CONTENT);
		Feedback feedback = Whitebox.<Feedback>invokeMethod(classUnderTest, "getFeedback");
		
		
		List<String> docFourList = Arrays.asList(DOC_FOUR_CONTENT.split(" "));
		
		AbstractStrategy strategy = PowerMockito.mock(AbstractStrategy.class);
		
		PowerMockito.when(strategy.getTermsInWindow(Mockito.any(TextWindow.class)))
			.thenReturn(docFourList.subList(0, 3), docFourList.subList(3, 5), 
					docFourList.subList(5, 8), docFourList.subList(8, 12));
		
		//creating a list with the same number of elements as in the strategy mocked return statement
		List<TextWindow> windows = Arrays.asList(new TextWindow(0, 0), new TextWindow(0, 0), new TextWindow(0, 0), new TextWindow(0, 0));

		Map<String, int[]> map = Whitebox.<Map<String, int[]>>invokeMethod(classUnderTest, "populateFeedbackVectors", feedback.getTerms(), strategy, windows);
		
		//order is: ate, burger, cloud, king, query, some, window
		int[] kingTrueVector  = new int[]{0, 1, 0, 0, 0, 1, 0};
		Assert.assertArrayEquals("vector of 'king' is not as expected", kingTrueVector, map.get("king"));
		
		int[] queryTrueVector  = new int[]{0, 0, 0, 0, 0, 1, 0};
		Assert.assertArrayEquals("vector of 'query' is not as expected", queryTrueVector, map.get("query"));
		
		int[] someTrueVector  = new int[]{1, 2, 1, 1, 1, 0, 1};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, map.get("some"));
		
		int[] burgerTrueVector  = new int[]{1, 0, 1, 1, 0, 2, 0};
		Assert.assertArrayEquals("vector of 'burger' is not as expected", burgerTrueVector, map.get("burger"));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPopulateVectorsWithTerms() throws Exception {
		initRowTermVectorField();
		
		List<String> listNoQuery = Arrays.asList(DOC_ONE_CONTENT.split(" "));
		List<String> listWithAdir = Arrays.asList(DOC_TWO_CONTENT.split(" "));
		List<String> listWithShow = Arrays.asList(DOC_THREE_CONTENT.split(" "));
		
		AbstractStrategy strategy = PowerMockito.mock(AbstractStrategy.class);
		int windowSize = 4;
		PowerMockito.when(strategy.getTermsInWindow(Mockito.any(TextWindow.class)))
			.thenReturn(listNoQuery,listWithAdir,listWithShow.subList(0, windowSize));
		
		Query query = new Query(String.valueOf(0), "adir show miller");
		
		List<TextWindow> windows = Arrays.asList(new TextWindow(0, 0), new TextWindow(0, 0), new TextWindow(0, 0));
		Map<String, int[]> map = Whitebox.<Map<String, int[]>>invokeMethod(classUnderTest, "populateQueryVectors", query, strategy, windows);
		
		int[] vectorOfMiller = map.get("miller");
		for (int i = 0; i < vectorOfMiller.length; i++) {
			Assert.assertEquals("There should be now value", 0, vectorOfMiller[i]);
		}
		
		int[] adirTrueVector = new int[]{0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, map.get("adir"));

		int[] showTrueVector = new int[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, map.get("show"));
	}
	
	@Test
	public void testGetOnlyFeedbackTerms() throws Exception {
		Query query = new Query(String.valueOf(0), "adir show miller");
		initDocumentsField("some string that contain one query term: adir");
		Feedback feedback = Whitebox.<Feedback>invokeMethod(classUnderTest, "getFeedback");
		
		List<String> noQueryTerms = Whitebox.<List<String>>invokeMethod(classUnderTest, "getOnlyFeedbackTerms", feedback, query);
		Assert.assertTrue("list should not contain query term", !noQueryTerms.contains("adir"));
		Assert.assertTrue("Original list should not be affected", feedback.getTerms().contains("adir"));
	}
	
	@Test
	public void testUpdateTermFrequency_newTerm() throws Exception{
		HashMap<String, Map<String, Short>> outerMap = new HashMap<String, Map<String, Short>>();
		HashMap<String, Short> innerMap = new HashMap<String, Short>();
		String outerKey = "qTerm1";
		outerMap.put(outerKey, innerMap);
		String innerKey = "term";
		Whitebox.invokeMethod(classUnderTest, "updateTermFrequency", outerMap, outerKey, innerKey);
		Short actual = outerMap.get(outerKey).get(innerKey);
		Assert.assertEquals("Frequency of a new term is not set to 1", Short.valueOf((short) 1), actual);
	}
	
	@Test
	public void testUpdateTermFrequency_newExisitng() throws Exception{
		HashMap<String, Map<String, Short>> outerMap = new HashMap<String, Map<String, Short>>();
		HashMap<String, Short> innerMap = new HashMap<String, Short>();
		String outerKey = "qTerm1";
		outerMap.put(outerKey, innerMap);
		String innerKey = "term";
		innerMap.put(innerKey, (short) 5);
		Whitebox.invokeMethod(classUnderTest, "updateTermFrequency", outerMap, outerKey, innerKey);
		Short actual = outerMap.get(outerKey).get(innerKey);
		Assert.assertEquals("Frequency of a new term is not set to 1", Short.valueOf((short) 6), actual);
	}
	
}

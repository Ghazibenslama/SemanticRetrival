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
		
		Map<String, Map<String, Short>> queryVectors = classUnderTest.buildVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		Map<String, Short> mapOfSome = queryVectors.get("some");
		Assert.assertEquals("size of map of 'some' is not as expected", 1, mapOfSome.size());
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("some"));

		Map<String, Short> mapOfAdir = queryVectors.get("adir");
		Assert.assertEquals("size of map of 'adir' is not as expected", 5, mapOfAdir.size());
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("query"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("window"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("without"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("adir"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("terms"));

		Map<String, Short> mapOfNight = queryVectors.get("night");
		Assert.assertEquals("size of map of 'night' is not as expected", 2, mapOfNight.size());
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("last"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("night"));
		
		Map<String, Short> mapOfShow = queryVectors.get("show");
		Assert.assertEquals("size of map of 'show' is not as expected", 5, mapOfShow.size());
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("I"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("saw"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("show"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("is"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("amzing!"));
	}
	
	@Test
	public void testBuildQueryVectors_HalfDistanceBetweenQueryTermsStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("HalfDistanceBetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, Map<String, Short>> queryVectors = classUnderTest.buildVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		Map<String, Short> mapOfSome = queryVectors.get("some");
		Assert.assertEquals("size of map of 'some' is not as expected", 3, mapOfSome.size());
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("window"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("without"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("some"));

		Map<String, Short> mapOfAdir = queryVectors.get("adir");
		Assert.assertEquals("size of map of 'adir' is not as expected", 5, mapOfAdir.size());
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("is"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("query"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("terms"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("adir"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("amzing!"));

		Map<String, Short> mapOfNight = queryVectors.get("night");
		Assert.assertEquals("size of map of 'night' is not as expected", 3, mapOfNight.size());
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("night"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("last"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("in"));

		Map<String, Short> mapOfShow = queryVectors.get("show");
		Assert.assertEquals("size of map of 'show' is not as expected", 4, mapOfShow.size());
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("I"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("saw"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("show"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("last"));
	}

	@Test
	public void testBuildQueryVectors_FixedWindowStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty("window.strategy")).andReturn("FixedWindowStrategy");
		EasyMock.expect(Utils.readProperty("window.size")).andReturn("7");
		
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, Map<String, Short>> queryVectors = classUnderTest.buildVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		Map<String, Short> mapOfSome = queryVectors.get("some");
		Assert.assertEquals("size of map of 'some' is not as expected", 7, mapOfSome.size());
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("is"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("window"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("query"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("terms"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("without"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("some"));
		Assert.assertEquals("map of 'some' is not as expected", Short.valueOf( (short)1 ), mapOfSome.get("adir"));

		Map<String, Short> mapOfAdir = queryVectors.get("adir");
		Assert.assertEquals("size of map of 'adir' is not as expected", 7, mapOfAdir.size());
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("is"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("window"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("query"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("terms"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("without"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("some"));
		Assert.assertEquals("map of 'adir' is not as expected", Short.valueOf( (short)1 ), mapOfAdir.get("adir"));

		Map<String, Short> mapOfNight = queryVectors.get("night");
		Assert.assertEquals("size of map of 'night' is not as expected", 7, mapOfNight.size());
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("night"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("amzing!"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("show"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("last"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("saw"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("I"));
		Assert.assertEquals("map of 'night' is not as expected", Short.valueOf( (short)1 ), mapOfNight.get("in"));
		

		Map<String, Short> mapOfShow = queryVectors.get("show");
		Assert.assertEquals("size of map of 'show' is not as expected", 7, mapOfShow.size());
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("night"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("amzing!"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("show"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("last"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("saw"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("I"));
		Assert.assertEquals("map of 'show' is not as expected", Short.valueOf( (short)1 ), mapOfShow.get("in"));
	}
	
	@Test
	public void testBuildFeedbackTermsVector() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStaticPartial(Utils.class, "readProperty");
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("HalfDistanceBetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");

		Map<String, Map<String, Short>> map = classUnderTest.buildVectors(query);
		Assert.assertTrue(!map.isEmpty());
		
		Map<String, Short> mapOfWithout = map.get("without");
		Assert.assertEquals("size of map of 'without' is not as expected", 2, mapOfWithout.size());
		Assert.assertEquals("map of 'without' is not as expected", Short.valueOf( (short)1 ), mapOfWithout.get("some"));
		Assert.assertEquals("map of 'without' is not as expected", Short.valueOf( (short)1 ), mapOfWithout.get("window"));
		
		Map<String, Short> mapOfWindow = map.get("window");
		Assert.assertEquals("size of map of 'window' is not as expected", 2, mapOfWindow.size());
		Assert.assertEquals("map of 'window' is not as expected", Short.valueOf( (short)1 ), mapOfWindow.get("some"));
		Assert.assertEquals("map of 'window' is not as expected", Short.valueOf( (short)1 ), mapOfWindow.get("without"));
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

		Map<String, Map<String, Short>> map = Whitebox.<Map<String, Map<String, Short>>>invokeMethod(classUnderTest, "populateFeedbackVectors", feedback.getTerms(), strategy, windows);
		
		//order is: ate, burger, cloud, king, query, some, window
		Map<String, Short> mapOfKing = map.get("king");
		Assert.assertEquals("Map should contain 2 elements", (short)2, mapOfKing.size());
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfKing.get("burger"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfKing.get("some"));
		
		Map<String, Short> mapOfQuery = map.get("query");
		Assert.assertEquals("Map should contain 1 elements", (short)1, mapOfQuery.size());
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfQuery.get("some"));
		
		Map<String, Short> mapOfSome = map.get("some");
		Assert.assertEquals("Map should contain 6 elements", (short)6, mapOfSome.size());
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 2), mapOfSome.get("burger"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfSome.get("window"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfSome.get("query"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfSome.get("ate"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfSome.get("cloud"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfSome.get("king"));

		Map<String, Short> mapOfBurger = map.get("burger");
		Assert.assertEquals("Map should contain 4 elements", (short)4, mapOfBurger.size());
		Assert.assertEquals("frequency should be 2", Short.valueOf((short) 2), mapOfBurger.get("some"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfBurger.get("king"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfBurger.get("cloud"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfBurger.get("ate"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPopulateQueryVectors() throws Exception {
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
		Map<String, Map<String, Short>> map = Whitebox.<Map<String, Map<String, Short>>>invokeMethod(classUnderTest, "populateQueryVectors", query, strategy, windows);
		
		Map<String, Short> vectorOfMiller = map.get("miller");
		Assert.assertTrue("There map should be empty", vectorOfMiller.isEmpty());
		
		Map<String, Short> mapOfAdir = map.get("adir");
		Assert.assertEquals("Map should contain 3 elements", (short)3, mapOfAdir.size());
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfAdir.get("adir"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfAdir.get("is"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfAdir.get("amzing!"));
		
		Map<String, Short> mapOfShow = map.get("show");
		Assert.assertEquals("Map should contain 4 elements", (short)4, mapOfShow.size());
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfShow.get("I"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfShow.get("saw"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfShow.get("show"));
		Assert.assertEquals("frequency should be 1", Short.valueOf((short) 1), mapOfShow.get("last"));

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
	public void testUpdateTermFrequency_forQueryTerm_newTerm() throws Exception{
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
	public void testUpdateTermFrequency_forQueryTerm_newExisitng() throws Exception{
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
	
	@Test
	public void testUpdateTermFrequency_forTextTerm_newTerm() throws Exception{
		HashMap<String, Map<String, Short>> outerMap = new HashMap<String, Map<String, Short>>();
		HashMap<String, Short> innerMap = new HashMap<String, Short>();
		String outerKey = "qTerm1";
		outerMap.put(outerKey, innerMap);
		String innerKey = "term";
		Whitebox.invokeMethod(classUnderTest, "updateTermFrequency", outerMap, outerKey, innerKey, (short)4);
		Short actual = outerMap.get(outerKey).get(innerKey);
		Assert.assertEquals("Frequency of a new term is not set to 1", Short.valueOf((short) 4), actual);
	}
	
	@Test
	public void testUpdateTermFrequency_forTextTerm_newExisitng() throws Exception{
		HashMap<String, Map<String, Short>> outerMap = new HashMap<String, Map<String, Short>>();
		HashMap<String, Short> innerMap = new HashMap<String, Short>();
		String outerKey = "qTerm1";
		outerMap.put(outerKey, innerMap);
		String innerKey = "term";
		innerMap.put(innerKey, (short) 5);
		Whitebox.invokeMethod(classUnderTest, "updateTermFrequency", outerMap, outerKey, innerKey, (short)4);
		Short actual = outerMap.get(outerKey).get(innerKey);
		Assert.assertEquals("Frequency of a new term is not set to 1", Short.valueOf((short) 9), actual);
	}
	
}

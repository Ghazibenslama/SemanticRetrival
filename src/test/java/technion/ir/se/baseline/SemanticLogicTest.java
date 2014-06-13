package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
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
import technion.ir.se.dao.Document;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.TextWidow;
import technion.ir.se.indri.SearchEngine;

@PrepareForTest(Utils.class)
public class SemanticLogicTest {
	private SemanticLogic classUnderTest;
	private SearchEngine searchEngine;
	
	@Rule
	public PowerMockRule rule = new PowerMockRule();
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SemanticLogic();
		searchEngine = new SearchEngine();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildTermVector() throws Exception {
		String[] rules= new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		List<RetrivalResult> retrivalResult = searchEngine.runQuery(2, rules, "international organized crime");
		searchEngine = PowerMockito.mock(SearchEngine.class);
		ArrayList<Document> documentsList = new ArrayList<Document>();
		documentsList.add(new Document(Arrays.asList("aba", "papa")));
		documentsList.add(new Document(Arrays.asList("aba", "padre")));
		PowerMockito.when( searchEngine.getDocumentsContet(Mockito.anyListOf(Integer.class)) ).thenReturn(documentsList);
		Whitebox.setInternalState(classUnderTest, "serchEngine", searchEngine);

		classUnderTest.buildRowTermVector(retrivalResult);
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
		
		PowerMock.mockStatic(Utils.class);
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("BetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, int[]> queryVectors = classUnderTest.buildQueryVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		int[] someTrueVector = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, queryVectors.get("some"));

		int[] adirTrueVector = new int[]{0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, queryVectors.get("adir"));
		
		int[] nightTrueVector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0};
		Assert.assertArrayEquals("vector of 'night' is not as expected", nightTrueVector, queryVectors.get("night"));
		
		int[] showTrueVector = new int[]{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, queryVectors.get("show"));
	}
	
	@Test
	public void testBuildQueryVectors_HalfDistanceBetweenQueryTermsStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStatic(Utils.class);
		EasyMock.expect(Utils.readProperty(EasyMock.anyString())).andReturn("HalfDistanceBetweenQueryTermsStrategy");
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, int[]> queryVectors = classUnderTest.buildQueryVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		int[] someTrueVector =  new int[]{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, queryVectors.get("some"));

		int[] adirTrueVector =  new int[]{0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, queryVectors.get("adir"));
		
		int[] showTrueVector =  new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, queryVectors.get("show"));
		
		int[] nightTrueVector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0};
		Assert.assertArrayEquals("vector of 'night' is not as expected", nightTrueVector, queryVectors.get("night"));
		
	}

	@Test
	public void testBuildQueryVectors_FixedWindowStrategy() {
		prepareForBuildQueryVectorsTest();
		
		PowerMock.mockStatic(Utils.class);
		EasyMock.expect(Utils.readProperty("window.strategy")).andReturn("FixedWindowStrategy");
		EasyMock.expect(Utils.readProperty("window.size")).andReturn("7");
		
		PowerMock.replay(Utils.class);
		
		Query query = new Query(String.valueOf(0), "some adir night show");
		
		Map<String, int[]> queryVectors = classUnderTest.buildQueryVectors(query);
		Assert.assertTrue("no vectors were created", !queryVectors.isEmpty());
		
		int[] someTrueVector  = new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'some' is not as expected", someTrueVector, queryVectors.get("some"));

		int[] adirTrueVector  = new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
		Assert.assertArrayEquals("vector of 'adir' is not as expected", adirTrueVector, queryVectors.get("adir"));
		
		int[] showTrueVector  = new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0};
		Assert.assertArrayEquals("vector of 'show' is not as expected", showTrueVector, queryVectors.get("show"));
		
		int[] nightTrueVector = new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0};
		Assert.assertArrayEquals("vector of 'night' is not as expected", nightTrueVector, queryVectors.get("night"));
	}

	private void prepareForBuildQueryVectorsTest() {
		String docOneContent = "some window without query terms";
		String docTwoContent = "adir is amzing!";
		String docThreeContent = "I saw show last night in bordway";
		Document documentOne = new Document(Arrays.asList(docOneContent.split(" ")));
		Document documentTwo = new Document(Arrays.asList(docTwoContent.split(" ")));
		Document documentThree = new Document(Arrays.asList(docThreeContent.split(" ")));
		Whitebox.setInternalState(classUnderTest, "documents", Arrays.asList(documentOne, documentTwo, documentThree));
		
		List<String> rowTermVector = Arrays.asList((docOneContent + " " + docTwoContent +  " " + docThreeContent).split(" "));
		Whitebox.setInternalState(classUnderTest, "rowTermVector", rowTermVector);
	}
	
	@Test
	public void testCreateQueryTermsList() throws Exception {
		List<String> quertTerms = Arrays.asList("russia", "adir", "putin");
		String[] rowTermVector = "a b c d e f r g".split(" ");
		Whitebox.setInternalState(classUnderTest, "rowTermVector", Arrays.asList(rowTermVector));
		Map<String, int[]> map = Whitebox.<Map<String, int[]>>invokeMethod(classUnderTest, "createQueryTermsMap", quertTerms);
		Assert.assertEquals("didn't create array for all query terms", 3l, map.size());
	
		for (Map.Entry<String, int[]> entry : map.entrySet()) {
			Assert.assertEquals("vector size is not as expected", rowTermVector.length, entry.getValue().length);
			for (int i = 0; i < entry.getValue().length; i++) {
				int content = entry.getValue()[i];
				Assert.assertEquals("content sould be 0", 0, content);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPopulateVectorsWithTerms() throws Exception {
		String doc1 = "some window without query terms";
		String doc2 = "adir is amzing!";
		String doc3 = "I saw show last night in bordway";
		List<String> rowTermVector = Arrays.asList((doc1 + " " + doc2 +  " " + doc3).split(" "));
		Whitebox.setInternalState(classUnderTest, "rowTermVector", rowTermVector);
		
		List<String> listNoQuery = Arrays.asList(doc1.split(" "));
		List<String> listWithAdir = Arrays.asList(doc2.split(" "));
		List<String> listWithShow = Arrays.asList(doc3.split(" "));
		
		Query query = new Query(String.valueOf(0), "adir show miller");
		AbstractStrategy strategy = PowerMockito.mock(AbstractStrategy.class);
		int windowSize = 4;
		PowerMockito.when(strategy.getTermsInWindow(Mockito.any(TextWidow.class)))
			.thenReturn(listNoQuery,listWithAdir,listWithShow.subList(0, windowSize));
		
		List<TextWidow> windows = Arrays.asList(new TextWidow(0, 0), new TextWidow(0, 0), new TextWidow(0, 0));
		Map<String, int[]> map = Whitebox.<Map<String, int[]>>invokeMethod(classUnderTest, "populateVectorsWithTerms", query, strategy, windows);
		
		int[] vectorOfMiller = map.get("miller");
		for (int i = 0; i < vectorOfMiller.length; i++) {
			Assert.assertEquals("There should be now value", 0, vectorOfMiller[i]);
		}
		
		int[] vectorOfAdir = map.get("adir");
		for (int i = 5 ; i <= 7; i++) {
			Assert.assertEquals("There should be 1 value", 1, vectorOfAdir[i]);
			
		}
		
		int[] vectorOfShow = map.get("show");
		int window3FirstWord = 8;
		int windows3LastWord = window3FirstWord + windowSize;
		for (int i = window3FirstWord ; i < windows3LastWord; i++) {
			Assert.assertEquals("There should be 1 value", 1, vectorOfShow[i]);
			
		}
		
	}
}

package technion.ir.se.baseline;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Document;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogicTest {
	private SemanticLogic classUnderTest;
	private SearchEngine searchEngine;
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SemanticLogic();
		searchEngine = new SearchEngine();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void retrieveQueryTermsTest() throws Exception {
		Query query = new Query("1", "Hello World");
		List<String> list = Whitebox.<List<String>>invokeMethod(classUnderTest, "retrieveQueryTerms", query);//WhiteBox because retrieveQueryTerms is Private func
		assertTrue("Doesn't contain 2 variables", list.size() == 2);
		assertTrue("Doesn't contain Hello variable", list.contains("Hello"));
		assertTrue("Doesn't contain World variable", list.contains("World"));
	}
	
	@Test
	public void testBuildTermVector() throws Exception
	{
		String[] rules= new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		List<RetrivalResult> retrivalResult = searchEngine.runQuery(2, rules, "international organized crime");
		searchEngine = PowerMockito.mock(SearchEngine.class);
		ArrayList<Document> documentsList = new ArrayList<Document>();
		documentsList.add(new Document(Arrays.asList("aba", "papa")));
		documentsList.add(new Document(Arrays.asList("aba", "padre")));
		PowerMockito.when( searchEngine.getDocumentsContet(Mockito.anyListOf(Integer.class)) ).thenReturn(documentsList);
		Whitebox.setInternalState(classUnderTest, "serchEngine", searchEngine);

		SortedSet<String> buildRowTermVector = classUnderTest.buildRowTermVector(retrivalResult);
		Assert.assertTrue("vector doesn't contain all terms", 
				buildRowTermVector.containsAll(Arrays.asList("papa","aba","padre")));
	}

}

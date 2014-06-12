package technion.ir.se.baseline;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogicTest {
	private SemanticLogic classUnderTest;
	private SearchEngine runQuery;
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SemanticLogic();
		runQuery = new SearchEngine();
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
	public void buildRowTermVectorTest()
	{
		String[] rules= new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		try {
			List<RetrivalResult> retrivalResult = runQuery.runQuery(2, rules, "international organized crime");
			SortedSet<String> buildRowTermVector = classUnderTest.buildRowTermVector(retrivalResult);
			String first = buildRowTermVector.first();
			
			
		} catch (Exception e) {
			System.out.println("runQuery failed");
			e.printStackTrace();
		}
		
		
		/*
		Query query = new Query("1", "Hello World");
		SortedSet<String> resultSet = classUnderTest.buildTermVector(query,null);
		assertTrue("Doesn't contain 2 variables", resultSet.size() == 2);
		assertTrue("Doesn't contain Hello variable", resultSet.contains("Hello"));
		assertTrue("Doesn't contain World variable", resultSet.contains("World"));
		*/
	}

}

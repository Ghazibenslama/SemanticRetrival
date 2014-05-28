package technion.ir.se.indri;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.dao.RetrivalResult;

public class RunQueryTest {
	private SearchEngine runQuery;

	@Before
	public void setUp() throws Exception {
		runQuery = new SearchEngine();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunQuery() throws Exception {
		String[] rules= new String[]{ "method:linear","collectionLambda:0.2","field:title" };
		List<RetrivalResult> retrivedDocumentId = runQuery.runQuery(1, rules, "cocaine police");
//		assertTrue("Didn't retrive document", retrivedDocumentId == 2);
		
	}
	
	@Test
	public void testRunQuery_TFIDF() throws Exception {
		String[] rules= new String[]{ "tfidf", "k1:1.0", "b:0.3" };
		List<RetrivalResult> retrivedDocumentId = runQuery.runQuery(1, rules, "Frank aircraft");
//		assertTrue("Didn't retrive document", retrivedDocumentId == 2);
	}
	
	@Test
	public void testRunQuery_Combine() throws Exception {
		String[] rules= new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		List<RetrivalResult> retrivedDocumentIds = runQuery.runQuery(1000, rules, "international organized crime");
		List<RetrivalResult> retrivedDocumentIdIndriForma = runQuery.runQuery(1000, rules, "#combine(international organized crime)");
		for (int i = 0; i < retrivedDocumentIds.size(); i++) {
			Assert.assertEquals(retrivedDocumentIds.get(i).getDocumentId(), retrivedDocumentIdIndriForma.get(i).getDocumentId());
		}
	}

}

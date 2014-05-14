package technion.ir.se.indri;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunQueryTest {
	private RunQuery runQuery;

	@Before
	public void setUp() throws Exception {
		runQuery = new RunQuery();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunQuery() throws Exception {
		String[] rules= new String[]{ "method:linear","collectionLambda:0.2","field:title" };
		int numberOfDocsRetrived = runQuery.runQuery(1, rules, "cocaine police", "TEXT");
		assertTrue("Didn't retrive document", numberOfDocsRetrived == 1);
		
	}

}

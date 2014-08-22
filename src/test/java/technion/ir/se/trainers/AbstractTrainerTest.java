package technion.ir.se.trainers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Query;

public class AbstractTrainerTest {
	AbstractParamTrainer classUnderTest;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunQuery() {
		Query query= new Query("Q_ID", "Query Content");
		String[] rules = new String[]{ "method:dir", "mu:1000", "fbDocs:25", "fbTerms:50", "fbOrigWeight:0.3", "fbMu:0"};
//		Whitebox.invokeMethod(AbstractTrainer.class, "runQuery", arguments)
	}

}

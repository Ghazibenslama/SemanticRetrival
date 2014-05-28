package technion.ir.se.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RetrivalResultTest {
	RetrivalResult classUnderTest;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDocumentId() {
		classUnderTest = new RetrivalResult(0, 0, 0, 0, 0, 0, 0, "docs/FBIS/.\\FBIS4-41991.txt");
		Assert.assertEquals("Didn't parse documentId correctly", "FBIS4-41991", classUnderTest.getDocumentId());
	}

}

package technion.ir.se.baseline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.TestUtils;

public class BaseLineTest {
	BaseLine classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new BaseLine();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testcreateBaseLine() throws IOException, URISyntaxException {
		classUnderTest.createBaseLine();
		List<String> fileLines = TestUtils.getFileLines("baseLineMap.res");
		
		Assert.assertTrue("File doesn't contain exactly 2000 results", fileLines.size() == 2000);
		Assert.assertTrue("results doesn't contain: 401 Q0 FBIS4-18602 946 -7.0698 Indri", fileLines.contains("401 Q0 FBIS4-18602 946 -7.0698 Indri"));
		TestUtils.deleteFile("baseLineMap.res");
	}

}

package technion.ir.se.baseline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import technion.ir.se.TestUtils;

public class BaseLineTest {
	BaseLine classUnderTest;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void testcreateBaseLine() throws IOException, URISyntaxException {
		classUnderTest = new BaseLine();
		classUnderTest.createBaseLine();
		List<String> fileLines = TestUtils.getFileLines("baseLineMap.res");
		
		Assert.assertTrue("File doesn't contain exactly 2000 results", fileLines.size() == 2000);
		Assert.assertTrue("results doesn't contain: 401 Q0 FBIS4-18602 946 -7.0698 Indri", fileLines.contains("401 Q0 FBIS3-59033 968 -7.0777 Indri"));
		TestUtils.deleteFile("baseLineMap.res");
	}
	
	@Test
	public void testCreateFileName() throws Exception {
		classUnderTest = Whitebox.newInstance(BaseLine.class);
		//This is the way to pass an array to a private method that is invoked by PowerMock
		String[] params = new String[] {"Okapi", "k1:1.2", "b:0.75", "k3:7" };
		String fileName = Whitebox.<String>invokeMethod(classUnderTest, "createFileName", new Class<?>[] {String[].class}, (Object)params);
		
		String expectedFileName = "Okapi_k1-1.2_b-0.75_k3-7";
		Assert.assertTrue(String.format("file name was '%s' and not '%s'", fileName, expectedFileName), expectedFileName.equals(fileName));
	}

}

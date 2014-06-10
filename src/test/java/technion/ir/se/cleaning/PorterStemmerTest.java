package technion.ir.se.cleaning;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class PorterStemmerTest {

	private PorterStemmer porterStemmer;
	@Before
	public void setUp() throws Exception {
		porterStemmer = new PorterStemmer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStem_Single_S() {
		String wordBeforeSteam = "sailors";
		porterStemmer.add(wordBeforeSteam.toCharArray(), wordBeforeSteam.length());
		porterStemmer.stem();
		char[] resultBuffer = porterStemmer.getResultBuffer();
		String wordAfterSteam = new String(resultBuffer, 0, porterStemmer.getResultLength());
		assertTrue("Didn't strem word", !wordAfterSteam.equalsIgnoreCase(wordBeforeSteam));
	}

}

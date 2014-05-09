package technion.ir.se.stemmer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DocumentStemmerTest {

	private DocumentStemmer stemmer;
	@Before
	public void setUp() throws Exception {
		stemmer = new DocumentStemmer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSteamWord() {
		String wordToSteam = "eggs";
		String steamedWord = stemmer.steamWord(wordToSteam);
		assertTrue("Word was not steamed", !steamedWord.equalsIgnoreCase(wordToSteam));
	}

}

package technion.ir.se.cleaning;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.cleaning.DocumentStemmer;

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
		String steamedWord = stemmer.stemWord(wordToSteam);
		assertTrue("Word was not steamed", !steamedWord.equalsIgnoreCase(wordToSteam));
	}
	
	@Test
	public void testSteamWords() {
		List<String> wordsToStem = Arrays.asList("eggs","cat","babies");
		ArrayList<String> wordsBeforeStem = new ArrayList<String>(wordsToStem);
		stemmer.steamWords(wordsToStem);
		boolean areListsDiffer = wordsBeforeStem.retainAll(wordsToStem);
		assertTrue("Lists are the same", areListsDiffer);
		assertTrue("Stemmed list doesn't contain cat", wordsBeforeStem.contains("cat"));
	}

}

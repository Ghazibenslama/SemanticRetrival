package technion.ir.se.stopwords;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StopwordsRemoverTest {

	private StopwordsRemover stopwordsRemover;
	@Before
	public void setUp() throws Exception {
		stopwordsRemover = StopwordsRemover.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		stopwordsRemover.init();
	}

	@Test
	public void testClear() {
		stopwordsRemover.clear();
		assertTrue("Didn't clear all stopwords", stopwordsRemover.size() == 0);
		
		stopwordsRemover = StopwordsRemover.getInstance();
		assertTrue("StopwordsRemover is not singletone", stopwordsRemover.size() == 0);
	}

	@Test
	public void testAdd() {
		int initiallSize = stopwordsRemover.size();
		
		stopwordsRemover.add("xxxxxxx");
		assertTrue("Didn't add one stopwords", stopwordsRemover.size() == (initiallSize+1) );
	}
	
	@Test
	public void teadAdd_existingWord() {
		stopwordsRemover.add("xxxxxxx");
		int sizeAfterAddingWord = stopwordsRemover.size();
		stopwordsRemover.add("xxxxxxx");
		int sizeAfterAddingExisitngWord = stopwordsRemover.size();
		assertTrue("Added an existing word", sizeAfterAddingWord == sizeAfterAddingExisitngWord );

	}

	@Test
	public void testRemove() {
		int initiallSize = stopwordsRemover.size();
		
		boolean wasRemoved = stopwordsRemover.remove("between");
		assertTrue("FailedTo remove a word", wasRemoved);
		assertTrue("Didn't add one stopwords", stopwordsRemover.size() == (initiallSize-1) );
	}
	
	@Test
	public void testRemove_notExistingWord() {
		boolean wasRemoved = stopwordsRemover.remove("between");
		assertTrue("FailedTo remove a word", wasRemoved);
		wasRemoved = stopwordsRemover.remove("between");
		assertFalse("Removed non exisitn g word", wasRemoved);
	}
	

	@Test
	public void testIs() {
		boolean isStopWord = stopwordsRemover.is("between");
		assertTrue("didn't detect stopword", isStopWord);
	}
	
	@Test
	public void testIs_withSpaces() {
		boolean isStopWord = stopwordsRemover.is(" between ");
		assertTrue("didn't detect stopword", isStopWord);
	}
	
	@Test
	public void testIs_WithMixedCase() {
		boolean isStopWord = stopwordsRemover.is("beTweEn");
		assertTrue("didn't detect stopword", isStopWord);
	}
	
	@Test
	public void testIs_NewWord() {
		stopwordsRemover.add("QWERASDFZXC");
		boolean isStopWord = stopwordsRemover.is("QWERASDFZXC");
		assertTrue("didn't detect stopword", isStopWord);
	}
	
	@Test
	public void testIs_notExisitng() {
		boolean isStopWord = stopwordsRemover.is("QWERASDFZXC");
		assertFalse("False detection. Detected a simpel word as stopword", isStopWord);
	}

}

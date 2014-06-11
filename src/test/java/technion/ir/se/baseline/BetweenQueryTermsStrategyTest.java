package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWidow;

public class BetweenQueryTermsStrategyTest {

	private BetweenQueryTermsStrategy classUnderTest;
	private Feedback feedback;
	private Query query;
	private static final String STORY = "my name is alon i come for moldova which was once part of russia in russia i was called alexy" +
			" but now i'm called alon and i love it";
	@Before
	public void setUp() throws Exception {
		classUnderTest = new BetweenQueryTermsStrategy();
		feedback = PowerMockito.mock(Feedback.class);
		query = PowerMockito.mock(Query.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test (expected = IllegalAccessError.class)
	public void testSetWindowSize() {
		classUnderTest.setWindowSize(7);
	}
	
	@Test
	public void testGetWindows_hasSixWindows() {
		PowerMockito.when(feedback.getTerms()).thenReturn(Arrays.asList(STORY.split(" ")));
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWidow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("there should be 6 windows", 6l, windows.size());
	}
	
	@Test
	public void testGetWindows_checkWindowsSizes() {
		PowerMockito.when(feedback.getTerms()).thenReturn(Arrays.asList(STORY.split(" ")));
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWidow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("there should be 4 windows", 4l, windows.get(0).getWindowSize());
		Assert.assertEquals("there should be 5 windows", 5l, windows.get(1).getWindowSize());
		Assert.assertEquals("there should be 7 windows", 7l, windows.get(2).getWindowSize());
		Assert.assertEquals("there should be 3 windows", 3l, windows.get(3).getWindowSize());
		Assert.assertEquals("there should be 10 windows", 10l, windows.get(4).getWindowSize());
		Assert.assertEquals("there should be 5 windows", 5l, windows.get(5).getWindowSize());
	}
	
	@Test
	public void testGetWindows_checkWindowsEndPositions() {
		List<String> story = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(story);
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWidow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("last word in window should be alon", "alon", story.get(windows.get(0).getWindowEnd()));
		Assert.assertEquals("last word in window should be moldova", "moldova", story.get(windows.get(1).getWindowEnd()));
		Assert.assertEquals("last word in window should be russia", "russia", story.get(windows.get(2).getWindowEnd()));
		Assert.assertEquals("last word in window should be russia", "russia", story.get(windows.get(3).getWindowEnd()));
		Assert.assertEquals("last word in window should be alon", "alon", story.get(windows.get(4).getWindowEnd()));
		Assert.assertEquals("last word in window should be it", "it", story.get(windows.get(5).getWindowEnd()));
	}
	
	@Test
	public void testGetWindows_checkWindowsStartPositions() {
		List<String> story = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(story);
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWidow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("last word in window should be my", "my", story.get(windows.get(0).getWindowStart()));
		Assert.assertEquals("last word in window should be alon", "alon", story.get(windows.get(1).getWindowStart()));
		Assert.assertEquals("last word in window should be moldova", "moldova", story.get(windows.get(2).getWindowStart()));
		Assert.assertEquals("last word in window should be russia", "russia", story.get(windows.get(3).getWindowStart()));
		Assert.assertEquals("last word in window should be russia", "russia", story.get(windows.get(4).getWindowStart()));
		Assert.assertEquals("last word in window should be alon", "alon", story.get(windows.get(5).getWindowStart()));
	}
	
	@Test
	public void testCalcWindowEnd() throws Exception {
		HashSet<String> queryTerms = new HashSet<String>();
		queryTerms.addAll(Arrays.asList("russia", "Moldova","alon"));
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.addAll(Arrays.asList(STORY.split(" ")));
		
		Integer windowEnd = Whitebox.<Integer>invokeMethod(classUnderTest, "calcWindowEnd", queryTerms, tokens, 0);
		Assert.assertEquals("didn't find window end", 3l, windowEnd.longValue());
	}
}

package technion.ir.se.windows;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;
import technion.ir.se.windows.HalfDistanceBetweenQueryTermsStrategy;

@PrepareForTest(HalfDistanceBetweenQueryTermsStrategy.class)
public class HalfDistanceBetweenQueryTermsStrategyTest {
	
	@Rule
    public PowerMockRule rule = new PowerMockRule();

	private HalfDistanceBetweenQueryTermsStrategy classUnderTest;
	private Feedback feedback;
	private Query query;
	private static final String STORY = "my name is alon i come for moldova which was once part of russia in russia i was called alexy" +
			" but now i'm called alon and i love it";
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new HalfDistanceBetweenQueryTermsStrategy();
		feedback = PowerMockito.mock(Feedback.class);
		query = PowerMockito.mock(Query.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testGetWindows() {
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		assertEquals("There should have been 6 windows", 6l, windows.size());
		
	}
	
	@Test
	public void testGetWindows_NoQueryTermExistsInFeedback() throws Exception {
		HalfDistanceBetweenQueryTermsStrategy partialMock = PowerMock.createPartialMock(HalfDistanceBetweenQueryTermsStrategy.class, "findQueryTermsOccurrences");
		PowerMock.expectPrivate(partialMock, "findQueryTermsOccurrences", EasyMock.anyObject(), EasyMock.anyObject())
			.andReturn(new ArrayList<TextWindow>());
		PowerMock.replay(partialMock);
		
		List<TextWindow> windows = partialMock.getWindows(feedback, query);
		Assert.assertTrue("there should be now windows", windows.isEmpty());
	}
	
	@Test
	public void testGetWindows_LessThanOneQueryTermExistsInFeedback() throws Exception {
		HalfDistanceBetweenQueryTermsStrategy partialMock = PowerMock.createPartialMock(HalfDistanceBetweenQueryTermsStrategy.class, "findQueryTermsOccurrences");
		PowerMock.expectPrivate(partialMock, "findQueryTermsOccurrences", EasyMock.anyObject(), EasyMock.anyObject())
			.andAnswer(new IAnswer<List<TextWindow>>() {
		        @Override
		        public List<TextWindow> answer() throws Throwable {
		        	ArrayList<TextWindow> list = new ArrayList<TextWindow>();
		        	list.add(new TextWindow(0, 0));
		            return list;
		        }
		    });
		
		PowerMock.replay(partialMock);
		
		List<TextWindow> windows = partialMock.getWindows(feedback, query);
		Assert.assertTrue("there should be now windows", windows.isEmpty());
	}


	@Test (expected = IllegalAccessError.class)
	public void testSetWindowSize() {
		classUnderTest.setWindowSize(4);
	}
	
	@Test
	public void testFindQueryTermsOccurrences() throws Exception {
		List<String> queryTerms = new ArrayList<String>();
		queryTerms.addAll(Arrays.asList("russia", "Moldova","alon"));
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.addAll(Arrays.asList(STORY.split(" ")));
		
		List<Integer> indexs = Whitebox.<List<Integer>>invokeMethod(classUnderTest, "findQueryTermsOccurrences", queryTerms, tokens);
		assertEquals("didn't find 1'st query term", 3l, indexs.get(0).longValue());
		assertEquals("didn't find 2'nd query term", 7l, indexs.get(1).longValue());
		assertEquals("didn't find 3'd query term", 13l, indexs.get(2).longValue());
		assertEquals("didn't find 4'th query term", 15l, indexs.get(3).longValue());
		assertEquals("didn't find 5'th query term", 24l, indexs.get(4).longValue());
	}
	
	@Test
	public void testCreateWindows() throws Exception {
		ArrayList<Integer> indexs = new ArrayList<Integer>(Arrays.asList(3,7,13,15,24));
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.addAll(Arrays.asList(STORY.split(" ")));
		
		List<TextWindow> list = Whitebox.<List<TextWindow>>invokeMethod(classUnderTest, "createWindows", indexs, tokens);
		assertEquals("list doesn't contain 5 windows", 5l, list.size());
	}
	
	@Test
	public void testCreateWindows_testStartWords() throws Exception {
		ArrayList<Integer> indexs = new ArrayList<Integer>(Arrays.asList(3,7,13,15,24));
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.addAll(Arrays.asList(STORY.split(" ")));
		
		List<TextWindow> list = Whitebox.<List<TextWindow>>invokeMethod(classUnderTest, "createWindows", indexs, tokens);
		assertEquals("first word of window is not right", "name", tokens.get( list.get(0).getWindowStart() ));
		assertEquals("first word of window is not right", "come", tokens.get( list.get(1).getWindowStart() ));
		assertEquals("first word of window is not right", "once", tokens.get( list.get(2).getWindowStart() ));
		assertEquals("first word of window is not right", "in", tokens.get( list.get(3).getWindowStart() ));
		assertEquals("first word of window is not right", "but", tokens.get( list.get(4).getWindowStart() ));
		
	}
	
	@Test
	public void testCreateWindows_testEndWords() throws Exception {
		ArrayList<Integer> indexs = new ArrayList<Integer>(Arrays.asList(3,7,13,15,24));
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.addAll(Arrays.asList(STORY.split(" ")));
		
		List<TextWindow> list = Whitebox.<List<TextWindow>>invokeMethod(classUnderTest, "createWindows", indexs, tokens);
		assertEquals("first word of window is not right", "come", tokens.get( list.get(0).getWindowEnd() ));
		assertEquals("first word of window is not right", "once", tokens.get( list.get(1).getWindowEnd() ));
		assertEquals("first word of window is not right", "in", tokens.get( list.get(2).getWindowEnd() ));
		assertEquals("first word of window is not right", "alexy", tokens.get( list.get(3).getWindowEnd() ));
		assertEquals("first word of window is not right", "it", tokens.get( list.get(4).getWindowEnd() ));
		
	}
	
	@Test
	public void testCreateLastWindow_gapTooSmall() throws Exception {
		TextWindow widow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createLastWindow", 30, 3, 32);
		assertEquals("Windows did start on right location", 27l, widow.getWindowStart());
		assertEquals("Windows did end on right location", 32l, widow.getWindowEnd());
	}
	
	@Test
	public void testCreateLastWindow() throws Exception {
		TextWindow widow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createLastWindow", 30, 3, 40);
		assertEquals("Windows did start on right location", 27l, widow.getWindowStart());
		assertEquals("Windows did end on right location", 33l, widow.getWindowEnd());
	}
	
	@Test
	public void testCreateFirstWindow_gapTooSmall() throws Exception {
		TextWindow widow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createFirstWindow", 3, 5);
		assertEquals("Windows did start on right location", 0l, widow.getWindowStart());
		assertEquals("Windows did end on right location", 8l, widow.getWindowEnd());
	}
	
	@Test
	public void testCreateFirstWindow() throws Exception {
		TextWindow widow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createFirstWindow", 9, 5);
		assertEquals("Windows did start on right location", 4l, widow.getWindowStart());
		assertEquals("Windows did end on right location", 14l, widow.getWindowEnd());
	}

}

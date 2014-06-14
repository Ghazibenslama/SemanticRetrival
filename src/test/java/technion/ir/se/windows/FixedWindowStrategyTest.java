package technion.ir.se.windows;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.TextWindow;
import technion.ir.se.windows.FixedWindowStrategy;

public class FixedWindowStrategyTest {

	private FixedWindowStrategy classUnderTest;
	@Before
	public void setUp() throws Exception {
		classUnderTest = new FixedWindowStrategy();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalcWindowEndIndex() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 8);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 8);
		int windowEndIndex = Whitebox.invokeMethod(classUnderTest, "calcWindowEndIndex", 3);
		assertTrue("window end index should be 3", windowEndIndex==3);
	}
	
	@Test
	public void testCalcWindowEndIndex_endIndexBig() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 18);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 8);
		int windowEndIndex = Whitebox.invokeMethod(classUnderTest, "calcWindowEndIndex", 13);
		assertTrue("window end index should be 13", windowEndIndex==13);
	}
	
	@Test
	public void testCalcWindowEndIndex_endIndexBiggerThanNumberOfTerms() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 18);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 8);
		int windowEndIndex = Whitebox.invokeMethod(classUnderTest, "calcWindowEndIndex", 20);
		assertTrue("window end index should be 17", windowEndIndex==17);
	}
	
	@Test
	public void testAddWindow() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 18);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createWindow", 0, 4);
		assertTrue("window should have start at index 0", textWidow.getWindowStart() == 0);
		assertTrue("window should have end at index 4", textWidow.getWindowEnd() == 4);
		assertTrue("window size should be 5", textWidow.getWindowSize() == 5);
	}
	
	@Test
	public void testAddWindow_exeddedNumberOfTerms() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 18);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createWindow", 17, 18);
		assertTrue("window should have start at index 17", textWidow.getWindowStart() == 17);
		assertTrue("window should have end at index 17", textWidow.getWindowEnd() == 17);
		assertTrue("window size should be 1", textWidow.getWindowSize() == 1);
	}
	
	@Test
	public void testCreateFirstWindow() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 30);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 8);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createFirstWindow");
		assertTrue("window should have start at index 0", textWidow.getWindowStart() == 0);
		assertTrue("window should have end at index 7", textWidow.getWindowEnd() == 7);
		assertTrue("window size should be 8", textWidow.getWindowSize() == 8);
	}
	
	@Test
	public void testCreateFirstWindow_fewTerms() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 5);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 8);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createFirstWindow");
		assertTrue("window should have start at index 0", textWidow.getWindowStart() == 0);
		assertTrue("window should have end at index 4", textWidow.getWindowEnd() == 4);
		assertTrue("window size should be 5", textWidow.getWindowSize() == 5);
	}
	
	@Test
	public void testCreateLastWindow() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 6);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 5);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createLastWindow");
		assertTrue("window should have start at index 5", textWidow.getWindowStart() == 5);
		assertTrue("window should have end at index 5", textWidow.getWindowEnd() == 5);
		assertTrue("window size should be 1", textWidow.getWindowSize() == 1);
	}
	
	@Test
	public void testCreateLastWindow_onlyFirstWindowExists() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 3);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 5);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createLastWindow");
		assertTrue("last window should not have been created", textWidow == null);
	}
	
	@Test
	public void testCreateLastWindow_noLastWindowNeeded() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 10);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 5);
		TextWindow textWidow = Whitebox.<TextWindow>invokeMethod(classUnderTest, "createLastWindow");
		assertTrue("window should have start at index 5", textWidow.getWindowStart() == 5);
		assertTrue("window should have end at index 9", textWidow.getWindowEnd() == 9);
		assertTrue("window size should be 5", textWidow.getWindowSize() == 5);
	}
	
	@Test
	public void testCreateOtherWindows() throws Exception {
		Whitebox.setInternalState(classUnderTest, "numberOfTerms", 21);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 5);
		List<TextWindow> textWidows = Whitebox.<List<TextWindow>>invokeMethod(classUnderTest, "createOtherWindows", 5);
		assertTrue("there should have been 4 windows", textWidows.size() == 4);
		for (int windowsIndex = 0; windowsIndex < 3; windowsIndex++) {
			int i = windowsIndex + 1;
			TextWindow textWidow = textWidows.get(windowsIndex);
			assertTrue("window should have start at position " + String.valueOf(i*5), textWidow.getWindowStart() == i*5);
			assertTrue("window should have end at position " + String.valueOf((i+1)*5-1), textWidow.getWindowEnd() == (i+1)*5-1);
			assertTrue("window size should be 5", textWidow.getWindowSize() == 5);
		}
		TextWindow textWidow = textWidows.get(3);
		assertTrue("window should have start at index 20", textWidow.getWindowStart() == 20);
		assertTrue("window should have end at index 20", textWidow.getWindowEnd() == 20);
		assertTrue("window size should be 1", textWidow.getWindowSize() == 1);
	}
	
	
	@Test
	public void testGetWindows(){
		Feedback feedback = PowerMockito.mock(Feedback.class);
		PowerMockito.when(feedback.getNumberOfTerms()).thenReturn(38);
		Whitebox.setInternalState(classUnderTest, "fixedWindowSize", 7);
		
		TextWindow textWidow;
		List<TextWindow> windows = classUnderTest.getWindows(feedback, null);
		assertTrue("Didn't get all windows", windows.size() == 6);
		for (int i = 0; i < windows.size() - 1; i++) {
			textWidow = windows.get(i);
			assertTrue("window should have start at position " + String.valueOf(i*7), textWidow.getWindowStart() == i*7);
			assertTrue("window should have end at position " + String.valueOf((i+1)*7-1), textWidow.getWindowEnd() == (i+1)*7-1);
			assertTrue("window size should be 7", textWidow.getWindowSize() == 7);
		}
		textWidow = windows.get(5);
		assertTrue("window should have start at position 35" , textWidow.getWindowStart() == 35);
		assertTrue("window should have end at position 37", textWidow.getWindowEnd() == 37);
		assertTrue("window size should be 3", textWidow.getWindowSize() == 3);
	}

}

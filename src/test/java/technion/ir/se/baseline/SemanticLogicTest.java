package technion.ir.se.baseline;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.SemanticTermScore;

public class SemanticLogicTest {

	private SemanticLogic classUnderTest;
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SemanticLogic();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertMaps() throws Exception {
		HashMap<String, int[]> testMap = new HashMap<String, int[]>();
		for (int i = 0; i < 30; i++) {
			int[] intArray = new int[30];
			for (int j = 0; j < 30; j++) {
				intArray[j] = i*j;
			}
			testMap.put("q"+String.valueOf(i), intArray);
		}
		
		Map<String, double[]> doubleMap = Whitebox.<Map<String, double[]>>invokeMethod(classUnderTest, "convertMaps", testMap);
		for (Entry<String, double[]> entry : doubleMap.entrySet()) {
			double[] doubleVector = entry.getValue();
			int[] intMap = testMap.get(entry.getKey());
			for (int i = 0; i < intMap.length; i++) {
				assertEquals(intMap[i], doubleVector[i], 0);
			}
		}
	}
	
	@Test
	public void testGetTermAlternatives() throws Exception {
		List<SemanticTermScore> termScores = new ArrayList<SemanticTermScore>();
		termScores.add(new SemanticTermScore("first", 0.88));
		termScores.add(new SemanticTermScore("second", 0.44));
		termScores.add(new SemanticTermScore("third", 0.33));
		List<String> list = Whitebox.<List<String>>invokeMethod(classUnderTest, "getTermAlternatives", termScores);
		assertEquals("list size not as expected", 1l, list.size());
		assertEquals("list doesn't contain expected element", "first", list.get(0));
		
	}

}

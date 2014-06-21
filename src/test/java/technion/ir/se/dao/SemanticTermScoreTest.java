package technion.ir.se.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SemanticTermScoreTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompareTo_positiveValues() {
		SemanticTermScore a = new SemanticTermScore("a", 0.9);
		SemanticTermScore b = new SemanticTermScore("b", 0.8);
		SemanticTermScore c = new SemanticTermScore("c", 0.7);
		Assert.assertEquals("a is bigger than b", 1l, a.compareTo(b));
		Assert.assertEquals("b is bigger than c", 1l, b.compareTo(c));
		Assert.assertEquals("a is bigger than c", 1l, a.compareTo(c));
		
		Assert.assertEquals("b is smaller than a", -1l, b.compareTo(a));
		Assert.assertEquals("c is smaller than a", -1l, c.compareTo(a));
		Assert.assertEquals("c is smaller than b", -1l, c.compareTo(b));
	}
	
	@Test
	public void testCompareTo_negativeValues() {
		SemanticTermScore a = new SemanticTermScore("a", -0.1);
		SemanticTermScore b = new SemanticTermScore("b", -0.6);
		SemanticTermScore c = new SemanticTermScore("c", -0.7);
		Assert.assertEquals("a is bigger than b", 1l, a.compareTo(b));
		Assert.assertEquals("b is bigger than c", 1l, b.compareTo(c));
		Assert.assertEquals("a is bigger than c", 1l, a.compareTo(c));
		
		Assert.assertEquals("b is smaller than a", -1l, b.compareTo(a));
		Assert.assertEquals("c is smaller than a", -1l, c.compareTo(a));
		Assert.assertEquals("c is smaller than b", -1l, c.compareTo(b));
	}
	
	@Test (expected = NullPointerException.class)
	public void testCompareTo_nullValues() {
		SemanticTermScore a = new SemanticTermScore("a", 3.5);
		SemanticTermScore b = null;
		Assert.assertEquals("a is bigger than b", 1l, a.compareTo(b));
	}
	
	@Test
	public void testCompareTo_equalValues() {
		SemanticTermScore a = new SemanticTermScore("a", 3.5);
		SemanticTermScore c = new SemanticTermScore("c", 3.5);
		
		Assert.assertEquals("a is equal to c", 0l, a.compareTo(c));
		Assert.assertEquals("c is equal to a", 0l, c.compareTo(a));
	}

	@Test
	public void testCompareTo_CollectionSort() {
		SemanticTermScore a = new SemanticTermScore("a", 0.9);
		SemanticTermScore b = new SemanticTermScore("b", 0.8);
		List<SemanticTermScore> list = new ArrayList<SemanticTermScore>();
		list.add(a);
		list.add(b);
		Collections.sort(list);
		Assert.assertEquals("second object in list after sort should be a", a, list.get(1));
		Assert.assertEquals("first object in list after sort should be b", b, list.get(0));
		
		list.clear();
		list.add(b);
		list.add(a);
		Collections.sort(list);
		Assert.assertEquals("second object in list after sort should be a", a, list.get(1));
		Assert.assertEquals("first object in list after sort should be b", b, list.get(0));
		
	}
}

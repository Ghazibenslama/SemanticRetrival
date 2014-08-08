package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.dao.SemanticTermScore;

public class AlternativesLogicTest {

	private AlternativesLogic classUnderTest;
	private List<SemanticTermScore> semanticTermScores;
	
	private void createTermsScores() {
		semanticTermScores = new ArrayList<SemanticTermScore>();
		semanticTermScores.add(new SemanticTermScore("first", 0.9));
		semanticTermScores.add(new SemanticTermScore("six", 0.2));
		semanticTermScores.add(new SemanticTermScore("fifth", 0.3));
		semanticTermScores.add(new SemanticTermScore("third", 0.7));
		semanticTermScores.add(new SemanticTermScore("forth", 0.6));
		semanticTermScores.add(new SemanticTermScore("second", 0.8));
		Collections.sort(semanticTermScores);
	}

	@Before
	public void setUp() {
		classUnderTest = new AlternativesLogic();
		this.createTermsScores();
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTermAlternatives_sizeOne() {
		List<String> alternatives = classUnderTest.getTermAlternatives(semanticTermScores, 1);
		Assert.assertEquals("there should have been 5 results", 5, alternatives.size());
	}
	
	@Test
	public void testGetTermAlternatives_sizeTwo() {
		List<String> alternatives = classUnderTest.getTermAlternatives(semanticTermScores, 2);
		Assert.assertEquals("there should have been 5 results", 5, alternatives.size());
	}
	
	@Test
	public void testGetTermAlternatives_sizeThree() {
		List<String> alternatives = classUnderTest.getTermAlternatives(semanticTermScores, 3);
		Assert.assertEquals("there should have been 3 results", 3, alternatives.size());
	}
	
	@Test
	public void testGetTermAlternatives_sizeFour() {
		List<String> alternatives = classUnderTest.getTermAlternatives(semanticTermScores, 4);
		Assert.assertEquals("there should have been 2 results", 2, alternatives.size());
	}
	
	@Test
	public void testGetTermAlternatives_sizeFive() {
		List<String> alternatives = classUnderTest.getTermAlternatives(semanticTermScores, 5);
		Assert.assertEquals("there should have been 2 results", 2, alternatives.size());
	}
	
	@Test
	public void testGetTermAlternatives_sizeOther() {
		List<String> alternatives = classUnderTest.getTermAlternatives(semanticTermScores, 6);
		Assert.assertEquals("there should have been 1 results", 1, alternatives.size());
	}

}

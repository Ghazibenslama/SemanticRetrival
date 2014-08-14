package technion.ir.se.baseline;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.MutualInformation;
import technion.ir.se.indri.SearchEngine;

public class MutualInformationLogicTest {
	private static final double DELTA = 0.001;
	private MutualInformationLogic classUnderTest;

	@Before
	public void setUp() throws Exception {
		
		SearchEngine engine = PowerMockito.mock(SearchEngine.class);
		classUnderTest = new MutualInformationLogic(engine);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalcTermProb() throws Exception {
		Double termProb = Whitebox.<Double>invokeMethod(classUnderTest, "calcTermProb", 3.0, 4.0);
		Assert.assertEquals("calculation of term probablity should have been 0.75 ", 3.0/4, termProb, DELTA);
	}
	
	@Test
	public void testCalcTermProb_zero() throws Exception {
		Double termProb = Whitebox.<Double>invokeMethod(classUnderTest, "calcTermProb", 0.0, 4.0);
		Assert.assertEquals("calculation of term probablity should have been 0 ", 0, termProb, DELTA);
	}
	
	@Test
	public void testCalcTermProb_divideByZero() throws Exception {
		Double termProb = Whitebox.<Double>invokeMethod(classUnderTest, "calcTermProb", 4.0,0.0);
		Assert.assertEquals("calculation of term probablity should have been 0 ", 0, termProb, DELTA);
	}
	
	@Test
	public void testCalcOnlyOneTermExists() throws Exception {
		MutualInformation mi = new MutualInformation(0,0,10,50);
		mi.setMutualProb(0.3);
		Double prob = Whitebox.<Double>invokeMethod(classUnderTest, "calcOnlyOneTermExists", 0.5, mi);
		Assert.assertEquals("result should have been 0.004", 0.2/50, prob, DELTA);
	}
	
	@Test
	public void testCalcOnlyOneTermExists_noMutual() throws Exception {
		MutualInformation mi = new MutualInformation(0,0,10,50);
		mi.setMutualProb(0);
		Double prob = Whitebox.<Double>invokeMethod(classUnderTest, "calcOnlyOneTermExists", 0.5, mi);
		Assert.assertEquals("result should have been 0.01", 0.5/50, prob, DELTA);
	}
	
	@Test
	public void testCalcOnlyOneTermExists_noDocs() throws Exception {
		MutualInformation mi = new MutualInformation(0,0,10,0);
		mi.setMutualProb(0);
		Double prob = Whitebox.<Double>invokeMethod(classUnderTest, "calcOnlyOneTermExists", 0.5, mi);
		Assert.assertEquals("result should have been 0.0", 0, prob, DELTA);
	}
	
	@Test
	public void testCalcFormula() throws Exception {
		Double result = Whitebox.<Double>invokeMethod(classUnderTest, "calcFormula", 0.0, 0.0, 0.0);
		Assert.assertEquals("result should have been 0", 0, result, DELTA);
	}
	
	@Test
	public void testCalcFormula_notNan1() throws Exception {
		Double result = Whitebox.<Double>invokeMethod(classUnderTest, "calcFormula", 0.0, 0.0, 0.2);
		Assert.assertEquals("result should have been 0", 0, result, DELTA);
	}
	
	@Test
	public void testCalcFormula_notNan2() throws Exception {
		Double result = Whitebox.<Double>invokeMethod(classUnderTest, "calcFormula", 0.0, 0.2, 0.0);
		Assert.assertEquals("result should have been 0", 0, result, DELTA);
	}
	
	@Test
	public void testCalcFormula_noMutual() throws Exception {
		Double result = Whitebox.<Double>invokeMethod(classUnderTest, "calcFormula", 0.0, 0.2, 0.1);
		Assert.assertEquals("result should have been 0", 0, result, DELTA);
	}
	
}

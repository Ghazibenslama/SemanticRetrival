package technion.ir.se.baseline;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.dao.ResultFormat;

public class FusionLogicTest {

	private FusionLogic classUnderTest;
	private List<ResultFormat> firstListResultFormat;
	private List<ResultFormat> secondListResultFormat;
	@Before
	public void setUp() throws Exception {
		classUnderTest = new FusionLogic();
		firstListResultFormat = new ArrayList<ResultFormat>();
		firstListResultFormat.add(new ResultFormat("1", "q0", "2", 1, 4, "indri"));
		firstListResultFormat.add(new ResultFormat("1", "q0", "4", 2, 3, "indri"));
		firstListResultFormat.add(new ResultFormat("1", "q0", "1", 3, 2, "indri"));
		firstListResultFormat.add(new ResultFormat("1", "q0", "3", 4, 1, "indri"));
		
		secondListResultFormat = new ArrayList<ResultFormat>();
		secondListResultFormat.add(new ResultFormat("1", "q0", "1", 1, 4, "indri"));
		secondListResultFormat.add(new ResultFormat("1", "q0", "3", 2, 3, "indri"));
		secondListResultFormat.add(new ResultFormat("1", "q0", "4", 3, 2, "indri"));
		secondListResultFormat.add(new ResultFormat("1", "q0", "5", 4, 1, "indri"));
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNormalizeMaxMin() {
		classUnderTest.NormalizeMaxMin(firstListResultFormat);
		Assert.assertEquals("Socre of first element not normilized correctly", 
				Double.valueOf(1), Double.valueOf(firstListResultFormat.get(0).getScore()));
		Assert.assertEquals("Socre of second element not normilized correctly", 
				Double.valueOf(2.0/3), Double.valueOf(firstListResultFormat.get(1).getScore()));
		Assert.assertEquals("Socre of third element not normilized correctly", 
				Double.valueOf(1.0/3), Double.valueOf(firstListResultFormat.get(2).getScore()));
		Assert.assertEquals("Socre of forth element not normilized correctly", 
				Double.valueOf(0), Double.valueOf(firstListResultFormat.get(3).getScore()));
		
	}

	@Test
	public void testMergeResults() {
		List<ResultFormat> mergedResults = new ArrayList<ResultFormat>();
		List<List<ResultFormat>> queryVariantsResults = new ArrayList<List<ResultFormat>>();
		queryVariantsResults.add(firstListResultFormat);
		queryVariantsResults.add(secondListResultFormat);
		mergedResults = classUnderTest.MergeResults(queryVariantsResults);
		Assert.assertEquals("size of merged list is not correct", 5, mergedResults.size());
		Assert.assertEquals("1", mergedResults.get(0).getDocumentID());
		Assert.assertEquals("4", mergedResults.get(1).getDocumentID());
		Assert.assertEquals("5", mergedResults.get(4).getDocumentID());
		Assert.assertEquals("Socre of first element not correct", 
				Double.valueOf(6), Double.valueOf(mergedResults.get(0).getScore()));
		Assert.assertEquals("Socre of Last element not correct", 
				Double.valueOf(1), Double.valueOf(mergedResults.get(4).getScore()));
	}

}

package technion.ir.se.baseline;

import java.util.ArrayList;
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
	
	private List<ResultFormat> thirdListResultFormat;
	private List<ResultFormat> fourthListResultFormat;
	
	private List<ResultFormat> fifthListResultFormat;
	private List<ResultFormat> sixListResultFormat;
	
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new FusionLogic();
		
		firstListResultFormat = new ArrayList<ResultFormat>();
		firstListResultFormat.add(new ResultFormat("1","2", 1, 4));
		firstListResultFormat.add(new ResultFormat("1","4", 2, 3));
		firstListResultFormat.add(new ResultFormat("1","1", 3, 2));
		firstListResultFormat.add(new ResultFormat("1","3", 4, 1));
		
		secondListResultFormat = new ArrayList<ResultFormat>();
		secondListResultFormat.add(new ResultFormat("1","1", 1, 4));
		secondListResultFormat.add(new ResultFormat("1","3", 2, 3));
		secondListResultFormat.add(new ResultFormat("1","4", 3, 2));
		secondListResultFormat.add(new ResultFormat("1","5", 4, 1));
		
		thirdListResultFormat = new ArrayList<ResultFormat>();
		thirdListResultFormat.add(new ResultFormat("310","1", 1, 5));
		thirdListResultFormat.add(new ResultFormat("310","2", 2, 4));
		thirdListResultFormat.add(new ResultFormat("310","3", 3, 3));
		thirdListResultFormat.add(new ResultFormat("310","4", 4, 2));
		thirdListResultFormat.add(new ResultFormat("310","5", 5, 1));
		
		fourthListResultFormat = new ArrayList<ResultFormat>();
		fourthListResultFormat.add(new ResultFormat("310","6", 1, 5));
		fourthListResultFormat.add(new ResultFormat("310","7", 2, 4));
		fourthListResultFormat.add(new ResultFormat("310","8", 3, 3));
		fourthListResultFormat.add(new ResultFormat("310","9", 4, 2));
		fourthListResultFormat.add(new ResultFormat("310","10", 5, 1));
		
		fifthListResultFormat = new ArrayList<ResultFormat>();
		fifthListResultFormat.add(new ResultFormat("310","52032", 1, -7.588));
		fifthListResultFormat.add(new ResultFormat("310","66406", 2, -7.9));
		fifthListResultFormat.add(new ResultFormat("310","21150", 3, -8));
		fifthListResultFormat.add(new ResultFormat("310","44915", 4, -8.15));
		fifthListResultFormat.add(new ResultFormat("310","59661", 5, -8.292));
		
		sixListResultFormat = new ArrayList<ResultFormat>();
		sixListResultFormat.add(new ResultFormat("310","66406", 1, -7.33));
		sixListResultFormat.add(new ResultFormat("310","59661", 2, -7.712));
		sixListResultFormat.add(new ResultFormat("310","52032", 3, -7.815));
		sixListResultFormat.add(new ResultFormat("310","67640", 4, -8.0883));
		sixListResultFormat.add(new ResultFormat("310","44915", 5, -8.1349));
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testMergeResults() {
		List<List<ResultFormat>> queryVariantsResults = new ArrayList<List<ResultFormat>>();
		List<List<ResultFormat>> queryVariantsResults2 = new ArrayList<List<ResultFormat>>();
		queryVariantsResults.add(firstListResultFormat);
		queryVariantsResults.add(secondListResultFormat);

		queryVariantsResults2.add(thirdListResultFormat);
		queryVariantsResults2.add(fourthListResultFormat);

		
		List<ResultFormat> mergedResults = classUnderTest.mergeResults(queryVariantsResults);
		List<ResultFormat> mergedResults2 = classUnderTest.mergeResults(queryVariantsResults2);
		
		
		Assert.assertEquals("size of merged list is not correct", 5, mergedResults.size());
		Assert.assertEquals("1", mergedResults.get(0).getDocumentID());
		Assert.assertEquals("4", mergedResults.get(1).getDocumentID());
		Assert.assertEquals("5", mergedResults.get(4).getDocumentID());
		
		Assert.assertEquals("Socre of first element not normilized correctly", 
				Double.valueOf(4.0/3), Double.valueOf(mergedResults.get(0).getScore()));
		Assert.assertEquals("Socre of second element not normilized correctly", 
				Double.valueOf(1.0), Double.valueOf(mergedResults.get(1).getScore()));
		Assert.assertEquals("Socre of third element not normilized correctly", 
				Double.valueOf(1.0), Double.valueOf(mergedResults.get(2).getScore()));
		Assert.assertEquals("Socre of forth element not normilized correctly", 
				Double.valueOf(2.0/3), Double.valueOf(mergedResults.get(3).getScore()));
		Assert.assertEquals("Socre of last element not normilized correctly", 
				Double.valueOf(0), Double.valueOf(mergedResults.get(4).getScore()));
//test correct rank order		
		Assert.assertEquals("rank of the first element is not correct", 1, mergedResults2.get(0).getRank());
		Assert.assertEquals("rank of the second element is not correct", 2, mergedResults2.get(1).getRank());
		Assert.assertEquals("rank of the third element is not correct", 3, mergedResults2.get(2).getRank());
		Assert.assertEquals("rank of the forth element is not correct", 4, mergedResults2.get(3).getRank());
		Assert.assertEquals("rank of the fifth element is not correct", 5, mergedResults2.get(4).getRank());
		Assert.assertEquals("rank of the six element is not correct", 6, mergedResults2.get(5).getRank());
		Assert.assertEquals("rank of the seven element is not correct", 7, mergedResults2.get(6).getRank());
		Assert.assertEquals("rank of the eight element is not correct", 8, mergedResults2.get(7).getRank());
		Assert.assertEquals("rank of the nine element is not correct", 9, mergedResults2.get(8).getRank());
		Assert.assertEquals("rank of the ten element is not correct", 10, mergedResults2.get(9).getRank());
	}
	
	@Test
	public void testMergeResultsDuplicate()
	{
		List<List<ResultFormat>> queryVariantsResults = new ArrayList<List<ResultFormat>>();
		queryVariantsResults.add(fifthListResultFormat);
		queryVariantsResults.add(sixListResultFormat);
		List<ResultFormat> mergedResults = classUnderTest.mergeResults(queryVariantsResults);
		
		Assert.assertEquals("size of merged list is not correct", 6, mergedResults.size());
		Assert.assertFalse("First document returned is not correct", mergedResults.get(0).equals("66406"));
		Assert.assertFalse("Second document returned is not correct", mergedResults.get(0).equals("52032"));
		Assert.assertFalse("Third document returned is not correct", mergedResults.get(0).equals("59661"));
		Assert.assertFalse("Fourth document returned is not correct", mergedResults.get(0).equals("21150"));
		Assert.assertFalse("Fifth document returned is not correct", mergedResults.get(0).equals("44915"));
		Assert.assertFalse("Six document returned is not correct", mergedResults.get(0).equals("67640"));
		
		
	}
	

}

package technion.ir.se.trec.eval;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.dao.RelevenceType;

public class TrecEvalDataFileTest {
	
	private TrecEvalDataFile classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new TrecEvalDataFile();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddRecordsToQuery() throws Exception {
		QrelsRecord record = new QrelsRecord("200", "db-123", 88, RelevenceType.YES);
		classUnderTest.addRecordsToQuery("200", record);
		
		QueryTrecEvalRecords recordsForQuery = classUnderTest.getRecordsForQuery("200");
		
		QrelsRecord qrelsRecord = recordsForQuery.getRecords().get(0);
		Assert.assertEquals("Returned record is not the same as entered", record, qrelsRecord);
		
		int numberOfReleventRecords = recordsForQuery.getRelevenceRecords().size();
		Assert.assertEquals("There should be <1> relevent records", 1, numberOfReleventRecords);
	}
	
	@Test
	public void testAddRecordsToQuery_notRelevant() throws Exception {
		QrelsRecord record = new QrelsRecord("200", "db-123", 88, RelevenceType.NO);
		classUnderTest.addRecordsToQuery("200", record);
		
		QueryTrecEvalRecords recordsForQuery = classUnderTest.getRecordsForQuery("200");


		QrelsRecord qrelsRecord = recordsForQuery.getRecords().get(0);
		Assert.assertEquals("Returned record is not the same as entered", record, qrelsRecord);
		
		int numberOfReleventRecords = recordsForQuery.getRelevenceRecords().size();
		Assert.assertEquals("There should be <0> relevent records", 0, numberOfReleventRecords);
	}
	
	@Test
	public void testAddRecordsToQuery_hasRecordsForQuery() throws Exception {
		QrelsRecord recordOne = new QrelsRecord("200", "db-123", 88, RelevenceType.NO);
		QrelsRecord recordTwo = new QrelsRecord("200", "xx-923", 8, RelevenceType.YES);
		classUnderTest.addRecordsToQuery("200", recordOne);
		classUnderTest.addRecordsToQuery("200", recordTwo);
		
		QueryTrecEvalRecords recordsForQuery = classUnderTest.getRecordsForQuery("200");
		
		List<QrelsRecord> records = recordsForQuery.getRecords();
		Assert.assertTrue("Returned records is not the same as entered", records.contains(recordOne));
		Assert.assertTrue("Returned records is not the same as entered", records.contains(recordTwo));
		
		int numberOfReleventRecords = recordsForQuery.getRelevenceRecords().size();
		Assert.assertEquals("There should be <1> relevent records", 1, numberOfReleventRecords);
	}
	
	@Test
	public void testAddRecordsToQuery_hasRecordstoTwoQueries() throws Exception {
		QrelsRecord recordOne = new QrelsRecord("200", "db-123", 88, RelevenceType.NO);
		QrelsRecord recordTwo = new QrelsRecord("8", "xx-923", 8, RelevenceType.YES);
		classUnderTest.addRecordsToQuery("200", recordOne);
		classUnderTest.addRecordsToQuery("8", recordTwo);
		
		QueryTrecEvalRecords recordsForQueryOne = classUnderTest.getRecordsForQuery("200");
		QueryTrecEvalRecords recordsForQueryTwo = classUnderTest.getRecordsForQuery("8");
		
		List<QrelsRecord> QueryOneRecords = recordsForQueryOne.getRecords();
		Assert.assertTrue("Returned records is not the same as entered", QueryOneRecords.contains(recordOne));
		List<QrelsRecord> QueryTwoRecords = recordsForQueryTwo.getRecords();
		Assert.assertTrue("Returned records is not the same as entered", QueryTwoRecords.contains(recordTwo));
		
		int numberOfReleventRecordsQOne = recordsForQueryOne.getRelevenceRecords().size();
		Assert.assertEquals("There should be <0> relevent records", 0, numberOfReleventRecordsQOne);
		
		int numberOfReleventRecordsQTwo = recordsForQueryTwo.getRelevenceRecords().size();
		Assert.assertEquals("There should be <1> relevent records", 1, numberOfReleventRecordsQTwo);
	}
}

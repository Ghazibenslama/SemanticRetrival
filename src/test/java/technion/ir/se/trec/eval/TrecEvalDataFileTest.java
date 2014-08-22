package technion.ir.se.trec.eval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.google.common.collect.ImmutableMap;

import technion.ir.se.Types.RelevenceType;
import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

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

		Assert.assertTrue("There shoudln't be any records", recordsForQuery.getRecords().isEmpty());
	}
	
	@Test
	public void testAddRecordsToQuery_hasRecordsForQuery() throws Exception {
		QrelsRecord recordOne = new QrelsRecord("200", "db-123", 88, RelevenceType.NO);
		QrelsRecord recordTwo = new QrelsRecord("200", "xx-923", 8, RelevenceType.YES);
		classUnderTest.addRecordsToQuery("200", recordOne);
		classUnderTest.addRecordsToQuery("200", recordTwo);
		
		QueryTrecEvalRecords recordsForQuery = classUnderTest.getRecordsForQuery("200");
		
		List<QrelsRecord> records = recordsForQuery.getRecords();
		Assert.assertFalse("Returned records should not contain this record", records.contains(recordOne));
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
		Assert.assertFalse("Returned records should not contain that record", QueryOneRecords.contains(recordOne));
		List<QrelsRecord> QueryTwoRecords = recordsForQueryTwo.getRecords();
		Assert.assertTrue("Returned records is not the same as entered", QueryTwoRecords.contains(recordTwo));
		
		int numberOfReleventRecordsQOne = recordsForQueryOne.getRelevenceRecords().size();
		Assert.assertEquals("There should be <0> relevent records", 0, numberOfReleventRecordsQOne);
		
		int numberOfReleventRecordsQTwo = recordsForQueryTwo.getRelevenceRecords().size();
		Assert.assertEquals("There should be <1> relevent records", 1, numberOfReleventRecordsQTwo);
	}
	
	@Test
	public void testIsDocumentRelevent() throws RecordsNotExistsException {
		Set<QrelsRecord> relDocs = generateRelDocs();
		
		classUnderTest = PowerMockito.spy(classUnderTest);
		PowerMockito.doReturn(relDocs).when(classUnderTest).getRelevantDocuments(Mockito.anyString());
		
		QrelsRecord recordUnderTest = new QrelsRecord("xxx", "doc-3", 77, RelevenceType.YES);
		boolean isDocumentRelevent = classUnderTest.isDocumentRelevent(recordUnderTest);
		Assert.assertTrue("replied 'False' for relevent recorf", isDocumentRelevent);
	}
	
	@Test
	public void testIsDocumentRelevent_negative() throws RecordsNotExistsException {
		Set<QrelsRecord> relDocs = generateRelDocs();
		
		classUnderTest = PowerMockito.spy(classUnderTest);
		PowerMockito.doReturn(relDocs).when(classUnderTest).getRelevantDocuments(Mockito.anyString());
		
		QrelsRecord recordUnderTest = new QrelsRecord("xxx", "doc-4", 77, RelevenceType.YES);
		boolean isDocumentRelevent = classUnderTest.isDocumentRelevent(recordUnderTest);
		Assert.assertFalse("replied 'True' for relevent recorf", isDocumentRelevent);
	}
	
	@Test
	public void testGetRankedDocuments_requestMoreDocsThanExists() throws RecordsNotExistsException {
		Map<Integer, QrelsRecord> map = ImmutableMap.of(1, new QrelsRecord("0", "xx", 1, null), 2, new QrelsRecord("0", "xd", 2, null));
		TreeMap<Integer, QrelsRecord> rankedDocuments = new TreeMap<Integer, QrelsRecord>(map);
		
		QueryTrecEvalRecords mock = PowerMockito.mock(QueryTrecEvalRecords.class);
		PowerMockito.when(mock.getRankedDocuments()).thenReturn(rankedDocuments);
		
		classUnderTest = PowerMockito.spy(classUnderTest);
		PowerMockito.doReturn(mock).when(classUnderTest).getRecordsForQuery(Mockito.anyString());
		
		List<QrelsRecord> rankedDocumentsResponse = classUnderTest.getRankedDocuments("0", 5);
		Assert.assertEquals("Result should contain only <2> documents", 2, rankedDocumentsResponse.size());
		Assert.assertEquals("first record doc ID is not as expected", "xx", rankedDocumentsResponse.get(0).getDocumentID());
		Assert.assertEquals("second record doc ID is not as expected", "xd", rankedDocumentsResponse.get(1).getDocumentID());
	}
	
	@Test
	public void testGetRankedDocuments_requestLessDocsThanExists() throws RecordsNotExistsException {
		Map<Integer, QrelsRecord> map = ImmutableMap.of(1, new QrelsRecord("0", "xx", 1, null), 2, new QrelsRecord("0", "xd", 2, null),
			3, new QrelsRecord("0", "xd", 3, null));
		TreeMap<Integer, QrelsRecord> rankedDocuments = new TreeMap<Integer, QrelsRecord>(map);
		
		QueryTrecEvalRecords mock = PowerMockito.mock(QueryTrecEvalRecords.class);
		PowerMockito.when(mock.getRankedDocuments()).thenReturn(rankedDocuments);
		
		classUnderTest = PowerMockito.spy(classUnderTest);
		PowerMockito.doReturn(mock).when(classUnderTest).getRecordsForQuery(Mockito.anyString());
		
		List<QrelsRecord> rankedDocumentsResponse = classUnderTest.getRankedDocuments("0", 1);
		Assert.assertEquals("Result should contain only <1> documents", 1, rankedDocumentsResponse.size());
		Assert.assertEquals("first record doc ID is not as expected", "xx", rankedDocumentsResponse.get(0).getDocumentID());
	}
	
	private Set<QrelsRecord> generateRelDocs() {
		List<QrelsRecord> list = Arrays.asList(new QrelsRecord("mock_query_id","doc-1", -1, RelevenceType.YES), 
				new QrelsRecord("mock_query_id","doc-2", -1, RelevenceType.YES),
				new QrelsRecord("mock_query_id","doc-3", -1, RelevenceType.YES));
		return new HashSet<QrelsRecord>(list);
	}
}

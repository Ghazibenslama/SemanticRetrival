package technion.ir.se.trec.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import technion.ir.se.Types.RelevenceType;
import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

public class AveragePercisionCalculatorTest {
	private AveragePercisionCalculator classUnderTest;
	private TrecEvalDataFile qerls;
	private TrecEvalDataFile goldResults;

	@Before
	public void setUp() throws Exception {
		qerls = PowerMockito.mock(TrecEvalDataFile.class);
		goldResults = PowerMockito.mock(TrecEvalDataFile.class);
		classUnderTest = new AveragePercisionCalculator(qerls, goldResults);
	}

	@After
	public void tearDown() throws Exception {
	}

	private List<QrelsRecord> generateRankedDocuments(String queryID) {
		List<QrelsRecord> asList = Arrays.asList(new QrelsRecord(queryID,"doc-1", 1, RelevenceType.YES), 
				new QrelsRecord(queryID,"doc-8", 2, RelevenceType.YES),
				new QrelsRecord(queryID,"doc-3", 3, RelevenceType.YES));
		return new ArrayList<QrelsRecord>(asList);
		
	}
	
	@Test
	public void testCalcAveragePercisionSocre_posNegPos() throws RecordsNotExistsException {
		String queryID = "5";
		PowerMockito.when(qerls.getRankedDocuments(Mockito.eq(queryID), Mockito.anyInt())).thenReturn( generateRankedDocuments(queryID) );
		PowerMockito.when(goldResults.isDocumentRelevent(Mockito.any(QrelsRecord.class))).thenReturn(true, false, true);
		PowerMockito.when(goldResults.getNumberOfRelevantDocuments(queryID)).thenReturn( 3 );
		PowerMockito.when(qerls.getNumberOfDocuments(queryID)).thenReturn( 3 );
		
		double APSocre = classUnderTest.calcAveragePercisionSocre(queryID);
		Assert.assertEquals("Avreage Percision score should be <0.555>", 0.55555, APSocre, 0.0001);
	}
	
	@Test
	public void testCalcAveragePercisionSocre_posPosNeg() throws RecordsNotExistsException {
		String queryID = "5";
		PowerMockito.when(qerls.getRankedDocuments(Mockito.eq(queryID), Mockito.anyInt())).thenReturn( generateRankedDocuments(queryID) );
		PowerMockito.when(goldResults.isDocumentRelevent(Mockito.any(QrelsRecord.class))).thenReturn(true, true, false);
		PowerMockito.when(qerls.getNumberOfDocuments(queryID)).thenReturn( 3 );
		
		double APSocre = classUnderTest.calcAveragePercisionSocre(queryID);
		Assert.assertEquals("Avreage Percision score should be <0.6666>", 0.6666, APSocre, 0.0001);
	}
	
	@Test
	public void testCalcAveragePercisionSocre_negPosNeg() throws RecordsNotExistsException {
		String queryID = "5";
		PowerMockito.when(qerls.getRankedDocuments(Mockito.eq(queryID), Mockito.anyInt())).thenReturn( generateRankedDocuments(queryID) );
		PowerMockito.when(goldResults.isDocumentRelevent(Mockito.any(QrelsRecord.class))).thenReturn(false, true, false);
		PowerMockito.when(qerls.getNumberOfDocuments(queryID)).thenReturn( 3 );
		
		double APSocre = classUnderTest.calcAveragePercisionSocre(queryID);
		Assert.assertEquals("Avreage Percision score should be <0.1666>", 0.1666, APSocre, 0.0001);
	}
	
	@Test
	public void testCalcAveragePercisionSocre_allNeg() throws RecordsNotExistsException {
		String queryID = "5";
		PowerMockito.when(qerls.getRankedDocuments(Mockito.eq(queryID), Mockito.anyInt())).thenReturn( generateRankedDocuments(queryID) );
		PowerMockito.when(goldResults.isDocumentRelevent(Mockito.any(QrelsRecord.class))).thenReturn(false, false, false);
		PowerMockito.when(qerls.getNumberOfDocuments(queryID)).thenReturn( 3 );
		
		double APSocre = classUnderTest.calcAveragePercisionSocre(queryID);
		Assert.assertEquals("Avreage Percision score should be <0>", 0, APSocre, 0.000);
	}
	
	@Test
	public void testCalcAveragePercisionSocre_allPos() throws RecordsNotExistsException {
		String queryID = "5";
		PowerMockito.when(qerls.getRankedDocuments(Mockito.eq(queryID), Mockito.anyInt())).thenReturn( generateRankedDocuments(queryID) );
		
		PowerMockito.when(goldResults.isDocumentRelevent(Mockito.any(QrelsRecord.class))).thenReturn(true, true, true);
		PowerMockito.when(qerls.getNumberOfDocuments(queryID)).thenReturn( 3 );
		
		double APSocre = classUnderTest.calcAveragePercisionSocre(queryID);
		Assert.assertEquals("Avreage Percision score should be <0>", 1, APSocre, 0.000);
	}
	
	@Test
	public void testGetNumberOfReleventRetrivedResults_noMatching() throws RecordsNotExistsException {
		QueryTrecEvalRecords mockQueryTrecEvalRecords = PowerMockito.mock(QueryTrecEvalRecords.class);
		PowerMockito.when(mockQueryTrecEvalRecords.getRecords()).thenReturn( this.generateRankedDocuments("1") );
		PowerMockito.when(qerls.getRecordsForQuery(Mockito.anyString())).thenReturn(mockQueryTrecEvalRecords);
		
		PowerMockito.when(goldResults.getRelevantDocuments(Mockito.anyString())).thenReturn(new HashSet<QrelsRecord>( this.generateRankedDocuments("3") ));
		
		int retrivedDocsThatRelevent = classUnderTest.getNumberOfReleventRetrivedResults("1");
		Assert.assertEquals("There should be <0> relevent docs", 0, retrivedDocsThatRelevent);
	}
	
	@Test
	public void testGetNumberOfReleventRetrivedResults_allMatching() throws RecordsNotExistsException {
		QueryTrecEvalRecords mockQueryTrecEvalRecords = PowerMockito.mock(QueryTrecEvalRecords.class);
		PowerMockito.when(mockQueryTrecEvalRecords.getRecords()).thenReturn( this.generateRankedDocuments("1") );
		PowerMockito.when(qerls.getRecordsForQuery(Mockito.anyString())).thenReturn(mockQueryTrecEvalRecords);
		
		PowerMockito.when(goldResults.getRelevantDocuments(Mockito.anyString())).thenReturn(new HashSet<QrelsRecord>( this.generateRankedDocuments("1") ));
		
		int retrivedDocsThatRelevent = classUnderTest.getNumberOfReleventRetrivedResults("1");
		Assert.assertEquals("There should be <3> relevent docs", 3, retrivedDocsThatRelevent);
	}
	
	

}

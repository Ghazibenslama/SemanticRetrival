package technion.ir.se.trec.eval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.dao.RelevenceType;
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

	private HashSet<QrelsRecord> generateRelDocs(String queryID) {
		List<QrelsRecord> list = Arrays.asList(new QrelsRecord(queryID,"doc-1", -1, RelevenceType.YES), 
				new QrelsRecord(queryID,"doc-2", -1, RelevenceType.YES),
				new QrelsRecord(queryID,"doc-3", -1, RelevenceType.YES));
		return new HashSet<QrelsRecord>(list);
	}
	
	private List<QrelsRecord> generateRankedDocuments(String queryID) {
		return Arrays.asList(new QrelsRecord(queryID,"doc-1", 1, RelevenceType.YES), 
				new QrelsRecord(queryID,"doc-8", 2, RelevenceType.YES),
				new QrelsRecord(queryID,"doc-3", 3, RelevenceType.YES));
		
	}
	
	@Test
	public void testCalcAveragePercisionSocre() throws RecordsNotExistsException {
		String queryID = "5";
		PowerMockito.when(qerls.getRankedDocuments(queryID)).thenReturn( generateRankedDocuments(queryID) );
		PowerMockito.when(goldResults.getRelevantDocuments(queryID)).thenReturn( generateRelDocs(queryID) );
		double APSocre = classUnderTest.calcAveragePercisionSocre(queryID);
		Assert.assertEquals("Avreage Percision score should be <0.555>", 0.555, APSocre, 0.000);
	}

}

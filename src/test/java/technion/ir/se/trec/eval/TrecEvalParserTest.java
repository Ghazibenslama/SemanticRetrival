package technion.ir.se.trec.eval;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import technion.ir.se.Types.RelevenceType;
import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.IllegalLineLength;

public class TrecEvalParserTest {
	
	private TrecEvalParser classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new TrecEvalParser();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseLine_fourArgs() throws Exception {
		String fourArgumentsLine = "301 0 FBIS3-10169 0";
		QrelsRecord qrelsRecord = Whitebox.<QrelsRecord>invokeMethod(classUnderTest, "parseLine", fourArgumentsLine);
		Assert.assertEquals("Query ID is not as expected", "301", qrelsRecord.getQueryID());
		Assert.assertEquals("Document ID is not as expected", "FBIS3-10169", qrelsRecord.getDocumentID());
		Assert.assertEquals("Rank is not as expected", Integer.valueOf(-1), qrelsRecord.getRank());
		Assert.assertEquals("Query ID is not as expected", RelevenceType.NO, qrelsRecord.isRelevence());
	}
	
	@Test
	public void testParseLine_sixArgs() throws Exception {
		String fourArgumentsLine = "309 Q0 FBIS4-41991 2 -5.7600 Indri";
		QrelsRecord qrelsRecord = Whitebox.<QrelsRecord>invokeMethod(classUnderTest, "parseLine", fourArgumentsLine);
		Assert.assertEquals("Query ID is not as expected", "309", qrelsRecord.getQueryID());
		Assert.assertEquals("Document ID is not as expected", "FBIS4-41991", qrelsRecord.getDocumentID());
		Assert.assertEquals("Rank is not as expected", Integer.valueOf(2), qrelsRecord.getRank());
		Assert.assertEquals("Query ID is not as expected", RelevenceType.YES, qrelsRecord.isRelevence());
	}
	
	@Test (expected = IllegalLineLength.class)
	public void testParseLine_illegalArgs() throws Exception {
		String fourArgumentsLine = "309 Q0 FBIS4-41991 2 -5.7600";
		Whitebox.<QrelsRecord>invokeMethod(classUnderTest, "parseLine", fourArgumentsLine);
	}
}

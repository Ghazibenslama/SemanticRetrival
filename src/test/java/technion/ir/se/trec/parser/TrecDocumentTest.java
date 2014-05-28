package technion.ir.se.trec.parser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import technion.ir.se.TestUtils;

public class TrecDocumentTest {

	private TrecDocument document;
	@Before
	public void setUp() throws Exception {
		document = new TrecDocument("mockNumber");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteFileDisk() throws Exception {
		StringBuilder mockBuilder = new StringBuilder("Mocked content");
		Whitebox.setInternalState(document, "content", mockBuilder);
		Whitebox.invokeMethod(document, "writeFileDisk", "c:/Temp/IR");
		
		String fileContnet = TestUtils.getFileContent("c:/Temp/IR/mockNumber.txt");
		
		Assert.assertTrue("File in disk doesn't contain content", fileContnet.contains("Mocked content"));
	}

}

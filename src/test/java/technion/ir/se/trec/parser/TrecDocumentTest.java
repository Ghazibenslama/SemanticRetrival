package technion.ir.se.trec.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

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
		
		File createdFile = new File("c:/Temp/IR/mockNumber.txt");
		createdFile.deleteOnExit();
		BufferedReader br = new BufferedReader(new FileReader(createdFile));
		String fileContnet = IOUtils.toString(br);
		
		Assert.assertTrue("File in disk doesn't contain content", fileContnet.contains("Mocked content"));
		br.close();
	}

}

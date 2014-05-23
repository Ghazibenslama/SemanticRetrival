package technion.ir.se.trec.parser;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class TrecDocumentCreatorTest {

	private TrecDocumentCreator creator;
	@Before
	public void setUp() throws Exception {
		creator = new TrecDocumentCreator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllFilesNames() throws FileNotFoundException, URISyntaxException {
		URL dirUrl = getClass().getResource("/TrecDocuments");
		File dir = new File(dirUrl.toURI());
		
		List<File> filesNames = creator.getAllFilesNames(dir.getAbsolutePath());
		assertTrue("didn't get 5 documents", filesNames.size() == 5);
		ArrayList<String> expectedFileNames = new ArrayList<String>(Arrays.asList("FB396001", "FB396002", "FB396003","FB396004", "FB396005"));
		for (File file : filesNames) {
			String fileName = file.getName();
			assertTrue("didn't parsed all files names", expectedFileNames.contains(fileName));
		}
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testGetAllFilesNames_throwException() throws FileNotFoundException {
		creator.getAllFilesNames("c:\\xxxxxxxxxx");
	}
	
	@Test
	public void tetstCreateFile() throws Exception {
		String line = "<DOCNO> validNumber </DOCNO>";
		TrecDocument trecDocument = Whitebox.<TrecDocument>invokeMethod(creator, "createFile", line);
		Assert.assertNotNull(trecDocument);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void tetstCreateFile_NoValidTag() throws Exception {
		String line = "NotvalidNumber";
		Whitebox.<TrecDocument>invokeMethod(creator, "createFile", line);
	}
	
	@Test
	public void testConvertTrecDocument() throws IOException, URISyntaxException {
		URL dirUrl = getClass().getResource("/TrecDocuments/FB396001");
		File fileToConvert = new File(dirUrl.toURI());
		List<File> trecFiles = creator.convertTrecDocument(fileToConvert);
		for (File file : trecFiles) {
			file.deleteOnExit();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			StringBuilder builder = new StringBuilder();
			while (line != null) {
				builder.append(line);
				builder.append("\n");
				line = br.readLine();
			}
			String fileContnet = br.toString();
			String testPhrase = "<TEXT>";
			testPhraseAppeasesOnce(fileContnet, testPhrase);
			testPhrase = "</TEXT>";
			testPhraseAppeasesOnce(fileContnet, testPhrase);
			testPhrase = "<DOC>";
			testPhraseAppeasesOnce(fileContnet, testPhrase);
			testPhrase = "</DOC>";
			testPhraseAppeasesOnce(fileContnet, testPhrase);
			testPhrase = "<DOCNO>";
			testPhraseAppeasesOnce(fileContnet, testPhrase);
			testPhrase = "</DOCNO>";
			testPhraseAppeasesOnce(fileContnet, testPhrase);
			br.close();
		}
	}

	private void testPhraseAppeasesOnce(String fileContnet, String testPhrase) {
		assertTrue(String.format("%s appears more than once",testPhrase) , isTestPhraseAppearsOnce(fileContnet, testPhrase));
	}

	private boolean isTestPhraseAppearsOnce(String fileContnet, String testPhrase) {
		return StringUtils.lastIndexOf(fileContnet, testPhrase) == fileContnet.indexOf(testPhrase);
	}

}

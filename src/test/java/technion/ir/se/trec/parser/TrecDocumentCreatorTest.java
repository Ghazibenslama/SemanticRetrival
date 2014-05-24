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

import org.apache.commons.io.IOUtils;
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
		File dir = getTestDocumentsFolder();
		
		List<File> filesNames = creator.getAllFilesNames(dir.getAbsolutePath());
		for (File file : filesNames) {
			if (file.getName().contains(".txt")) {
				file.delete();
			}
		}
	}

	private File getTestDocumentsFolder() throws URISyntaxException {
		URL dirUrl = getClass().getResource("/TrecDocuments");
		File dir = new File(dirUrl.toURI());
		return dir;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllFilesNames() throws FileNotFoundException, URISyntaxException {
		File dir = getTestDocumentsFolder();
		
		List<File> filesNames = creator.getAllFilesNames(dir.getAbsolutePath());
		assertTrue(String.format("got %s instead of 5 documents",filesNames.size()), filesNames.size() == 5);
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
		Assert.assertTrue("didn't create all 49 documents", trecFiles.size() == 49);
		for (File file : trecFiles) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String fileContnet = IOUtils.toString(br);
			List<String> list = Arrays.asList("<TEXT>", "</TEXT>","<DOC>", "</DOC>", "<DOCNO>", "</DOCNO>");
			for (String testPhrase : list) {
				assertTrue(String.format("%s not exists in document", testPhrase), fileContnet.contains(testPhrase));
				testPhraseAppeasesOnce(fileContnet, testPhrase);
			}
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

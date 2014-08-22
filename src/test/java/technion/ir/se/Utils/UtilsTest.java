package technion.ir.se.Utils;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import technion.ir.se.TestUtils;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.ResultFormat;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.exception.FileNameNotExtracted;

@PrepareForTest(Utils.class)
@RunWith(PowerMockRunner.class)
public class UtilsTest {

	@Before
	public void setUp() throws Exception {
	}
	

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadQueries() throws IOException, URISyntaxException {
		List<Query> queries = Utils.readQueries();
		assertTrue("Doesn't contain 2 queries", queries.size() == 2);
		
		assertTrue("first query text is wrong", queries.get(0).getQueryText().equals("international organized crime"));
		assertTrue("first query text is wrong", queries.get(1).getQueryText().equals("foreign minorities germany"));
		
		assertTrue("first query text is wrong", queries.get(0).getId().equals("301"));
		assertTrue("first query text is wrong", queries.get(1).getId().equals("401"));
	}
	
	@Test
	public void testwriteMapFile() throws IOException{
		
		StringBuilder stringBuilder = new StringBuilder("Hello world");
		Utils.writeFile(stringBuilder,"prefix_baseLineMap", ".res");
		String fileContnet = TestUtils.getFileContent("prefix_baseLineMap.res");
		assertTrue("file content not correct", fileContnet.equals("Hello world"));
	}
	
	@Test
	public void testreadProperty()
	{
		String result;
		result = Utils.readProperty("Eilon");
		assertTrue("The returned result key property is not correct", result.equals("King"));
	}
	
	@Test
	public void testconvertRetrivalResultListToResultFormatList() throws FileNameNotExtracted
	{
		RetrivalResult retResult1 = new RetrivalResult(1, 2, 0, 30, 5, 5, 5, "\\SecondDoc.txt");
		RetrivalResult retResult2 = new RetrivalResult(0.5, 2, 0, 30, 5, 5, 5, "\\FirstDoc.txt");
		RetrivalResult retResult3 = new RetrivalResult(0.3, 2, 0, 30, 5, 5, 5, "\\ThirdDoc.txt");
		RetrivalResult retResult4 = new RetrivalResult(0, 2, 0, 30, 5, 5, 5, "\\LastDoc.txt");
		List<RetrivalResult> retResultList = new ArrayList<RetrivalResult>();
		retResultList.add(retResult1);
		retResultList.add(retResult2);
		retResultList.add(retResult3);
		retResultList.add(retResult4);
		
		Query query = new Query("1","bla");
		
		List<ResultFormat> retFormat = new ArrayList <ResultFormat>();
		retFormat = Utils.convertRetrivalResultListToResultFormatList(retResultList, query);
		Assert.assertEquals("first Doc is not correct", "SecondDoc", retFormat.get(0).getDocumentID());
		Assert.assertEquals("Second Doc is not correct", "FirstDoc", retFormat.get(1).getDocumentID());
		Assert.assertEquals("Third Doc is not correct", "ThirdDoc", retFormat.get(2).getDocumentID());
		Assert.assertEquals("Last Doc is not correct", "LastDoc", retFormat.get(3).getDocumentID());
		Assert.assertEquals("queryId is not correct", "1", retFormat.get(0).getQueryID());
		Assert.assertEquals("Score of second doc not correct", 0.5, retFormat.get(1).getScore(), 0);
	}
	
	@Test
	public void testConvertPathToExistingPath() {
		String path = "jar:file:/C:/Users/XPS_Sapir/Documents/GitHub/SemanticRetrival/target/SemanticRetrival-0.0.1-jar-with-dependencies.jar!/queries.txt";
		String existingPath = Utils.convertPathToExistingPath(path, "/queries.txt");
		org.junit.Assert.assertEquals("path not OK", "C:/Users/XPS_Sapir/Documents/GitHub/SemanticRetrival/target/test-classes/queries.txt", existingPath);
	}
}

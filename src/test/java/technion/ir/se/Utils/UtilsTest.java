package technion.ir.se.Utils;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import technion.ir.se.TestUtils;
import technion.ir.se.dao.Query;

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
		Utils.writeMapFile(stringBuilder);
		String fileContnet = TestUtils.getFileContent("baseLineMap.res");
		assertTrue("file content not correct", fileContnet.equals("Hello world"));
		
		
	}

}

package ShellCommands;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BuildIndexCommandTest {

	private BuildIndexCommand buildIndex; 
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteCommand() {
		buildIndex = new BuildIndexCommand("IndriBuildIndex.exe", "buildIndex.xml");
		String output = buildIndex.executeCommand(buildIndex.get_command());
		assertTrue("Index was not created", output.contains("Opened repository index"));
		assertTrue("Index was not created", output.contains("Opened document/FB396001.txt"));
		assertTrue("Index was not created", output.contains("Documents parsed: 1 Documents indexed: 0"));
		assertTrue("Index was not created", output.contains("Closed document/FB396001.txt"));
		assertTrue("Index was not created", output.contains("Closing index"));
		assertTrue("Index was not created", output.contains("Finished"));
	}
	
	@Test
	public void testExecuteCommand_WrongIndexSettingFile() {
		buildIndex = new BuildIndexCommand("IndriBuildIndex.exe", "build.xml");
		String output = buildIndex.executeCommand(buildIndex.get_command());
		assertFalse("Index was not supposed to be created", output.contains("Opened repository index"));
	}

}

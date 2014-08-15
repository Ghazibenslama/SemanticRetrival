package technion.ir.se.Utils;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLog4j {
	
    static final Logger logger = Logger.getLogger(TestLog4j.class);


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
        logger.debug("Prining in Debug");
        logger.error("Prining in error");
        logger.fatal("Prining in fatal");
        logger.info("Prining in info");
        logger.trace("Prining in trace");
	}

}

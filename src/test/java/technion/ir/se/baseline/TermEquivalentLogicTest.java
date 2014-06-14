package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;

public class TermEquivalentLogicTest {
	private TermEquivalentLogic classUnderTest;
	private double[] qTermVector;
	private int[] firstTermVector;
	private int[] secondTermVector;
	private int[] thirdTermVector;
	Map <String,double[]> documentTerms;
	
	@Before
	public void setUp() throws Exception 
	{
		classUnderTest = new TermEquivalentLogic();
		qTermVector = new double[] {2.0,1.0,0.0};
		firstTermVector = new int[] {3,0,0};
		secondTermVector = new int[] {2,1,1};
		thirdTermVector = new int[] {1,1,1};
		documentTerms = new TreeMap<String, double[]>();
		documentTerms.put("t1", Utils.convertIntArrtoDoubleArr(firstTermVector));
		documentTerms.put("t2", Utils.convertIntArrtoDoubleArr(secondTermVector));
		documentTerms.put("t3", Utils.convertIntArrtoDoubleArr(thirdTermVector));
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTermEquivalentLogic() throws VectorLengthException 
	{
		List<SemanticTermScore> termScore = new ArrayList<SemanticTermScore>();
		termScore = classUnderTest.similarVectors(documentTerms, qTermVector);
		Assert.assertEquals("result contains 3 items", 3, termScore.size());
		Assert.assertEquals("First argument is not correct", "t2", termScore.get(0).getTerm());
		Assert.assertEquals("last argument is not correct", "t3", termScore.get(2).getTerm());
		Assert.assertFalse("First Score not in range", termScore.get(1).getSemanticScore() < 0.89 ||
														termScore.get(1).getSemanticScore() > 0.895 );
		
		Assert.assertFalse("Second Score not in range", termScore.get(0).getSemanticScore() < 0.91 ||
														termScore.get(0).getSemanticScore() > 0.92 );
		
		Assert.assertFalse("Thirs Score not in range", termScore.get(2).getSemanticScore() < 0.77 ||
														termScore.get(2).getSemanticScore() > 0.775 );
		
		

	}

}

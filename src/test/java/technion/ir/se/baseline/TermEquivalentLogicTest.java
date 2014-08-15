package technion.ir.se.baseline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import technion.ir.se.Model.Model;
import technion.ir.se.dao.SemanticTermScore;
import technion.ir.se.exception.VectorLengthException;

import com.google.common.collect.ImmutableMap;

@PrepareForTest(Model.class)
@RunWith(PowerMockRunner.class)
public class TermEquivalentLogicTest {
	private TermEquivalentLogic classUnderTest;
	private static final String ALEF = "alef";
	private static final String BETH = "beth";
	private static final String GIMEL = "gimel";
	private static final String DALED = "Daled";
	private static final String HEI = "Hei";
	private static final String QUERY_TERM = "q";
	private Map<String, Map<String, Short>> outerMap;
	
	@Before
	public void setUp() throws Exception 
	{
		classUnderTest = new TermEquivalentLogic();
		outerMap = new HashMap<String, Map<String,Short>>();
		
		List<String> list = Arrays.asList(ALEF, BETH, GIMEL, DALED, HEI);
		Model mockedModel = PowerMockito.mock(Model.class);
		PowerMockito.when(mockedModel.getModel()).thenReturn(list);
		
		PowerMock.mockStatic(Model.class);
		EasyMock.expect(Model.getInstance()).andReturn(mockedModel).anyTimes();
		PowerMock.replay(Model.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTermEquivalentLogic() throws VectorLengthException 
	{
		outerMap.put(QUERY_TERM, ImmutableMap.of(ALEF, (short)2, BETH, (short)1));
		outerMap.put(ALEF, ImmutableMap.of(ALEF, (short) 3, BETH, (short)0, GIMEL, (short)0));
		outerMap.put(BETH, ImmutableMap.of(ALEF, (short) 2, BETH, (short)1, GIMEL, (short)1));
		outerMap.put(GIMEL, ImmutableMap.of(ALEF, (short) 1, BETH, (short)1, GIMEL, (short)1));
		
		List<SemanticTermScore> termScore = classUnderTest.similarVectors(outerMap, QUERY_TERM);
		Assert.assertEquals("result contains 3 items", 3, termScore.size());
		Assert.assertEquals("First argument is not correct", BETH, termScore.get(0).getTerm());
		Assert.assertEquals("last argument is not correct", GIMEL, termScore.get(2).getTerm());
		Assert.assertFalse("First Score not in range", termScore.get(1).getSemanticScore() < 0.89 ||
														termScore.get(1).getSemanticScore() > 0.895 );
		
		Assert.assertFalse("Second Score not in range", termScore.get(0).getSemanticScore() < 0.91 ||
														termScore.get(0).getSemanticScore() > 0.92 );
		
		Assert.assertFalse("Thirs Score not in range", termScore.get(2).getSemanticScore() < 0.77 ||
														termScore.get(2).getSemanticScore() > 0.775 );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTermEquivalentLogic_ScoreNan() throws VectorLengthException 
	{
		outerMap.put(QUERY_TERM, ImmutableMap.of(ALEF, (short)2, BETH, (short)1));
		outerMap.put(ALEF, ImmutableMap.of(ALEF, (short) 3, BETH, (short)0, GIMEL, (short)0));
		outerMap.put(BETH, ImmutableMap.of(ALEF, (short) 2, BETH, (short)1, GIMEL, (short)1));
		outerMap.put(GIMEL, ImmutableMap.of(ALEF, (short) 1, BETH, (short)1, GIMEL, (short)1));
		
		SimilarityLogic similarityLogic = PowerMockito.mock(SimilarityLogic.class);
		PowerMockito.when(similarityLogic.calculateSimilarity(Mockito.anyMap(), Mockito.anyMap())).thenReturn(Double.NaN);
		Whitebox.setInternalState(classUnderTest, "similarityLogic", similarityLogic);
		
		List<SemanticTermScore> termScore = classUnderTest.similarVectors(outerMap, QUERY_TERM);
		
		Assert.assertTrue("a SemanticTermScore with soce of 'Nan' was inserted to list", termScore.isEmpty());
	}
}

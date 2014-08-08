package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.List;

import technion.ir.se.dao.SemanticTermScore;

public class AlternativesLogic {

	private static final int ONE_ALTERNATIVES = 1;
	private static final int TWO_ALTERNATIVES = 2;
	private static final int THREE_ALTERNATIVES = 3;
	private static final int FIVE_ALTERNATIVES = 5;
	public List<String> getTermAlternatives(List<SemanticTermScore> similarity, int size) {
		switch (size) {
		case 1:
		case 2:
			return this.fetchAlternatives(similarity, FIVE_ALTERNATIVES);
		case 3:
			return this.fetchAlternatives(similarity, THREE_ALTERNATIVES);
		case 4:
		case 5:
			return this.fetchAlternatives(similarity, TWO_ALTERNATIVES);
		default:
			return this.fetchAlternatives(similarity, ONE_ALTERNATIVES);
		}
		
	}
	
	/**
	 * The method return a {@link List} of alternatives that should be used.<br>
	 * Number of alternatives is the <b>minimum</b> between that value of <code>size</code> parameter 
	 * and the size of <code>terms</code> parameter
	 * 
	 * @param terms
	 * @param size
	 * @return
	 */
	private List<String> fetchAlternatives(List<SemanticTermScore> terms, int size) {
		int numberOfAlternatives = Math.min(size, terms.size());
		ArrayList<String> resultList = new ArrayList<String>(numberOfAlternatives);
		List<SemanticTermScore> subList = terms.subList(0, numberOfAlternatives);
		for (SemanticTermScore semanticTermScore : subList) {
			resultList.add( semanticTermScore.getTerm() );
		}
		return resultList;
		
	}
}

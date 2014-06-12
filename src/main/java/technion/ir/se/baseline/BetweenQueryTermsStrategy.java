package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWidow;

public class BetweenQueryTermsStrategy extends AbstractStrategy {

	private static final int DELIMITER = 1;

	@Override
	public List<TextWidow> getWindows(Feedback feedback, Query query) {
		ArrayList<TextWidow> windows = new ArrayList<TextWidow>();
		TreeSet<String> queryTerms = new TreeSet<String>(query.getQueryTerms());
		List<String> terms = feedback.getTerms();
		int windowStart = 0, windowEnd = 0; 
		
		while (windowEnd != (terms.size()-1)) {
			windowEnd = calcWindowEnd(queryTerms, terms, windowStart);
			windows.add(new TextWidow(windowStart, windowEnd));
			windowStart = windowEnd + 1;
		}
		return windows;
	}

	private int calcWindowEnd(Set<String> queryTerms, List<String> terms, int windowStart) {
		List<Integer> list = new ArrayList<Integer>();
		int startSerachIndex = windowStart + DELIMITER;
		List<String> subList = terms.subList(startSerachIndex, terms.size());
		for (String queryTerm : queryTerms) {
			list.add( subList.indexOf(queryTerm.toLowerCase()) + startSerachIndex );
		}
		Integer windowEnd = getIndexNotLessThan(list, windowStart);
		windowEnd = (windowEnd == null) ? terms.size()-1 : windowEnd;
		return windowEnd;
	}

	/**
	 * Given a list of integers, the method search for the first element in the list that is equal or greater
	 * than <b>windowStart</b>.<br>
	 * If none exists than return <code>null</code>.<br>
	 * 
	 * The method doesn't assume the list is sorted
	 * @param list
	 * @param windowStart
	 * @return The first element equal or greater than <code>windowStart</code> or <code>null</code> if none exists.
	 */
	private Integer getIndexNotLessThan(List<Integer> list, int windowStart) {
		Collections.sort(list);
		for (Integer index : list) {
			if (index > windowStart) {
				return index;
			}
		}
		return null;
	}

	@Override
	public void setWindowSize(int windowSize) {
		throw new IllegalAccessError("Cannot set fixed window size for: 'BetweenQueryTermsStrategy'");
	}

}

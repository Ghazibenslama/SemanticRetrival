package technion.ir.se.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;

public class BetweenQueryTermsStrategy extends AbstractStrategy {

	@Override
	public List<TextWindow> getWindows(Feedback feedback, Query query) {
		ArrayList<TextWindow> windows = new ArrayList<TextWindow>();
		TreeSet<String> queryTerms = new TreeSet<String>(query.getQueryTerms());
		List<String> terms = feedback.getTerms();
		int windowStart = 0, windowEnd = 0; 
		
		while (windowEnd != (terms.size()-1)) {
			windowEnd = calcWindowEnd(queryTerms, terms, windowStart);
			windows.add(new TextWindow(windowStart, windowEnd));
			windowStart = windowEnd + 1;
		}
		return windows;
	}

	private int calcWindowEnd(Set<String> queryTerms, List<String> terms, int windowStart) {
		List<Integer> list = new ArrayList<Integer>();
		int startSerachIndex = windowStart;
		List<String> subList = createSublistStartingCurrentTerm(terms, startSerachIndex);
		for (String queryTerm : queryTerms) {
			list.add( calculateQueryTermPos(startSerachIndex, subList, queryTerm) );
		}
		Integer windowEnd = getIndexNotLessThan(list, windowStart);
		windowEnd = (windowEnd == null) ? terms.size()-1 : windowEnd;
		return windowEnd;
	}

	private int calculateQueryTermPos(int startSerachIndex, List<String> subList,
			String queryTerm) {
		return subList.indexOf(queryTerm.toLowerCase()) + startSerachIndex;
	}

	private List<String> createSublistStartingCurrentTerm(List<String> terms, int startSerachIndex) {
		return terms.subList(startSerachIndex, terms.size());
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
			if (index >= windowStart) {
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

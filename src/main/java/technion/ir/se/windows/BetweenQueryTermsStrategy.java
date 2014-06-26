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
		int lastTermInFeedbackIndex = feedback.getTerms().size()-1;
		int windowStart = 0, windowEnd = 0; 
		
		while (windowEnd != lastTermInFeedbackIndex) {
			windowEnd = calcWindowEnd(queryTerms, feedback, windowStart);
			if (windowEnd != -1) {
				windows.add(new TextWindow(windowStart, windowEnd));
				windowStart = windowEnd + 1;
			} else {
				windowStart = feedback.getNextDocumentStartingIndex(windowStart);
				if (windowStart == -1) {
					break;
				}
			}
		}
		return windows;
	}

	/**
	 * Return the end Index of the window
	 * In case windowsEnd and windowStart are in different documents the returned value is -1
	 * @param queryTerms
	 * @param feedback
	 * @param windowStart
	 * @return
	 */
	private int calcWindowEnd(Set<String> queryTerms, Feedback feedback, int windowStart) {
		List<Integer> list = new ArrayList<Integer>();
		int startSerachIndex = windowStart;
		List<String> terms = feedback.getTerms();
		List<String> subList = createSublistStartingCurrentTerm(terms , startSerachIndex);
		for (String queryTerm : queryTerms) {
			list.add( calculateQueryTermPos(startSerachIndex, subList, queryTerm) );
		}
		Integer windowEnd = getIndexNotLessThan(list, windowStart);
		windowEnd = (windowEnd == null) ? terms.size()-1 : windowEnd;
		boolean indexesInSameDoc = this.doesIndexesInsameDocument(windowStart, windowEnd, feedback);
		return indexesInSameDoc ? windowEnd : -1;
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

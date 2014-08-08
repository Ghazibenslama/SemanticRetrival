package technion.ir.se.windows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;

public class BetweenQueryTermsStrategy extends AbstractStrategy {

	private static final Integer TERMS_NOT_IS_THE_SAME_DOCUMENT = -1;
	private static final Integer DIDNOT_FIND_SUCH_TERM = -1;

	/* (non-Javadoc)
	 * @see technion.ir.se.windows.IWindowSize#getWindows(technion.ir.se.dao.Feedback, technion.ir.se.dao.Query)
	 * The method creates windows in a document only if it contains 2 or more query terms.
	 * The windows are created between the query terms.
	 * 
	 * e.g.: if three query terms appear in a document in the following order: q1, q2 & q3
	 * there will be two windows as followed:
	 * 1) window #1 starting at q1 and ends at q2 (including).
	 * 2) window #2 starting at q2 and ends at q3 (including).
	 */
	@Override
	public List<TextWindow> getWindows(Feedback feedback, Query query) {
		ArrayList<TextWindow> windows = new ArrayList<TextWindow>();
		TreeSet<String> queryTerms = new TreeSet<String>(query.getQueryTerms());
		int lastTermInFeedbackIndex = feedback.getTerms().size()-1;
		int windowStart = 0, windowEnd = 0;
		int startSearchQueryIndex = 0;
		
		while (windowEnd != lastTermInFeedbackIndex && startSearchQueryIndex < lastTermInFeedbackIndex) {
			windowStart = findNextQueryTerm(queryTerms, feedback, startSearchQueryIndex);
			if (windowStart == DIDNOT_FIND_SUCH_TERM) {
				break;
			}
			windowEnd = calcWindowEnd(queryTerms, feedback, windowStart);
			if (windowEnd != TERMS_NOT_IS_THE_SAME_DOCUMENT) {
				windows.add(new TextWindow(windowStart, windowEnd));
				startSearchQueryIndex = windowEnd;
			} else {
				if (windowStart > startSearchQueryIndex) {
					startSearchQueryIndex = windowStart;
				}
				startSearchQueryIndex++;
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
		
		Integer windowEnd = findNextQueryTerm(queryTerms, feedback, windowStart + 1);
		if (windowEnd == DIDNOT_FIND_SUCH_TERM) {
			return TERMS_NOT_IS_THE_SAME_DOCUMENT;
		}
		boolean indexesInSameDoc = this.doesIndexesInSameDocument(windowStart, windowEnd, feedback);
		return indexesInSameDoc ? windowEnd : TERMS_NOT_IS_THE_SAME_DOCUMENT;
	}

	private Integer findNextQueryTerm(Collection<String> queryTerms,
			Feedback feedback, int windowStart) {
		List<Integer> queryTermsIndexlist = new ArrayList<Integer>();
		List<String> terms = feedback.getTerms();
		List<String> subList = createSublistStartingCurrentTerm(terms , windowStart);
		
		for (String queryTerm : queryTerms) {
			queryTermsIndexlist.add( calculateQueryTermPos(windowStart, subList, queryTerm) );
		}
		
		Integer nextQueryTermIndex = getIndexNotLessThan(queryTermsIndexlist, windowStart);
		return nextQueryTermIndex;
	}

	private int calculateQueryTermPos(int startSerachIndex, List<String> list,
			String queryTerm) {
		return list.indexOf(queryTerm.toLowerCase()) + startSerachIndex;
	}

	private List<String> createSublistStartingCurrentTerm(List<String> terms, int startSerachIndex) {
		return terms.subList(startSerachIndex, terms.size());
	}

	/**
	 * Given a list of integers, the method search for the first element in the list that is equal or greater
	 * than <b>windowStart</b>.<br>
	 * If none exists than return <code>-1</code>.<br>
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
		return DIDNOT_FIND_SUCH_TERM;
	}

	@Override
	public void setWindowSize(int windowSize) {
		throw new IllegalAccessError("Cannot set fixed window size for: 'BetweenQueryTermsStrategy'");
	}

}

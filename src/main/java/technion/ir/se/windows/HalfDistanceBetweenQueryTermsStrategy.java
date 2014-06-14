package technion.ir.se.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import technion.ir.se.baseline.AbstractStrategy;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWidow;

public class HalfDistanceBetweenQueryTermsStrategy extends AbstractStrategy {

	private static final int DELIMITER = 1;

	@Override
	public List<TextWidow> getWindows(Feedback feedback, Query query) {
		ArrayList<TextWidow> list = new ArrayList<TextWidow>();
		List<String> queryTerms = query.getQueryTerms();
		List<String> terms = feedback.getTerms();
		
		List<Integer> queryTermsOccurrencesIndex = findQueryTermsOccurrences(queryTerms,terms);
		if (queryTermsOccurrencesIndex.size() > 1) {
			list.addAll( createWindows(queryTermsOccurrencesIndex, terms) );
		}
		
		return list;
	}

	private List<TextWidow> createWindows(List<Integer> occurrencesIndex, List<String> terms) {
		ArrayList<TextWidow> resultList = new ArrayList<TextWidow>();
		
		int leftGap = calcWindowGap(occurrencesIndex.get(0), occurrencesIndex.get(1));
		resultList.add(createFirstWindow(occurrencesIndex.get(0), leftGap));
		for (int i = 1; i < occurrencesIndex.size()-1; i++) {
			int rightGap = calcWindowGap(occurrencesIndex.get(i), occurrencesIndex.get(i+1));
			resultList.add( createWindow(occurrencesIndex.get(i), leftGap, rightGap));
			leftGap = rightGap;
		}
		resultList.add(createLastWindow(occurrencesIndex.get(occurrencesIndex.size()-1), leftGap, terms.size()-1));
		
		return resultList;
	}

	private TextWidow createFirstWindow(Integer centerPos, int gap) {
		TextWidow window = null;
		//when windows gap is too big
		if (centerPos - gap < 0) {
			window = new TextWidow(0, centerPos + gap);
		} else {
			window = createWindow(centerPos, gap, gap);
		}
		return window;
	}

	private TextWidow createLastWindow(Integer centerPos, int gap, int numberOfterms) {
		TextWidow widow = null;
		//when windows gap is too big
		if (centerPos + gap > numberOfterms) {
			widow = new TextWidow(centerPos - gap, numberOfterms);
		} else {
			widow = createWindow(centerPos, gap, gap);
		}
		return widow;
	}

	/**
	 * @param centerPos - Center location of window.
	 * @param leftGap - left gap from center.
	 * @param rightGap - right gap from center.
	 * @return
	 */
	private TextWidow createWindow(Integer centerPos, int leftGap, int rightGap) {
		return new TextWidow(centerPos-leftGap, centerPos+rightGap);
	}

	private int calcWindowGap(Integer left, Integer right) {
		return (right - left)/2;
	}

	private List<Integer> findQueryTermsOccurrences(List<String> queryTerms,
			List<String> terms) {
		
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		Integer queryIndex = -1;
		List<String> workingFeedbackTerms;
		Integer startSearchPos = updateStartSearchPos(queryIndex);
		while (queryIndex != null) {
			workingFeedbackTerms = terms.subList(startSearchPos, terms.size());
			TreeSet<Integer> set = new TreeSet<Integer>();
			for (String queryTerm : queryTerms) {
				set.add(workingFeedbackTerms.indexOf(queryTerm.toLowerCase()));
			}
			queryIndex = getNextIndexPositionInFeedback(set, startSearchPos);
			if (queryIndex != null) {
				resultList.add(queryIndex);
			}
			startSearchPos = updateStartSearchPos(queryIndex);
		}
		return resultList;
	}

	private Integer getNextIndexPositionInFeedback(TreeSet<Integer> set,
			Integer startSearchPos) {
		Integer queryIndex = getNextIndex(set);
		Integer pos = null;
		if (queryIndex != null) {
			pos = queryIndex + startSearchPos;
		}
		return pos;
	}

	private Integer getNextIndex(TreeSet<Integer> set) {
		return set.higher(Integer.valueOf(-1));
	}

	private int updateStartSearchPos(Integer queryIndex) {
		if (queryIndex != null) {
			return queryIndex + DELIMITER;
		}
		return 0;
	}

	@Override
	public void setWindowSize(int windowSize) {
		throw new IllegalAccessError("Cannot set fixed window size for: 'HalfDistanceBetweenQueryTerms'");
	}

}

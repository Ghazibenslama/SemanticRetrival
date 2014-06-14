package technion.ir.se.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import technion.ir.se.baseline.AbstractStrategy;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;

public class HalfDistanceBetweenQueryTermsStrategy extends AbstractStrategy {

	private static final int DELIMITER = 1;

	@Override
	public List<TextWindow> getWindows(Feedback feedback, Query query) {
		ArrayList<TextWindow> list = new ArrayList<TextWindow>();
		List<String> queryTerms = query.getQueryTerms();
		List<String> terms = feedback.getTerms();
		
		List<Integer> queryTermsOccurrencesIndex = findQueryTermsOccurrences(queryTerms,terms);
		if (queryTermsOccurrencesIndex.size() > 1) {
			list.addAll( createWindows(queryTermsOccurrencesIndex, terms) );
		}
		
		return list;
	}

	private List<TextWindow> createWindows(List<Integer> occurrencesIndex, List<String> terms) {
		ArrayList<TextWindow> resultList = new ArrayList<TextWindow>();
		
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

	private TextWindow createFirstWindow(Integer centerPos, int gap) {
		TextWindow window = null;
		//when windows gap is too big
		if (centerPos - gap < 0) {
			window = new TextWindow(0, centerPos + gap);
		} else {
			window = createWindow(centerPos, gap, gap);
		}
		return window;
	}

	private TextWindow createLastWindow(Integer centerPos, int gap, int numberOfterms) {
		TextWindow widow = null;
		//when windows gap is too big
		if (centerPos + gap > numberOfterms) {
			widow = new TextWindow(centerPos - gap, numberOfterms);
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
	private TextWindow createWindow(Integer centerPos, int leftGap, int rightGap) {
		return new TextWindow(centerPos-leftGap, centerPos+rightGap);
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

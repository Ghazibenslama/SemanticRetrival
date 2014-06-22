package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Document;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.TextWindow;
import technion.ir.se.indri.SearchEngine;
import technion.ir.se.windows.AbstractStrategy;
import technion.ir.se.windows.StrategyFactory;

public class SimilarityVectors {
	
	private static final String WINDOW_STRATEGY_KEY = "window.strategy";
	private SearchEngine serchEngine;
	private Feedback feedback;

	public SimilarityVectors() {
		serchEngine = SearchEngine.getInstance();
	}
	
	public List<String> buildRowTermVector (List<RetrivalResult> retrivalResult)
	{
		List<Integer> docIndriIDs = new ArrayList<Integer>() ;
		TreeSet<String> termsVector = new TreeSet<String>();
		for (RetrivalResult doc : retrivalResult) 
		{
			docIndriIDs.add(doc.getIndriDocumentId());
		}
		try {
			List<Document> documentsContet = serchEngine.getDocumentsContet(docIndriIDs);
			createFeedback(documentsContet);
			
			for (Document document : documentsContet) {
				termsVector.addAll(document.getDocumentTermsStemed());
			}
		} catch (Exception e) {
			System.err.println("error getting content by IndriID");
			e.printStackTrace();
		}
		ArrayList<String> resultList = new ArrayList<String>(termsVector);
		return resultList;
	}
	
	public Map<String, Map<String, Short>> buildVectors(Query query) {
		Feedback feedback = getFeedback();
		String windowStrategyName = Utils.readProperty(WINDOW_STRATEGY_KEY);
		AbstractStrategy strategy = StrategyFactory.factory(windowStrategyName, feedback);
		List<TextWindow> windows = strategy.getWindows(feedback, query);
		
		Map<String, Map<String, Short>> map = populateQueryVectors(query, strategy, windows);
		
		List<String> terms = getOnlyFeedbackTerms(feedback, query);
		Map<String, Map<String, Short>> feedbackTermsVectors = populateFeedbackVectors(terms, strategy, windows);
		map.putAll(feedbackTermsVectors);
		return map;
	}
	

	private List<String> getOnlyFeedbackTerms(Feedback feedback, Query query) {
		ArrayList<String> resultList = new ArrayList<String>(feedback.getTerms());
		resultList.removeAll(query.getQueryTerms());
		return resultList;
	}

	/**
	 * The method will calculate a frequency vector For each term in a window.<br>
	 * That means the same window will be iterated several times, In each iteration a different
	 * vector will be calculated.
	 * @param allTermsInFeedback - All the existing terms (Not including query terms)
	 * @param strategy - Windows strategy that is applied
	 * @param windows - set of windows
	 * @return
	 */
	private Map<String, Map<String, Short>> populateFeedbackVectors(
			List<String> allTermsInFeedback, AbstractStrategy strategy, List<TextWindow> windows) {
		
		Map<String, Map<String, Short>> feedbackTermsMap = createTermsMap(allTermsInFeedback);
		
		//pass all available windows
		for (TextWindow textWidow : windows) {
			List<String> termsInWindow = strategy.getTermsInWindow(textWidow);
			List<String> uniqueTermsInWindow = Utils.getUniqueValues(termsInWindow);
			HashMap<String, Short> windowFrequencyCache = new HashMap<String, Short>(uniqueTermsInWindow.size());
			for (String subjectOfVectorFreq : uniqueTermsInWindow) {
				//case term is not part of query
				if(doesUniqueTermAppearsInTerms(allTermsInFeedback, subjectOfVectorFreq)) {
					//iterate over each term in window
					for (int i = 0; i < uniqueTermsInWindow.size(); i++) {
						String someTermInWindow = uniqueTermsInWindow.get(i);
						//Since we iterate all terms in window, we don't want to take into consideration
						//the case if have encountered the current term
						if (!subjectOfVectorFreq.equals(someTermInWindow)) {
							//if value exist in cache, use it
							short frequency = findFrequencyOfOtherTerm(termsInWindow,
									windowFrequencyCache, someTermInWindow);
							updateTermFrequency(feedbackTermsMap, subjectOfVectorFreq, someTermInWindow, frequency);
						}
					}
				}
			}
		}
		return feedbackTermsMap;
	}

	private short findFrequencyOfOtherTerm(List<String> termsInWindow,
			HashMap<String, Short> windowFrequencyCache, String someTermInWindow) {
		short frequency;
		if (windowFrequencyCache.containsKey(someTermInWindow)) {
			frequency = windowFrequencyCache.get(someTermInWindow);
		} else {
			frequency = (short) Collections.frequency(termsInWindow, someTermInWindow);
			windowFrequencyCache.put(someTermInWindow, frequency);
		}
		return frequency;
	}

	private boolean doesUniqueTermAppearsInTerms(List<String> terms, String uniqueTerm) {
		return terms.contains(uniqueTerm);
	}

	private Map<String, Map<String, Short>> populateQueryVectors(Query query, AbstractStrategy strategy,
			List<TextWindow> windows) {
		
		Map<String, Map<String, Short>> queryVectors = createTermsMap(query.getQueryTerms());
		
		for (TextWindow textWidow : windows) {
			List<String> termsInWindow = strategy.getTermsInWindow(textWidow);
			for (String queryTerm : query.getQueryTerms()) {
				if (termsInWindow.contains(queryTerm)) {
					for (String term : termsInWindow) {
						updateTermFrequency(queryVectors, queryTerm, term);
					}
				}
			}
		}
		return queryVectors;
	}

	private void updateTermFrequency(
			Map<String, Map<String, Short>> queryMap, String outerKey,
			String innerKey) {
		
		updateTermFrequency(queryMap, outerKey, innerKey, (short) 1);
	}

	
	private void updateTermFrequency(
			Map<String, Map<String, Short>> map, String outerKey, 
			String innerKey, short frequency) {
		
		Map<String, Short> mapOfQueryTerm = map.get(outerKey);
		Short currentFrequency = mapOfQueryTerm.get(innerKey);
		short newFrequency = (short) (currentFrequency == null ? frequency : currentFrequency+frequency);
		mapOfQueryTerm.put(innerKey, newFrequency);
	}

	private void createFeedback(List<Document> documentsContet) {
		this.feedback = new Feedback(documentsContet);
	}
	
	private Feedback getFeedback() {
		return feedback;
	}

	private Map<String, Map<String, Short>> createTermsMap(List<String> terms) {
		List<String> uniqueTerms = Utils.getUniqueValues(terms);
		HashMap<String, Map<String, Short>> queryVectorsMap = new HashMap<String, Map<String, Short>>();
		for (String term : uniqueTerms) {
			queryVectorsMap.put(term, new HashMap<String, Short>());
		}
		return queryVectorsMap;
	}

}

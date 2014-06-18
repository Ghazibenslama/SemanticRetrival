package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import technion.ir.se.Model.Model;
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
	private List<Document> documents;

	public SimilarityVectors() {
		serchEngine = SearchEngine.getInstance();
	}
	
	public void buildRowTermVector (List<RetrivalResult> retrivalResult)
	{
		List<Integer> docIndriIDs = new ArrayList<Integer>() ;
		TreeSet<String> termsVector = new TreeSet<String>();
		for (RetrivalResult doc : retrivalResult) 
		{
			docIndriIDs.add(doc.getIndriDocumentId());
		}
		try {
			documents = serchEngine.getDocumentsContet(docIndriIDs);
			for (Document document : documents) {
				termsVector.addAll(document.getDocumentTermsStemed());
			}
		} catch (Exception e) {
			System.err.println("error getting content by IndriID");
			e.printStackTrace();
		}
		ArrayList<String> resultList = new ArrayList<String>(termsVector);
		Model model = Model.getInstance();
		model.setModel(resultList);
//		this.rowTermVector = resultList;
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

	private Map<String, Map<String, Short>> populateFeedbackVectors(
			List<String> terms, AbstractStrategy strategy, List<TextWindow> windows) {
		
		Map<String, Map<String, Short>> feedbackTermsMap = createTermsMap(terms);
		
		for (TextWindow textWidow : windows) {
			List<String> termsInWindow = strategy.getTermsInWindow(textWidow);
			List<String> uniqueTerms = Utils.getUniqueValues(termsInWindow);
			for (String uniqueTerm : uniqueTerms) {
				if(doesTermAppearsInTerms(terms, uniqueTerm)) {
					for (int i = 0; i < uniqueTerms.size(); i++) {
						String otherTerm = uniqueTerms.get(i);
						if (!uniqueTerm.equals(otherTerm)) {
							short frequency = findFrequencyOfOtherTerm(termsInWindow, otherTerm);
							
							updateTermFrequency(feedbackTermsMap, uniqueTerm, otherTerm, frequency);
						}
					}
				}
			}
		}
		return feedbackTermsMap;
	}

	private boolean doesTermAppearsInTerms(List<String> terms, String uniqueTerm) {
		return terms.contains(uniqueTerm);
	}

	private short findFrequencyOfOtherTerm(List<String> termsInWindow, String otherTerm) {
		return (short)Collections.frequency(termsInWindow, otherTerm);
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

	private Feedback getFeedback() {
		return new Feedback(documents);
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

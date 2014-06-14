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
	private List<String> rowTermVector;
	private List<Document> documents;

	public SimilarityVectors() {
		serchEngine = new SearchEngine();
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
		this.rowTermVector = resultList;
	}
	
	public Map<String, int[]> buildVectors(Query query) {
		Feedback feedback = getFeedback();
		String windowStrategyName = Utils.readProperty(WINDOW_STRATEGY_KEY);
		AbstractStrategy strategy = StrategyFactory.factory(windowStrategyName, feedback);
		List<TextWindow> windows = strategy.getWindows(feedback, query);
		
		Map<String, int[]> vectors = populateQueryVectors(query, strategy, windows);
		
		List<String> terms = getOnlyFeedbackTerms(feedback, query);
		Map<String, int[]> feedbackTermsVectors = populateFeedbackVectors(terms, strategy, windows);
		vectors.putAll(feedbackTermsVectors);
		return vectors;
	}
	

	private List<String> getOnlyFeedbackTerms(Feedback feedback, Query query) {
		ArrayList<String> resultList = new ArrayList<String>(feedback.getTerms());
		resultList.removeAll(query.getQueryTerms());
		return resultList;
	}

	private Map<String, int[]> populateFeedbackVectors(
			List<String> terms, AbstractStrategy strategy, List<TextWindow> windows) {
		
		Map<String, int[]> feedbackTermsVectors = createTermsMap(terms);
		
		for (TextWindow textWidow : windows) {
			List<String> termsInWindow = strategy.getTermsInWindow(textWidow);
			List<String> uniqueTerms = Utils.getUniqueValues(termsInWindow);
			for (String uniqueTerm : uniqueTerms) {
				if(doesTermAppearsInTerms(terms, uniqueTerm)) {
					for (int i = 0; i < uniqueTerms.size(); i++) {
						String otherTerm = uniqueTerms.get(i);
						if (!uniqueTerm.equals(otherTerm)) {
							int index = findIndexOfOtherTerm(otherTerm);
							int frequency = findFrequencyOfOtherTerm(termsInWindow, otherTerm);
							
							feedbackTermsVectors.get(uniqueTerm)[index]+=frequency;
						}
					}
				}
			}
		}

		return feedbackTermsVectors;
	}

	private boolean doesTermAppearsInTerms(List<String> terms, String uniqueTerm) {
		return terms.contains(uniqueTerm);
	}

	private int findFrequencyOfOtherTerm(List<String> termsInWindow, String otherTerm) {
		return Collections.frequency(termsInWindow, otherTerm);
	}

	private int findIndexOfOtherTerm(String otherTerm) {
		return rowTermVector.indexOf(otherTerm);
	}

	private Map<String, int[]> populateQueryVectors(Query query, AbstractStrategy strategy,
			List<TextWindow> windows) {
		
		Map<String, int[]> queryVectors = createTermsMap(query.getQueryTerms());
		
		for (TextWindow textWidow : windows) {
			List<String> termsInWindow = strategy.getTermsInWindow(textWidow);
			for (String queryTerm : query.getQueryTerms()) {
				if (termsInWindow.contains(queryTerm)) {
					for (String term : termsInWindow) {
						int index = findIndexOfOtherTerm(term);
						queryVectors.get(queryTerm)[index]++;
					}
				}
			}
		}
		return queryVectors;
	}

	private Feedback getFeedback() {
		return new Feedback(documents);
	}

	private Map<String, int[]> createTermsMap(List<String> terms) {
		List<String> uniqueTerms = Utils.getUniqueValues(terms);
		HashMap<String, int[]> queryVectorsMap = new HashMap<String, int[]>();
		for (String term : uniqueTerms) {
			queryVectorsMap.put(term, new int[rowTermVector.size()]);
		}
		return queryVectorsMap;
	}

}

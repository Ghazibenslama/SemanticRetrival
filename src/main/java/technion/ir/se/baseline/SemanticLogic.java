package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Document;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.dao.TextWidow;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogic {
	
	private static final String WINDOW_STRATEGY_KEY = "window.strategy";
	private SearchEngine serchEngine;
	private List<String> rowTermVector;
	private List<Document> documents;

	public SemanticLogic() {
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
	
	public Map<String, int[]> buildQueryVectors(Query query) {
		Feedback feedback = getFeedback();
		String windowStrategyName = Utils.readProperty(WINDOW_STRATEGY_KEY);
		AbstractStrategy strategy = StrategyFactory.factory(windowStrategyName, feedback);
		List<TextWidow> windows = strategy.getWindows(feedback, query);
		
		Map<String, int[]> queryVectors  = populateVectorsWithTerms(query, strategy, windows);
		return queryVectors;
	}

	private Map<String, int[]> populateVectorsWithTerms(Query query, AbstractStrategy strategy,
			List<TextWidow> windows) {
		
		Map<String, int[]> queryVectors = createQueryTermsMap(query.getQueryTerms());
		
		for (TextWidow textWidow : windows) {
			List<String> termsInWindow = strategy.getTermsInWindow(textWidow);
			for (String queryTerm : query.getQueryTerms()) {
				if (termsInWindow.contains(queryTerm)) {
					for (String term : termsInWindow) {
						int index = rowTermVector.indexOf(term);
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

	private Map<String, int[]> createQueryTermsMap(List<String> queryTerms) {
		HashMap<String, int[]> queryVectorsMap = new HashMap<String, int[]>();
		for (String queryTerm : queryTerms) {
			queryVectorsMap.put(queryTerm, new int[rowTermVector.size()]);
		}
		return queryVectorsMap;
	}
}

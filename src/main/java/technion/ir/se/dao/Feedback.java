package technion.ir.se.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import technion.ir.se.exception.LocationNotFoundException;

/**
 * This class is a the relevance feedback of a query.
 * It contains a {@link List} of String, where each String is a term in one of the feedback documents.
 * @author XPS_Sapir
 *
 */
public class Feedback {
	private static final int NO_MORE_WINDOWS = -1;
	private List<Document> documents;
	private List<String> terms;
	private TreeMap<Integer, Document> docsStartingIndex;
	
	/**
	 * @param terms - {@link List} of Document that are the documents in the feedback
	 */
	public Feedback(List<Document> documents) {
		this.documents = documents;
		this.terms = null;
		initDocsStartingIndex();
	}
	
	private void initDocsStartingIndex() {
		int index = 0;
		docsStartingIndex = new TreeMap<Integer, Document>();
		for (Document document : documents) {
			docsStartingIndex.put(index, document);
			index += document.getDocumentTermsStemed().size();
		}
		
	}

	/**
	 * @return {@link List} of String that appears in feedback documents.
	 * <ul>
	 * <li>The first element in the List is the first term in top#1 document feedback</li>
	 * <li>The second element in the List is the second term in top#1 document feedback</li>
	 * <li>etc...</li>
	 * </ul>
	 */
	public List<String> getTerms() {
		if (terms==null) {
			initTerms();
		}
		return terms;
	}

	private void initTerms() {
		if (terms==null) {
			terms = new ArrayList<String>();
			for (Document document : documents) {
				terms.addAll(document.getDocumentTermsStemed());
			}
		}
	}
	
	public int getNumberOfTerms() {
		initTerms();
		return terms.size();
	}

	public Document getDocumentOfIndex(int index) throws LocationNotFoundException {
		Entry<Integer, Document> floorEntry = docsStartingIndex.floorEntry(index);
		if (floorEntry != null) {
			return floorEntry.getValue();
		}
		throw new LocationNotFoundException(String.format("Got index '%d' that is smaller that all documents starting index", index));
	}

	/**
	 * The method returns the starting index of the next document, or -1 if there is no document.
	 * @param windowStart
	 * @return
	 */
	public int getNextDocumentStartingIndex(int windowStart) {
		Integer higherKey = docsStartingIndex.higherKey(windowStart);
		if (higherKey == null) {
			return NO_MORE_WINDOWS;
		} else {
			return higherKey;
		}
	}
	
}

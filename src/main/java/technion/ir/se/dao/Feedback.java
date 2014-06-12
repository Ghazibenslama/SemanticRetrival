package technion.ir.se.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a the relevance feedback of a query.
 * It contains a {@link List} of String, where each String is a term in one of the feedback documents.
 * @author XPS_Sapir
 *
 */
public class Feedback {
	private List<Document> documents;
	private List<String> terms;
	
	/**
	 * @param terms - {@link List} of Document that are the documents in the feedback
	 */
	public Feedback(List<Document> documents) {
		this.documents = documents;
		this.terms = null;
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
	
}

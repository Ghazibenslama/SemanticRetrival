package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import technion.ir.se.dao.Document;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogic {
	
	private SearchEngine serchEngine;

	public SemanticLogic() {
		serchEngine = new SearchEngine();
	}
	public SortedSet<String> buildRowTermVector (List<RetrivalResult> retrivalResult)
	{
		List<Integer> docIndriIDs = new ArrayList<Integer>() ;
		TreeSet<String> termsVector = new TreeSet<String>();
		for (RetrivalResult doc : retrivalResult) 
		{
			docIndriIDs.add(doc.getIndriDocumentId());
		}
		List<Document> documentsContet;
		try {
			documentsContet = serchEngine.getDocumentsContet(docIndriIDs);
			for (Document document : documentsContet) {
				termsVector.addAll(document.getDocumentTermsStemed());
			}
		} catch (Exception e) {
			System.err.println("error getting content by IndriID");
			e.printStackTrace();
		}
		return termsVector;
	}

	private List<String> retrieveQueryTerms(Query query) {
		String queryContent = query.getQueryText();
		String[] queryTerms = queryContent.split(" ");
		ArrayList<String> queryTermsList = new ArrayList<String>(Arrays.asList(queryTerms));
		return queryTermsList;
	}
	
	

}

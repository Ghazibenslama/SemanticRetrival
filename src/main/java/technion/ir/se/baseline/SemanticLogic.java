package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import technion.ir.se.dao.Document;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class SemanticLogic {
	
	private SearchEngine serchEngine;
	private List<String> rowTermVector;

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
		ArrayList<String> resultList = new ArrayList<String>(termsVector);
		this.rowTermVector = resultList;
	}
	
}

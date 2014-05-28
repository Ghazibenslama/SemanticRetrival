package technion.ir.se.indri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import technion.ir.se.dao.RetrivalResult;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

public class SearchEngine {
	
	//collection name
	public String COLLECTION;
	//the path to the documents index folder
	public String DOCUMENTS_INDEX_DIR = "C:\\Temp\\InformationRetrival\\testIndexDir";
	//the path to the entities index folder
	public String ENTITIES_INDEX_DIR;
	
	private QueryEnvironment queryEnvironment;
	
	public SearchEngine() {
		queryEnvironment = new QueryEnvironment();
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to init QueryEnvironment");
		}
	}
	private void init() throws Exception {
		queryEnvironment.setMemory(1);
		queryEnvironment.addIndex(DOCUMENTS_INDEX_DIR);
	}
	
	public List<RetrivalResult> runQuery(int numOfInitialDocuments, String[] rules, String query) throws Exception {
		List<RetrivalResult> retrivedDocuments = new ArrayList<RetrivalResult>();

		queryEnvironment.setScoringRules(rules);
		ScoredExtentResult[] results = queryEnvironment.runQuery(query, numOfInitialDocuments);
		String[] fileNamesInDisk = retrivedDocumentsNames(results);
		
		for (int i = 0; i < results.length; i++) {
			RetrivalResult retrivalResult = new RetrivalResult( results[i].score, results[i].document, results[i].begin, 
					results[i].end, results[i].number, results[i].ordinal, results[i].parentOrdinal, fileNamesInDisk[i]);
			
			retrivedDocuments.add(retrivalResult);
		}
		return retrivedDocuments;
	}
	
	private String[] retrivedDocumentsNames(ScoredExtentResult[] results) throws Exception {
		return queryEnvironment.documentMetadata(results, "docno");
	}
	
	private String getDocumentContet(int documentId) throws Exception {
		List<String> list = this.getDocumentsContet(Arrays.asList(new Integer(documentId)));
		return list.get(0);
	}
	
	private List<String> getDocumentsContet(List<Integer> documentIds) throws Exception {
		int[] documentIdsInt = ArrayUtils.toPrimitive(documentIds.toArray(new Integer[documentIds.size()]));
		ParsedDocument[] documents = queryEnvironment.documents( documentIdsInt );
		
		List<String> documentsResult = new ArrayList<String>();
		for (int i = 0; i < documents.length; i++) {
			documentsResult.add(documents[i].text);
		}
		return documentsResult;
		
	}
}

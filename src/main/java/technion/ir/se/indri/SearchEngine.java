package technion.ir.se.indri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lemurproject.indri.DocumentVector;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

import org.apache.commons.lang3.ArrayUtils;

import technion.ir.se.dao.Document;
import technion.ir.se.dao.RetrivalResult;

public class SearchEngine {
	
	//collection name
	public String COLLECTION;
	//the path to the documents index folder
	public String DOCUMENTS_INDEX_DIR = "C:\\Temp\\InformationRetrival\\testIndexDir";
	//the path to the entities index folder
	public String ENTITIES_INDEX_DIR;
	
	private QueryEnvironment queryEnvironment;
	private static SearchEngine instance = null;
	
	private SearchEngine() {
		queryEnvironment = new QueryEnvironment();
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to init QueryEnvironment");
		}
	}
	
	public static synchronized SearchEngine getInstance() {
		if (instance == null) {
			instance  = new SearchEngine();
		}
		return instance;
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
	
	public List<Document> getDocumentsContet(List<Integer> documentIds) throws Exception {
		int[] documentIdsInt = ArrayUtils.toPrimitive(documentIds.toArray(new Integer[documentIds.size()]));
		DocumentVector[] documentVectors = queryEnvironment.documentVectors(documentIdsInt);
		
		List<Document> documentsResult = new ArrayList<Document>();
		for (int i = 0; i < documentVectors.length; i++) {
			List<String> stemedTerms = new ArrayList<String>(Arrays.asList(documentVectors[i].stems));
			documentsResult.add(new Document(stemedTerms));
		}
		return documentsResult;
		
	}
	
	public long documentStemCount(String term) {
		return queryEnvironment.documentStemCount(term);
	}
	
	public long documentCount(String term) throws Exception {
		return queryEnvironment.documentCount(term);
	}
	
	public Long documentCount() throws Exception {
		return Long.valueOf(queryEnvironment.documentCount());
	}
}

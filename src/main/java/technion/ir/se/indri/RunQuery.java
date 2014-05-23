package technion.ir.se.indri;

import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

public class RunQuery {
	
	//collection name
	public String COLLECTION;
	//the path to the documents index folder
	public String DOCUMENTS_INDEX_DIR = "C:\\Temp\\InformationRetrival\\testIndexDir";
	//the path to the entities index folder
	public String ENTITIES_INDEX_DIR;
	
	private QueryEnvironment queryEnvironment;
	
	public RunQuery() {
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
	
	public int runQuery(int numOfInitialDocuments, String[] rules, String query, String idField) throws Exception {
		queryEnvironment.setScoringRules(rules);
		ScoredExtentResult[] results = queryEnvironment.runQuery(query, numOfInitialDocuments);
		String[] names = queryEnvironment.documentMetadata(results, idField);
		int docId = results[0].document;
		return docId;
//		return names.length;
//		Result[] res = new Result[names.length];
//		for(int i=0;i<names.length;i++){
//			Result r= new Result();
//			r.ResultName = names[i];
//			r.ResultScore = results[i].score;
//			r.ResultID = results[i].document;
//			res[i]=r;
//		}
//		return res;
	}
}

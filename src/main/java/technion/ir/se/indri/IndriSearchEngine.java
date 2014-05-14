package technion.ir.se.indri;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import Queries.Result;
import lemurproject.indri.DocumentVector;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

public class IndriSearchEngine{

	//========parameters set by config file=========
	//collection name
	public String COLLECTION;
	//the path to the documents index folder
	public String DOCUMENTS_INDEX_DIR;
	//the path to the entities index folder
	public String ENTITIES_INDEX_DIR;
	
	//========internal parameters===================
	//documents index enviornment
	QueryEnvironment docsEnv;
	
	public void initialize() throws Exception {
		docsEnv = new QueryEnvironment();   
		docsEnv.setMemory(1);
		docsEnv.addIndex(DOCUMENTS_INDEX_DIR);
	}

	public void initializeWithParameters(String parameters) throws Exception{
		if(docsEnv==null){
			docsEnv = new QueryEnvironment();   
			docsEnv.setMemory(1);
			docsEnv.addIndex(parameters);
		}
	}

	public Result[] runQuery(int numOfInitialDocuments, String[] rules,String searchField, String query, String idField) throws Exception {
		docsEnv.setScoringRules(rules);
		ScoredExtentResult[] results = docsEnv.runQuery(query, numOfInitialDocuments);
		String[] names = docsEnv.documentMetadata(results, idField);
		Result[] res = new Result[names.length];
		for(int i=0;i<names.length;i++){
			Result r= new Result();
			r.ResultName = names[i];
			r.ResultScore = results[i].score;
			r.ResultID = results[i].document;
			res[i]=r;
		}
		return res;
	}

	private int[] extractDocIDs(Result[] resultList, QueryEnvironment env, int numberOfDocs) throws Exception {
		int[] ids = new int[numberOfDocs];
		for(int i=0;i<numberOfDocs;i++){
			if(resultList[i].ResultID!=0){
				ids[i] = resultList[i].ResultID;
			}else{
				String[] docname = new String[1];
				docname[0] = resultList[i].ResultName;
				int[] docsIDs = env.documentIDsFromMetadata("docno", docname);
				ids[i] = docsIDs[0];
			}
		}
		return ids;
	}
	
	public Map<String,Double> runQueryForResultList(Result[] resultList, String[] rules,String query, int numberOfDocs) throws Exception {
		docsEnv.setScoringRules(rules);
		int[] docids = extractDocIDs(resultList,docsEnv,numberOfDocs);
		ScoredExtentResult[] res = this.docsEnv.runQuery(query, docids, docids.length);
		String[] names = docsEnv.documentMetadata(res, "docno");
		Map<String,Double> scores = new HashMap<String, Double>();
		for(int i=0;i<names.length;i++){
			scores.put(names[i], res[i].score);
		}
		return scores;
	}
	
	public Result[] runQueryForAGivenResultList(Result[] resultList, String[] rules,String query, int numberOfDocs) throws Exception {
		this.docsEnv.setScoringRules(rules);
		int[] docids = extractDocIDs(resultList,docsEnv,numberOfDocs);
		ScoredExtentResult[] res = this.docsEnv.runQuery(query, docids, docids.length);
		String[] names = docsEnv.documentMetadata(res, "docno");
		Result[] results = new Result[names.length];
		for(int i=0;i<names.length;i++){
			Result r= new Result();
			r.ResultName = names[i];
			r.ResultScore = res[i].score;
			r.ResultID = res[i].document;
			results[i]=r;
		}
		return results;
	}
	
	public long getTotalCollectionCount(String field) throws Exception {
		return this.docsEnv.termCount();
	}

	public Map<String, Long> getTermsCollectionFrequency(
			List<String> queryTerms, String field) throws Exception {
		Map<String, Long> collectionFreq = new HashMap<String, Long>();
		for(String term: queryTerms){
			if(this.docsEnv.stemCount(term)==0){
				System.out.println("no collection frequency for term ," + term +  ",");
				collectionFreq.put(term, (long) 0.0);
				//System.exit(1);
			}else{
				collectionFreq.put(term, this.docsEnv.stemCount(term));
			}
		}
		return collectionFreq;
	}
	
	public Map<String, DocumentLanguageModel> calcDocumentsLM(Result[] res, String field, 
			String smoothingType, String smoothingParam,int numberOfDocs,boolean calcStats,
			boolean oneDocumentSmoothing,String idField) throws Exception {
		int minNumber = Math.min(numberOfDocs, res.length);
		Map<String,DocumentLanguageModel> docsLM = new HashMap<String, DocumentLanguageModel>();
		for(int i=0;i<minNumber;i++){
			String[] result = new String[1];
			result[0]=res[i].ResultName;
			int[] docsIDs = docsEnv.documentIDsFromMetadata("docno", result);
			if(docsIDs.length>0){
				DocumentVector[] docs = docsEnv.documentVectors(docsIDs);
				DocumentLanguageModel docLM = createDocumentLM(docsIDs[0],docs[0],smoothingParam,smoothingType,calcStats,oneDocumentSmoothing);
				docsLM.put(res[i].ResultName, docLM);
			}else{
				//System.out.println("empty vector for " + result[0]);
				DocumentLanguageModel docLM = createEmptyLM(smoothingParam,smoothingType,oneDocumentSmoothing);
				docsLM.put(res[i].ResultName, docLM);
			}
		}	
		return docsLM;
	}

	private DocumentLanguageModel createEmptyLM(String smoothingParam, String smoothingType, boolean oneDocumentSmoothing) throws Exception {
		DocumentLanguageModel docLM = new DocumentLanguageModel();
		docLM.setDocID(-1);
		docLM.setSmoothingMethod(smoothingType);
		docLM.setSmoothingParam(smoothingParam);
		docLM.setCollectionCount(docsEnv.termCount());
		if(oneDocumentSmoothing){
			docLM.setDocLength(1);
		}else{
			docLM.setDocLength(0);
		}
		return docLM;
	}

	private DocumentLanguageModel createDocumentLM(int docID, DocumentVector dv, 
			String smoothingParam, String smoothingType, boolean calcStats, boolean oneDocumentSmoothing) throws Exception {
		DocumentLanguageModel docLM = new DocumentLanguageModel();
		docLM.setDocID(docID);
		docLM.setSmoothingMethod(smoothingType);
		docLM.setSmoothingParam(smoothingParam);
		docLM.setCollectionCount(docsEnv.termCount());
		int docLength = 0;
		if(dv.positions.length==0){
			//System.out.println("document " + docID + " is of length 0");
		}
		for(int j=0;j<dv.positions.length;j++){
			String term = dv.stems[dv.positions[j]];
			if(!term.equals("[OOV]")){
				docLM.addTermInDoc(term);
				if(!docLM.termCollectionStats(term)){
					docLM.addTermCollectionStats(term,docsEnv.stemCount(term));
					if(docsEnv.stemCount(term)==0){
						System.out.println("term in a document does not exist in collection");
						System.exit(1);
					}
				}
				docLength++;
			}
		}
		if(oneDocumentSmoothing){
			docLM.setDocLength(docLength+1);
		}else{
			docLM.setDocLength(docLength);
		}
		if(calcStats){
			docLM.calcTermsStatistics();
		}
		return docLM;
	}

	public Result[] reRankUsingRM(Map<String,Double> rm, String originalQuery, double rmLambda,
			String smoothingParam, String smoothingType,Result[] res,int numberOfDocuments,String field,
			Map<String,DocumentLanguageModel> lm) throws Exception {
		String newQuery = buildRMExtendedQuery(rm,originalQuery,rmLambda);
		System.out.println(newQuery);
		String[] rules = {"method:"+smoothingType,"mu:"+ smoothingParam};
		return this.runQueryForAGivenResultList(res, rules, newQuery, numberOfDocuments);
	}

	public String buildRMExtendedQuery(Map<String, Double> rm, String originalQuery,double rmLambda) {
		checkRM(rm);
		StringBuffer sb = new StringBuffer();
		sb.append("#weight( ");
		sb.append(String.valueOf(rmLambda) + " ");
		sb.append(originalQuery + " ");
		sb.append(String.valueOf(1-rmLambda) + " ");
		sb.append("#weight( ");
		for(Entry<String, Double> entry: rm.entrySet()){
			sb.append(entry.getValue() + " " + entry.getKey() + " ");
		}
		sb.append("))");
		return sb.toString();
	}

	private void checkRM(Map<String, Double> rm) {
		double sum = 0;
		for(Entry<String,Double> entry: rm.entrySet()){
			sum=sum+entry.getValue();
		}
		if(sum>1.00001|sum<0.99999999){
			System.out.println("sum is not equal to one");
			for(Entry<String,Double> entry: rm.entrySet()){
				rm.put(entry.getKey(), entry.getValue()/sum);
			}
		}
	}

}

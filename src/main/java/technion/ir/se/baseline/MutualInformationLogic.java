package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import technion.ir.se.dao.MutualInformation;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class MutualInformationLogic {
	private final Logger logger = Logger.getLogger(MutualInformationLogic.class);
	private static final String QUERY_TEMPLATE = "#band(%s %s)";//#band - use for finding documents which contain both term A and B
	private SearchEngine engine;
	private final String[] QUERY_RULE = new String[]{"method:tfidf"};//We chose random rule in order to get all documents (the rank isn't important)
	private final double THRESHOLD = 0.5;// 0 - return all
	private String queryID;

	public MutualInformationLogic(SearchEngine engine) {
		this.engine = engine;
	}
	
	public List<List<String>> findPhrases(Query query) throws Exception {
		return findPhrasesInternal(query, THRESHOLD);
	}

	private List<List<String>> findPhrasesInternal(Query query, double threshold) throws Exception {
		ArrayList<List<String>> resultLists = new ArrayList<List<String>>();
		List<String> queryTerms = query.getQueryTerms();
		this.setQueryID(query.getId());
		
		for (int i = 0; i < queryTerms.size() -1 ; i++) {
			String termA = queryTerms.get(i);
			String termB = queryTerms.get(i+1);
			long termADocFreq = engine.documentCount(termA);
			long termBDocFreq = engine.documentCount(termB);
			String pairQuery = String.format(QUERY_TEMPLATE, termA, termB);
			int numOfDocuments = engine.documentCount().intValue();
			
			List<RetrivalResult> mutualDocumentList = engine.runQuery(numOfDocuments, QUERY_RULE, pairQuery);
			int mutualDocumentsSize = mutualDocumentList.size();
			MutualInformation mi = new MutualInformation(termADocFreq, termBDocFreq, mutualDocumentsSize, numOfDocuments);
			
			double mutualInformationScore = calcMuaualInformation(mi);
			// if bigger than Threshold, the terms are dependent
			if (mutualInformationScore > threshold) {
				logger.info("PairPhraseIDC: " + query.getId()+" "+ termA +" "+termB);
				resultLists.add( new ArrayList<String>(Arrays.asList(termA, termB)) );
			}
		}
		return resultLists;
	}
	
	public List<List<String>> findPhrases(Query query, double threshold) throws Exception {
		return findPhrasesInternal(query, threshold);
	}
	
	private double calcMuaualInformation(MutualInformation mi) {
		prerequisitesCalculations(mi);
		
		return calcMuaualInformationScore(mi);
	}

	private void prerequisitesCalculations(MutualInformation mi) {
		double termAProb = calcTermAProb(mi.getTermADocFreq(), mi.getNumberOfDocuments());
		mi.setTermAProb(termAProb);
		
		double termBProb = calcTermBProb(mi.getTermBDocFreq(), mi.getNumberOfDocuments());
		mi.setTermBProb(termBProb);
		
		double mutualProb = calcMutualProb(mi.getMutualDocumentsSize(), mi.getNumberOfDocuments());
		mi.setMutualProb(mutualProb);
		
		double onlyTermAExistsProb = calcOnlyTermAExists(mi);
		mi.setOnlyTermAExistsProb(onlyTermAExistsProb);
		
		double onlyTermBExistsProb = calcOnlyTermBExists(mi);
		mi.setOnlyTermBExists(onlyTermBExistsProb);
		
		double mutualcompProb = calcMutualcompProb(mi);
		mi.setMutualCompProb(mutualcompProb);
	}

	private double calcMuaualInformationScore(MutualInformation mi) {
		double calcOne = calcFormula(mi.getMutualCompProb(), mi.getTermACompProb(), mi.getTermBCompProb());
		double calcTwo = calcFormula(mi.getOnlyTermAExist(), mi.getTermAProb(), mi.getTermBCompProb());
		double calcThree = calcFormula(mi.getOnlyTermBExists(), mi.getTermACompProb(), mi.getTermBProb());
		double calcFour = calcFormula(mi.getMutualProb(), mi.getTermAProb(), mi.getTermBProb());
		
		return calcOne + calcTwo + calcThree + calcFour;
		
	}

	private double calcFormula(double mutualProb, double aProb, double bProb) {
		double result = mutualProb *  Math.log(mutualProb / (aProb * bProb) );
		return Double.isNaN(result) ? 0 : result;
	}

	private double calcMutualcompProb(MutualInformation mi) {
		return 1 - mi.getOnlyTermAExist() - mi.getOnlyTermBExists() - mi.getMutualProb();
	}

	private double calcOnlyTermBExists(MutualInformation mi) {
		return calcOnlyOneTermExists(mi.getTermBProb(), mi);
	}
	
	private double calcOnlyTermAExists(MutualInformation mi) {
		return calcOnlyOneTermExists(mi.getTermAProb(), mi);
	}

	private double calcOnlyOneTermExists(double termProb, MutualInformation mi) {
		double result = (termProb - mi.getMutualProb() ) / mi.getNumberOfDocuments();
		return testForInfinite(result);
	}

	private double calcMutualProb(int mutualDocumentsSize, long numberOfDocuments) {
		return calcTermProb(mutualDocumentsSize, numberOfDocuments);
	}

	private double calcTermBProb(long termBDocFreq, long numberOfDocuments) {
		double prob = calcTermProb(termBDocFreq, numberOfDocuments);
		return prob;
	}

	private double calcTermAProb(long termADocFreq, long numberOfDocuments) {
		double prob = calcTermProb(termADocFreq, numberOfDocuments);
		return prob;
	}

	private double calcTermProb(double termDocFreq, double numberOfDocuments) {
		double result = termDocFreq/numberOfDocuments;
		return testForInfinite(result);
	}

	private double testForInfinite(double result) {
		return Double.isInfinite(result) ? 0 : result;
	}
	
	public String getQueryID()
	{
		return this.queryID;
	}
	
	public void setQueryID(String ID)
	{
		this.queryID = ID;
	}

	
}

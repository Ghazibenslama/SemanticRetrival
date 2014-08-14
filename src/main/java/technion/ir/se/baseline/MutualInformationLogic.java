package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import technion.ir.se.dao.MutualInformation;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.SearchEngine;

public class MutualInformationLogic {
	private static final String QUERY_TEMPLATE = "#band(%s %s)";
	private SearchEngine engine;
	private final String[] QUERY_RULE = new String[]{"method:tfidf"};
	private final double THRESHOLD = 8;

	public MutualInformationLogic(SearchEngine engine) {
		this.engine = engine;
	}
	
	public List<List<String>> findPhrases(Query query) throws Exception {
		ArrayList<List<String>> resultLists = new ArrayList<List<String>>();
		List<String> queryTerms = query.getQueryTerms();
		
		for (int i = 0; i < queryTerms.size() -1 ; i++) {
			String termA = queryTerms.get(i);
			String termB = queryTerms.get(i+1);
			long termADocFreq = engine.documentStemCount(termA);
			long termBDocFreq = engine.documentStemCount(termB);
			String pairQuery = String.format(QUERY_TEMPLATE, termA, termB);
			int numOfDocuments = engine.documentCount().intValue();
			List<RetrivalResult> mutualDocumentList = engine.runQuery(numOfDocuments, QUERY_RULE, pairQuery);
			int mutualDocumentsSize = mutualDocumentList.size();
			MutualInformation mi = new MutualInformation(termADocFreq, termBDocFreq, mutualDocumentsSize, numOfDocuments);
			
			double muaualInformationScore = calcMuaualInformation(mi);
			if (muaualInformationScore > THRESHOLD) {
				resultLists.add( new ArrayList<String>(Arrays.asList(termA, termB)) );
			}
		}
		return resultLists;
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
		double calcOne = calcformula(mi.getMutualCompProb(), mi.getTermACompProb(), mi.getTermBCompProb());
		double calcTwo = calcformula(mi.getOnlyTermAExist(), mi.getTermAProb(), mi.getTermBCompProb());
		double calcThree = calcformula(mi.getOnlyTermBExists(), mi.getTermACompProb(), mi.getTermBProb());
		double calcFour = calcformula(mi.getMutualProb(), mi.getTermAProb(), mi.getTermBProb());
		
		return calcOne + calcTwo + calcThree + calcFour;
		
	}

	private double calcformula(double mutualProb, double aProb, double bProb) {
		return mutualProb *  Math.log(mutualProb / (aProb * bProb) );
	}

	private double calcMutualcompProb(MutualInformation mi) {
		return 1 - mi.getOnlyTermAExist() - mi.getOnlyTermBExists() - mi.getMutualProb();
	}

	private double calcOnlyTermBExists(MutualInformation mi) {
		return (mi.getTermBProb() - mi.getMutualProb() ) / mi.getNumberOfDocuments();
	}

	private double calcOnlyTermAExists(MutualInformation mi) {
		return (mi.getTermAProb() - mi.getMutualProb() ) / mi.getNumberOfDocuments();
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
		return termDocFreq/numberOfDocuments;
	}
	
}

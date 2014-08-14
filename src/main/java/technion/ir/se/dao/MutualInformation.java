package technion.ir.se.dao;

public class MutualInformation {
	private long termADocFreq;
	private long termBDocFreq;
	private int mutualDocumentsSize;
	private long numberOfDocuments;
	
	private double termAProb;
	private double termBProb;
	private double mutualProb;
	private double mutualCompProb;
	private double termAExistsTermBNotExistsProb;
	private double termBExistsTermANotExistsProb;
	
	
	public MutualInformation(long termADocFreq, long termBDocFreq,
			int mutualDocumentsSize, long numberOfDocuments) {
		this.termADocFreq = termADocFreq;
		this.termBDocFreq = termBDocFreq;
		this.mutualDocumentsSize = mutualDocumentsSize;
		this.numberOfDocuments = numberOfDocuments;
	}

	public long getTermADocFreq() {
		return this.termADocFreq;
	}

	public long getNumberOfDocuments() {
		return this.numberOfDocuments;
	}

	public void setTermAProb(double termAProb) {
		this.termAProb = termAProb;
	}
	
	public void setTermBProb(double termBProb) {
		this.termBProb = termBProb;
	}

	public long getTermBDocFreq() {
		return this.termBDocFreq;
	}

	public int getMutualDocumentsSize() {
		return this.mutualDocumentsSize;
	}

	public void setMutualProb(double mutualProb) {
		this.mutualProb = mutualProb;
	}

	public double getTermAProb() {
		return this.termAProb;
	}

	public double getMutualProb() {
		return this.mutualProb;
	}

	public void setOnlyTermAExistsProb(double prob) {
		this.termAExistsTermBNotExistsProb = prob;
	}

	public double getTermBProb() {
		return this.termBProb;
	}

	public void setOnlyTermBExists(double prob) {
		this.termBExistsTermANotExistsProb = prob;
	}

	public double getOnlyTermAExist() {
		return this.termAExistsTermBNotExistsProb;
	}

	public double getOnlyTermBExists() {
		return this.termBExistsTermANotExistsProb;
	}

	public void setMutualCompProb(double prob) {
		this.mutualCompProb = prob;
	}

	public double getMutualCompProb() {
		return this.mutualCompProb;
	}
	
	public double getTermACompProb() {
		return 1 - this.termAProb;
	}
	
	public double getTermBCompProb() {
		return 1 - this.termBProb;
	}
	
	
}

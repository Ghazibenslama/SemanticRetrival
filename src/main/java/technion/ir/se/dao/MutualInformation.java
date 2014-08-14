package technion.ir.se.dao;

public class MutualInformation {
	private long termADocFreq;//c(a=1)
	private long termBDocFreq;//c(b=1)
	private int mutualDocumentsSize;//c(a=1,b=1)
	private long numberOfDocuments;//N
	
	private double termAProb;// P(a=1)
	private double termBProb;//p(b=1)
	private double mutualProb;// p(a=1,b=1)
	private double mutualCompProb;// p(a=0,b=0)
	private double termAExistsTermBNotExistsProb;//p(a=1,b=0)
	private double termBExistsTermANotExistsProb;//p(a=0,b=1)
	
	
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

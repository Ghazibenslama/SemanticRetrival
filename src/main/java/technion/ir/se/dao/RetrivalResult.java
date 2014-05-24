package technion.ir.se.dao;

public class RetrivalResult {
	private double score;
	private int docId;
	private int begin;
	private int end;
	private long number;
	private int ordinal;
	private int parentOrdinal;
	private String nameIdDisk;
	
	public RetrivalResult(double score, int docid, int begin, int end, long number,
			int ordinal, int parentOrdinal, String nameIdDisk) {
		this.score = score;
		this.docId = docid;
		this.begin = begin;
		this.end = end;
		this.number = number;
		this.ordinal = ordinal;
		this.parentOrdinal = parentOrdinal;
		this.nameIdDisk = nameIdDisk;
	}
	
	public int getDocumentId() {
		return this.docId;
	}

	public double getScore() {
		return this.score;
	}
	
}

package technion.ir.se.dao;

public class QrelsRecord {
	private String documentID;
	private Integer rank;
	private RelevenceType relevence;
	private String queryID;
	
	public QrelsRecord(String queryID, String documentID, Integer rank,
			RelevenceType relevence) {
		this.documentID = documentID;
		this.rank = rank;
		this.relevence = relevence;
		this.queryID = queryID;
	}

	public String getDocumentID() {
		return documentID;
	}

	public Integer getRank() {
		return rank;
	}

	public RelevenceType isRelevence() {
		return relevence;
	}

	public String getQueryID() {
		return queryID;
	}
	
	
}

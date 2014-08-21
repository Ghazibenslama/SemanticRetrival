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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documentID == null) ? 0 : documentID.hashCode());
		result = prime * result + ((queryID == null) ? 0 : queryID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QrelsRecord other = (QrelsRecord) obj;
		if (documentID == null) {
			if (other.documentID != null)
				return false;
		} else if (!documentID.equals(other.documentID))
			return false;
		if (queryID == null) {
			if (other.queryID != null)
				return false;
		} else if (!queryID.equals(other.queryID))
			return false;
		return true;
	}
	
	
}

package technion.ir.se.dao;

public class ResultFormat implements Comparable<ResultFormat>{
	
	private String queryID;
	private String q0;// default value Q0
	private String documentID;
	private int rank;
	private double score;
	private String indri;// default value Indri
	
	public ResultFormat(String queryID, String q0, String documentID, int rank, double score, String indri)
	{
		this.queryID = queryID;
		this.q0 = q0;
		this.documentID = documentID;
		this.rank = rank;
		this.score = score;
		this.indri = indri;
	}
	
	public ResultFormat()
	{
		
	}
	
	public String getQueryID()
	{
		return this.queryID;
	}
	
	public String getQ0()
	{
		return this.q0;
	}
	
	public String getDocumentID()
	{
		return this.documentID;
	}
	
	public int getRank()
	{
		return this.rank;
	}
	
	public double getScore()
	{
		return this.score;
	}
	
	public String getIndri()
	{
		return this.indri;
	}
	
	public void setScore(double newScore)
	{
		this.score = newScore;
	}
	
	public void setRank (int newRank)
	{
		this.rank = newRank;
	}

	@Override
	public int compareTo(ResultFormat otherResultFormat) {
		if (otherResultFormat == null ) {
			return -1;
		}
		
		if (this.getScore() < otherResultFormat.getScore()) {
			return 1;
		} else if (this.getScore() > otherResultFormat.getScore()){ 
			return -1;
		}
		return 0;
	}
	

}

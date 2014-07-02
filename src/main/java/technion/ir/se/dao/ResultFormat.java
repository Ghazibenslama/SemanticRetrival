package technion.ir.se.dao;

public class ResultFormat implements Comparable<ResultFormat>{
	
	private static final String INDRI = "indri";
	private static final String Q0 = "q0";
	private String queryID;
	private String q0;// default value Q0
	private String documentID;
	private int rank;
	private double score;
	private String indri;// default value Indri
	
	public ResultFormat(String queryID, String documentID, int rank, double score)
	{
		this.queryID = queryID;
		this.documentID = documentID;
		this.rank = rank;
		this.score = score;
		
		this.q0 = Q0;
		this.indri = INDRI;
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
			throw new NullPointerException(String.format("Tried to comapre %s with null", this.documentID));
		}

		//case values are equal. Can also be that both values are null 
		if (this.getScore() == otherResultFormat.getScore()) {
			return 0;
		} else if (this.getScore() < otherResultFormat.getScore()) {
			//case this is smaller than other
			return -1;
		} else if (this.getScore() > otherResultFormat.getScore()) {
			//case this is bigger than other
			return 1;
		}
		return 0;
	}
	

}

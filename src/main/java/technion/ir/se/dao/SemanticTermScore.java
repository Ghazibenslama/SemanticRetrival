package technion.ir.se.dao;

public class SemanticTermScore implements Comparable<SemanticTermScore>
{
	private String term;
	private double semanticScore;
	
	public SemanticTermScore (String term, double semanticScore )
	{
		this.term = term;
		this.semanticScore = semanticScore; 
	}
	
	public SemanticTermScore()
	{
		
	}
	
	public String getTerm ()
	{
		return this.term;
	}
	
	public double getSemanticScore()
	{
		return this.semanticScore;
	}
	
	public void setTerm(String term)
	{
		this.term = term;
	}
	
	public void setScore(double score)
	{
		this.semanticScore = score;
	}
	
	
	
	@Override
	public int compareTo(SemanticTermScore otherResultFormat) {
		if (otherResultFormat == null ) {
			throw new NullPointerException(String.format("Tried to comapre %s with null", this.term));
		}
		
		if (this.getSemanticScore() < otherResultFormat.getSemanticScore()) {
			return -1;
		} else if (this.getSemanticScore() > otherResultFormat.getSemanticScore()){ 
			return 1;
		}
		return 0;
	}

}

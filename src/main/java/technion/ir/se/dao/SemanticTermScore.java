package technion.ir.se.dao;

public class SemanticTermScore implements Comparable<SemanticTermScore>
{
	private String term;
	private double semanticScore;
	
	public SemanticTermScore (String term, double semanticScore ) {
		this.term = term;
		this.semanticScore = semanticScore; 
	}
	
	public SemanticTermScore() {
		
	}
	
	public String getTerm () {
		return this.term;
	}
	
	public double getSemanticScore() {
		return this.semanticScore;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setScore(double score) {
		this.semanticScore = score;
	}
	
	@Override
	public int compareTo(SemanticTermScore otherResultFormat) {
		if (otherResultFormat == null ) {
			throw new NullPointerException(String.format("Tried to comapre %s with null", this.term));
		}
		
		//case values are equal. Can also be that both values are null 
		if (this.getSemanticScore() == otherResultFormat.getSemanticScore()) {
			return 0;
		} else if (this.getSemanticScore() < otherResultFormat.getSemanticScore()) {
			//case this is smaller than other
			return -1;
		} else if (this.getSemanticScore() > otherResultFormat.getSemanticScore()) {
			//case this is bigger than other
			return 1;
		}
		
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SemanticTermScore) {
			SemanticTermScore other = (SemanticTermScore) obj;
			if (this.getSemanticScore() == other.getSemanticScore()) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}
	
	

}

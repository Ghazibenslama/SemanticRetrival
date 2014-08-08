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
		int compare = Double.compare(this.getSemanticScore(), otherResultFormat.getSemanticScore());
		return compare;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(semanticScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SemanticTermScore)) {
			return false;
		}
		SemanticTermScore other = (SemanticTermScore) obj;
		int areEquals = Double.compare(this.getSemanticScore(), other.getSemanticScore());
		return (areEquals == 0) ? true : false;
	}

}

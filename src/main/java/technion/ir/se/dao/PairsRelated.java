package technion.ir.se.dao;

import java.util.List;
import java.util.TreeMap;

public class PairsRelated 
{
	private List<String> pairs;
	private String queryID;
	
	public PairsRelated (List<String> pairs , String queryID )
	{
		this.pairs = pairs;
		this.queryID = queryID;
	}
	
	public List<String> getPairs()
	{
		return this.pairs;
	}

	
	public String getQueryID()
	{
		return this.queryID;
	}

}

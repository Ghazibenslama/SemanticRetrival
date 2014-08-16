package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import technion.ir.se.dao.Query;

public class PairsRelatedLogic
 {
	private MutualInformationLogic mil;
	private List<Query> queries;
	
	public PairsRelatedLogic (MutualInformationLogic mil , List<Query> queries )
	{
		this.mil = mil;
		this.queries = queries;
	}
	
	public Map<String, List<List<String>>> clacRelatedPairs()
	{
		Map<String, List<List<String>>> pairRelatedMap = new HashMap<String,List<List<String>>>(); 
		//List<PairsRelated> pairRelatedList = new ArrayList <PairsRelated>();
		for (Query query : queries)
		{
			ArrayList<List<String>> resultLists = new ArrayList<List<String>>();
			try 
			{
				resultLists = (ArrayList<List<String>>) mil.findPhrases(query);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (resultLists.size() > 0)
				for (List<String> list : resultLists) 
				{
					pairRelatedMap.put(mil.getQueryID(), resultLists);
				}
				
		}
		return pairRelatedMap;
	}

 }

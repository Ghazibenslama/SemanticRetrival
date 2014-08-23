package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import technion.ir.se.dao.Query;

public class PairsRelatedLogic
 {
	
	private final Logger logger = Logger.getLogger(PairsRelatedLogic.class);
	private List<Query> queries;
	
	protected MutualInformationLogic mil;
	
	public PairsRelatedLogic (MutualInformationLogic mil , List<Query> queries ) {
		this.mil = mil;
		this.queries = queries;
	}
	
	/**
	 * Override this method if you wish to change the way phrases are constructed for each query
	 * @param query
	 * @return
	 * @throws Exception
	 */
	protected List<List<String>> findPhrases(Query query) throws Exception {
		return mil.findPhrases(query);
	}
	
	/**
	 * The method defines a template of 'findRelatedPairs' of terms that have some 'connection' between them.<br>
	 * You can change the way term are constructed as 'pairs' by overriding {@link #findPhrases(Query)}
	 * @return
	 */
	public final Map<String, List<List<String>>> findRelatedPairs() {
		Map<String, List<List<String>>> pairRelatedMap = new HashMap<String,List<List<String>>>(); 
		for (Query query : queries) {
			List<List<String>> resultLists = new ArrayList<List<String>>();
			try  {
				resultLists = this.findPhrases(query);
			} catch (Exception e) {
				logger.error("Failed to find pairs of terms (phrases) that have high Mutual Information value" , e);
			}
			
			if (resultLists.size() > 0) {
				pairRelatedMap.put(mil.getQueryID(), resultLists);
			}
				
		}
		return pairRelatedMap;
	}
	
 }

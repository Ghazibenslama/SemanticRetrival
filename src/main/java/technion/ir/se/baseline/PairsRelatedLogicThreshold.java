package technion.ir.se.baseline;

import java.util.List;

import org.apache.log4j.Logger;

import technion.ir.se.dao.Query;

public class PairsRelatedLogicThreshold extends PairsRelatedLogic {

	private double threshold;
	private final Logger logger = Logger.getLogger(PairsRelatedLogicThreshold.class);


	public PairsRelatedLogicThreshold(MutualInformationLogic mil, List<Query> queries, double threshold) {
		super(mil, queries);
		this.threshold = threshold;
	}

	@Override
	protected List<List<String>> findPhrases(Query query) throws Exception {
		logger.debug("finding phrases with threshold: " + threshold);
		return mil.findPhrases(query, threshold);
	}

	
}

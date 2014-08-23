package technion.ir.se.trainers;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.RM3Rule;
import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.exception.RecordsNotExistsException;
import technion.ir.se.indri.SearchEngine;
import technion.ir.se.trec.eval.AveragePercisionCalculator;
import technion.ir.se.trec.eval.TrecEvalDataFile;
import technion.ir.se.trec.eval.TrecEvalParser;

public abstract class AbstractParamTrainer implements IParamTrainer {
	
	protected final int MU = 1000;
	protected List<Query> queries;
	protected int numOfDocsToRetrive;
	protected final String FBIS_QRELS_FILE = "/2004relevanceFBIS.qrels";
	protected TrecEvalParser parser;
	
    private final Logger logger = Logger.getLogger(AbstractParamTrainer.class);


	public AbstractParamTrainer() {
		this.parser = new TrecEvalParser();
		
	}
	
	public AbstractParamTrainer(List<Query> queries, int numOfdocsToRetrive) {
		this();
		this.queries = queries;
		this.numOfDocsToRetrive = numOfdocsToRetrive;
	}
	
	@Override
	public abstract Map<Double, Double> train();
	
	public void setQueries(List<Query> queries) {
		this.queries = queries;
	}

	public void setNumOfDocsToRetrive(int numOfdocsToRetrive) {
		this.numOfDocsToRetrive = numOfdocsToRetrive;
	}

	protected double runQuery(TrecEvalDataFile goldResults, RM3Rule rule, Query query)
			throws Exception, RecordsNotExistsException {
				SearchEngine engine = SearchEngine.getInstance();
				String queryID = query.getId();
				
				List<RetrivalResult> results = engine.runQuery(numOfDocsToRetrive, rule.toIndriRule(), query.getQueryText());
				TrecEvalDataFile retrivedResults = parser.convertList(results, queryID);
				AveragePercisionCalculator calculator = new AveragePercisionCalculator(retrivedResults, goldResults);
				double avgPercision = calculator.calcAveragePercisionSocre(queryID);
				logger.debug("Retrived docs: " + calculator.getNumberOfRetrivedResults(queryID));
				logger.debug("Gold docs: " + calculator.getNumberOfGoldResults(queryID));
				logger.debug("relevant docs: " + calculator.getNumberOfReleventRetrivedResults(queryID));
				
				if (Double.isNaN(avgPercision) || Double.isInfinite(avgPercision)) {
					return 0;
				}
				return avgPercision;
			}

}

package technion.ir.se.trainers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import technion.ir.se.dao.Query;
import technion.ir.se.dao.RM3Rule;
import technion.ir.se.exception.RecordsNotExistsException;
import technion.ir.se.trec.eval.TrecEvalDataFile;

public class FeedbackDocumentsTrainer extends AbstractParamTrainer {

    private final Logger logger = Logger.getLogger(FeedbackDocumentsTrainer.class);

	public FeedbackDocumentsTrainer(List<Query> queries, int numOfdocsToRetrive) {
		super(queries, numOfdocsToRetrive);
	}
	
	public FeedbackDocumentsTrainer() {
		super();
	}
	
	@Override
	public Map<Double, Double> train() {
		Map<Double, Double> scores = new HashMap<Double, Double>();
		try {
			TrecEvalDataFile goldResults = parser.convertFile(FBIS_QRELS_FILE);
			double sumOfAvgPer = 0;
			
			for (int parameter = 5; parameter <= 100; parameter+= 5) {
				logger.info("Traing parameter 'Feedback Documents' with value of " + parameter);
				RM3Rule rule = new RM3Rule(MU);
				for (Query query : queries) {
					double avgPercisionForQuery = runQuery(goldResults, rule, query);
					sumOfAvgPer += avgPercisionForQuery;
				}
				scores.put((double) parameter, sumOfAvgPer/queries.size());
				sumOfAvgPer = 0;
			}
		} catch (FileNotFoundException e) {
			logger.fatal("Failed to convert golden result file: " + FBIS_QRELS_FILE, e);
		} catch (IOException e) {
			logger.fatal("Failed to convert golden result file: " + FBIS_QRELS_FILE, e);
		} catch (RecordsNotExistsException e) {
			logger.fatal("Failed to run query", e);
		} catch (Exception e) {
			logger.fatal("Failed to run query", e);
		}
		return scores;
	}

}

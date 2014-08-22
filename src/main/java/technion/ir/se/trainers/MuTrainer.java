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

public class MuTrainer extends AbstractParamTrainer {
	
    private final Logger logger = Logger.getLogger(MuTrainer.class);


	public MuTrainer(List<Query> queries, int numOfdocsToRetrive) {
		super(queries, numOfdocsToRetrive);
	}
	
	public MuTrainer() {
		super();
	}

	@Override
	public Map<Integer, Double> train() {
		Map<Integer, Double> scores = new HashMap<Integer, Double>();
		try {
			TrecEvalDataFile goldResults = parser.convertFile(FBIS_QRELS_FILE);
			double sumOfAvgPer = 0;
			
			for (int lambda = 100; lambda <= 4000; lambda+= 25) {
				logger.info("Traing parameter 'Mu'with value of " + lambda);
				RM3Rule rule = new RM3Rule(lambda);
				for (Query query : queries) {
					double avgPercisionForQuery = runQuery(goldResults, rule, query);
					sumOfAvgPer += avgPercisionForQuery;
				}
				double meanAvgPer = sumOfAvgPer/queries.size();
				scores.put(lambda, meanAvgPer);
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

package technion.ir.se.baseline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.exception.RecordsNotExistsException;
import technion.ir.se.trainers.IParamTrainer;
import technion.ir.se.trec.eval.AveragePercisionCalculator;
import technion.ir.se.trec.eval.TrecEvalDataFile;
import technion.ir.se.trec.eval.TrecEvalParser;

public class MutualInformationTrainer extends BaseLine implements IParamTrainer {
    
	private final Logger logger = Logger.getLogger(MutualInformationTrainer.class);
	private double threshold;

	public MutualInformationTrainer() {
		super();
	}

	@Override
	public Map<Double, Double> train() {
		TrecEvalParser parser = new TrecEvalParser();
		HashMap<Double, Double> scores = new HashMap<Double, Double>();
		try {
			String qrelsFileName = Utils.readProperty("qrels.file");
			logger.debug("Finshed reading qrels file: " + qrelsFileName);
			TrecEvalDataFile goldResults = parser.convertFile(qrelsFileName);
			for (double threshold = 0.1; threshold <= 2.0; threshold+=0.3) {
				this.threshold = threshold;
				logger.debug("About to run Training of MutualInformationTrainer with threshold=" + this.threshold);
				File mutualInformationResults = this.createAlternatives();
				TrecEvalDataFile searchEngineResults = parser.convertFile(mutualInformationResults);
				
				logger.debug("Calculating MAP score for run with threshold=" + this.threshold);
				AveragePercisionCalculator calculator = new AveragePercisionCalculator(searchEngineResults, goldResults);
				double sumOfAvgPer = 0;
				for (Query query : queries) {
					double socre = calculator.calcAveragePercisionSocre(query.getId());
					logger.debug("score of query #" + query.getId() + " is: " + socre);
					if (!Double.isNaN(socre) && !Double.isInfinite(socre)) {
						sumOfAvgPer += socre;
					}
				}
				double meanAvgPer = sumOfAvgPer/queries.size();
				scores.put(threshold, meanAvgPer);
				logger.debug("finished to run Training of MutualInformationTrainer with threshold=" + this.threshold);
			}
		} catch (FileNotFoundException e) {
			logger.fatal("failed to convert file to 'TrecEvalDataFile'", e);
		} catch (IOException e) {
			logger.fatal("failed to convert file to 'TrecEvalDataFile'", e);
		} catch (RecordsNotExistsException e) {
			logger.fatal("Failed to calculate Average Percision", e);
		}
		return scores;
	}

	@Override
	protected File writeResultsToFile(StringBuilder trecMap, String[] rules)
			throws IOException {	
		File tempFile = File.createTempFile("MutualInformationRunResults", ".txt");
		return Utils.writeFile(trecMap, tempFile);
	}

	@Override
	protected Map<String, List<List<String>>> findRelatedQueriesTerms() {
		MutualInformationLogic mil = new MutualInformationLogic(engine);
		PairsRelatedLogicThreshold logicThreshold = new PairsRelatedLogicThreshold(mil, queries, threshold);
		return logicThreshold.findRelatedPairs();
	}
	
	
}

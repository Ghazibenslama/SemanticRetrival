package technion.ir.se.baseline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Query;
import technion.ir.se.trainers.IParamTrainer;
import technion.ir.se.trainers.ParamTrainerFactory;

public class Trainer {

	private final Logger logger = Logger.getLogger(Trainer.class);
			
	public void trainBaseLine(String parameterType) {
		try {
			List<Query> queries = Utils.readQueries();
			IParamTrainer trainer = ParamTrainerFactory.factory(parameterType, queries, BaseLine.NUMBER_OF_DOCUMNETS_TO_RETRIVE);
			Map<Double, Double> trainingResult = trainer.train();
			
			StringBuilder builder = new StringBuilder();
			builder.append(String.format(Utils.RESULT_LINE_FORMAT, "Mu", "MAP"));
			Utils.writeTrainingResultsInCsv(trainingResult, builder, "Mu_Train");
			
		} catch (IOException e) {
			logger.error("failed to read queris", e);
		} catch (URISyntaxException e) {
			logger.error("failed to read queris", e);
		}
		
	}
	
	public void trainMutualInformation() {
		IParamTrainer trainer = new MutualInformationTrainer();
		Map<Double, Double> trainingResult = trainer.train();
		
		StringBuilder builder = new StringBuilder();
		builder.append(String.format(Utils.RESULT_LINE_FORMAT, "Threshold", "MAP"));
		Utils.writeTrainingResultsInCsv(trainingResult, builder, "MutualInformation_Train");
	}

}

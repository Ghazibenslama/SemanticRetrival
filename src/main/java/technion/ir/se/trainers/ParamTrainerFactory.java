package technion.ir.se.trainers;

import java.util.List;

import technion.ir.se.dao.Query;

public class ParamTrainerFactory {
	
	public enum ParameterType {
		
		MU( new MuTrainer());
		
		/*MU( new MuTrainer()), 
		FBDOCS, 
		FBTERMS;*/
		
		private AbstractParamTrainer trainer;
		
		private ParameterType(AbstractParamTrainer trainer) {
			this.trainer = trainer;
		}
		
		public AbstractParamTrainer getImpl() {
			return trainer;
		}
	}

	public static IParamTrainer factory(String paramType, List<Query> queries, int numOfDocstoRetrive) {
		ParameterType type = ParameterType.valueOf(paramType);
		AbstractParamTrainer trainer = type.getImpl();
		trainer.setNumOfDocsToRetrive(numOfDocstoRetrive);
		trainer.setQueries(queries);
		
		return trainer;
	}
}

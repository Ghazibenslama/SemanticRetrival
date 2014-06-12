package technion.ir.se.baseline;

import technion.ir.se.Utils.Utils;
import technion.ir.se.dao.Feedback;

public class StrategyFactory {

	private static final String WINDOW_SIZE_KEY = "window.size";

	public enum Strategy {
		BetweenQueryTermsStrategy( new BetweenQueryTermsStrategy() ), 
		FixedWindowStrategy( new FixedWindowStrategy() ), 
		HalfDistanceBetweenQueryTermsStrategy( new HalfDistanceBetweenQueryTermsStrategy() );

		private Strategy(AbstractStrategy windowSize) {
			this.strategy = windowSize;
		}
		private AbstractStrategy strategy;
		
		public AbstractStrategy getImpl() {
			return strategy;
		}
		
	}
	
	public static AbstractStrategy factory(String windowStrategy, Feedback feedback) {
			Strategy strategyName = null;
			try {
				strategyName = Strategy.valueOf(windowStrategy);
			} catch (Exception e) {
				String message = String.format("Invalid strategy name in properties file. Name '%s' in invalid", windowStrategy);
				System.err.println(message);
				System.exit(1);
			}
			
			AbstractStrategy abstractStrategy = strategyName.getImpl();
			abstractStrategy.setFeedback(feedback);
			if (abstractStrategy instanceof FixedWindowStrategy) {
				String windowSize = Utils.readProperty(WINDOW_SIZE_KEY);
				abstractStrategy.setWindowSize(Integer.valueOf(windowSize));
			}
			return abstractStrategy;
	}

}

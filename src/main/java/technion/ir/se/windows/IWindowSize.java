package technion.ir.se.windows;

import java.util.List;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;

public interface IWindowSize {

	/**
	 * Given a relevance feedback of a {@link Query}, this method generates the windows that should be used
	 * by a semantic algorithm.
	 * The windows are tightly coupled with the feedback. 
	 * @param feedback
	 * @param queryTerms
	 * @return
	 */
	public List<TextWindow>getWindows(Feedback feedback, Query query);
	
	
	/**
	 * @param windowSize - Set hard coded the window size
	 */
	public void setWindowSize(int windowSize);
}

package technion.ir.se.windows;

import java.util.ArrayList;
import java.util.List;

import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.TextWindow;

public abstract class AbstractStrategy implements IWindowSize {

	protected Feedback feedback;

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}
	
	public List<String> getTermsInWindow(TextWindow window) {
		ArrayList<String> resultList = new ArrayList<String>();
		for (int i = window.getWindowStart(); i <= window.getWindowEnd(); i++) {
			resultList.add(feedback.getTerms().get(i));
		}
		return resultList;
	}
	


}
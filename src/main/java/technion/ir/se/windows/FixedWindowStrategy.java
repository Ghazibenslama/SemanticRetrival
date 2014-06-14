package technion.ir.se.windows;

import java.util.ArrayList;
import java.util.List;

import technion.ir.se.baseline.AbstractStrategy;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;

public class FixedWindowStrategy extends AbstractStrategy {

	private int fixedWindowSize;
	private int numberOfTerms;
	@Override
	
	public List<TextWindow> getWindows(Feedback feedback, Query query) {
		numberOfTerms = feedback.getNumberOfTerms();
		int numberOfwindows = calcNumberOfWindows();
		ArrayList<TextWindow> windows = new ArrayList<TextWindow>();
		
		windows.add( createFirstWindow() );
		windows.addAll(createOtherWindows(numberOfwindows));
		
		return windows;
	}

	private List<TextWindow> createOtherWindows(int numberOfwindows) {
		ArrayList<TextWindow> list = new ArrayList<TextWindow>();
		for (int i = 1; i < numberOfwindows-1; i++) {
			int windowBegin = i * fixedWindowSize;
			int windowEnd = windowBegin + fixedWindowSize -1;
			TextWindow window = createWindow(windowBegin, windowEnd);
			list.add(window);
		}
		list.add(createLastWindow());
		return list;
	}

	private TextWindow createLastWindow() {
		TextWindow lastWindow = null;
		if (numberOfTerms > fixedWindowSize) {
			lastWindow = this.createWindow( (calcNumberOfWindows()-1)*fixedWindowSize, numberOfTerms-1);
		}
		return lastWindow;
	}

	private TextWindow createWindow(int windowBegin, int windowEnd) {
		int windowsEnd = calcWindowEndIndex(windowEnd);
		return new TextWindow(windowBegin, windowsEnd);
	}

	private int calcNumberOfWindows() {
		return numberOfTerms/fixedWindowSize + (numberOfTerms%fixedWindowSize > 0 ? 1:0) ;
	}

	private TextWindow createFirstWindow() {
		int firstWindowEndIndex = calcWindowEndIndex(fixedWindowSize - 1);
		return this.createWindow(0, firstWindowEndIndex);
	}

	private int calcWindowEndIndex(int endIndex) {
		return Math.min(numberOfTerms-1, endIndex);
	}

	@Override
	public void setWindowSize(int windowSize) {
		fixedWindowSize = windowSize;
	}

}

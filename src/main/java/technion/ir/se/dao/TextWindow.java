package technion.ir.se.dao;

public class TextWindow {
	private int windowStart;
	private int windowEnd;
	
	public TextWindow(int windowStart, int windowEnd) {
		this.windowStart = windowStart;
		this.windowEnd = windowEnd;
	}
	
	public int getWindowStart() {
		return windowStart;
	}
	public int getWindowEnd() {
		return windowEnd;
	}
	
	public int getWindowSize() {
		return windowEnd - windowStart +1;
	}
	
}

package technion.ir.se.Types;

public enum RelevenceType {
	YES, NO;
	
	public static RelevenceType isRelevence(String arg) {
		if (Integer.valueOf(arg) == 0) {
			return NO;
		}
		return YES;
	}
}

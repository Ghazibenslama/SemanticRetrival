package technion.ir.se.baseline;

import java.util.Comparator;
import java.util.Map;

public class DoubleComparator implements Comparator<String>
{
	private Map<String, Double> base;
	public DoubleComparator(Map<String, Double> base) {
	        this.base = base;
	    }
	
    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String keyA, String keyB) {
        if (base.get(keyA) >= base.get(keyB)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

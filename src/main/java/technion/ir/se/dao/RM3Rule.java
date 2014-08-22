package technion.ir.se.dao;

public class RM3Rule {
	 private String method;
	 private int mu;
	 
	 public RM3Rule(int mu) {
		 this.method = "dir";
		 this.mu = mu;
	 }
	 
	 public String[] toIndriRule() {
		 return new String[]{ "method:"+method, "mu:"+mu};
	 }
}

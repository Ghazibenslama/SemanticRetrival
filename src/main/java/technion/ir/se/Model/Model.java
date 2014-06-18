package technion.ir.se.Model;

import java.util.ArrayList;
import java.util.List;

public class Model {

	private static Model instance = null;
	private List<String> model;
	
	private Model() {
		model = null;
	}
	
	public static Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public void setModel(ArrayList<String> model) {
		this.model = model;
	}
	
	public List<String> getModel() {
		return this.model;
	}

}

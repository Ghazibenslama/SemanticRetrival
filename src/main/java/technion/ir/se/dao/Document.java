package technion.ir.se.dao;

import java.util.List;

public class Document {
	
	private List<String> terms;
	
	public Document(List<String> termsStemed)
	{
		this.terms = termsStemed;
	}

	public List<String> getDocumentTermsStemed() {
		return this.terms;
	}


	
	
	
}

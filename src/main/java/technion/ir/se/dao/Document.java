package technion.ir.se.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Document {
	
	private String content;
	private List<String> terms;
	
	public Document(String content)
	{
		this.content = content;
		this.terms = null;
	}

	public List<String> getDocumentContent()
	{
		if (terms == null) {
			String[] contentTerms = this.content.split(" ");
			this.terms = new ArrayList<String>(Arrays.asList(contentTerms));
			
		}
		return terms;
	}
}

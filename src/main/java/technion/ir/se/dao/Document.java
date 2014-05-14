package technion.ir.se.dao;

public class Document {
	
	private String docID;
	private String docContent;
	private String type;//Doc format i.e "WEB"/"text"/"TrecWEB"/"TrecText"
	
	
	public Document()//default CTr
	{
		
	}
	
	public Document (String _docID, String _docContent, String _type)
	{
		docID = _docID;
		docContent = _docContent;
		type = _type;
	}
	
	public String getContent()
	{
		return this.docContent;
	}

	public String getID()
	{
		return this.docID;
	}
}

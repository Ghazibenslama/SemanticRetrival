package technion.ir.se.dao;

public class Query {
	private String id;
	private String content;
	
	public Query(String id, String content) {
		this.id = id;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public String getQueryText() {
		return content;
	}
}

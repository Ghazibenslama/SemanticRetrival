package technion.ir.se.map;

import java.util.List;

import technion.ir.se.dao.RetrivalResult;
import technion.ir.se.indri.RunQuery;

public class MapFormatGenerator {

	private static final String MAP_FORMAT = "%d Q0 %s %d %d Indri";
	private static final int NUMBER_OF_RETRIVED_DOCUMENTS = 1000;
	private RunQuery runQuery;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public List<RetrivalResult> runGivenQuery(String query) throws Exception {
		String[] rules = new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		List<RetrivalResult> returnedDocuments = runQuery.runQuery(NUMBER_OF_RETRIVED_DOCUMENTS, rules, query);
		return returnedDocuments;
	}
	
	public StringBuilder createMapFormatForQuery(int queryId, List<RetrivalResult> results){
		StringBuilder builder = new StringBuilder();
		int rank = 1;
		for (RetrivalResult retrivalResult : results) {
			String querySingleResult = String.format(MAP_FORMAT, queryId, retrivalResult.getDocumentId(), rank, retrivalResult.getScore());
			builder.append(querySingleResult);
			builder.append("\n");
			rank++;
		}
		return builder;
		
	}
	
}

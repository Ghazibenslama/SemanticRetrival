package technion.ir.se.trec.eval;

import java.util.HashMap;
import java.util.Map;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

public class TrecEvalDataFile {
	private static final String MESSAGE_FORMAT = "There are no records for query with ID: %s";
	private Map<String, QueryTrecEvalRecords> dataFile;
	
	public TrecEvalDataFile() {
		dataFile = new HashMap<String, QueryTrecEvalRecords>();
	}
	
	public QueryTrecEvalRecords getRecordsForQuery(String queryID) throws RecordsNotExistsException {
		QueryTrecEvalRecords queryTrecEvalRecords = dataFile.get(queryID);
		if (queryTrecEvalRecords == null) {
			throw new RecordsNotExistsException(String.format(MESSAGE_FORMAT, queryID));
		}
		return queryTrecEvalRecords;
	}
	
	public boolean doesContainRecordsForQuery(String queryID) {
		return dataFile.containsKey(queryID);
	}
	
	public void addRecordsToQuery(String queryID, QrelsRecord record) {
		if (dataFile.containsKey(queryID)) {
			dataFile.get(queryID).add(record);
		} else {
			QueryTrecEvalRecords trecEvalDataFile = new QueryTrecEvalRecords();
			trecEvalDataFile.add(record);
			dataFile.put(queryID, trecEvalDataFile);
		}
	}

}

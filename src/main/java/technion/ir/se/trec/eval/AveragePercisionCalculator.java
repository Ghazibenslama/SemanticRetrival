package technion.ir.se.trec.eval;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

public class AveragePercisionCalculator {
	private TrecEvalDataFile qerls;
	private TrecEvalDataFile goldResults;
	
    private final Logger logger = Logger.getLogger(AveragePercisionCalculator.class);

	
	public AveragePercisionCalculator(TrecEvalDataFile qerls, TrecEvalDataFile goldResults) {
		this.qerls = qerls;
		this.goldResults = goldResults;
	}
	
	public double calcAveragePercisionSocre(String queryID) throws RecordsNotExistsException {
		int numberOfDocuments = qerls.getNumberOfDocuments(queryID);
		return this.calcPercisionAt(queryID, numberOfDocuments);
	}
	
	public double calcPercisionAt(String queryID, int at) throws RecordsNotExistsException {
		List<QrelsRecord> rankedDocuments = qerls.getRankedDocuments(queryID, at);
		
		double releventDocsFound = 0;
		double docIndex = 1;
		double percisionSum = 0;
		
		for (QrelsRecord qrelsRecord : rankedDocuments) {
			if (goldResults.isDocumentRelevent(qrelsRecord)) {
				releventDocsFound++;
				percisionSum+= releventDocsFound / docIndex;
			}
			docIndex++;
		}
		
		int numOfDocs = rankedDocuments.size();
		return percisionSum / numOfDocs; 
	}
	
	public int getNumberOfGoldResults(String queryID) {
		try {
			return getNumberOfRecords(goldResults, queryID);
		} catch (RecordsNotExistsException e) {
			logger.error("Failed to find golden records for query. ID:" + queryID, e);
			return 0;
		}
	}
	
	public int getNumberOfRetrivedResults(String queryID) {
		try {
			return getNumberOfRecords(qerls ,queryID);
		} catch (RecordsNotExistsException e) {
			logger.error("Failed to find retrived records for query. ID:" + queryID, e);
			return 0;
		}
	}

	private int getNumberOfRecords(TrecEvalDataFile dataFile, String queryID) throws RecordsNotExistsException {
		return dataFile.getRecordsForQuery(queryID).getRecords().size();
	}
	
	public int getNumberOfReleventRetrivedResults(String queryID) {
		try {
			Set<QrelsRecord> relevantDocuments = goldResults.getRelevantDocuments(queryID);
			List<QrelsRecord> recordsForQuery = qerls.getRecordsForQuery(queryID).getRecords();
			recordsForQuery.retainAll(relevantDocuments);
			return recordsForQuery.size();
		} catch (RecordsNotExistsException e) {
			logger.error("failed to calculate Number Of Relevent Retrived records. queryID: " + queryID, e);
			return 0;
		}
		
	}

}

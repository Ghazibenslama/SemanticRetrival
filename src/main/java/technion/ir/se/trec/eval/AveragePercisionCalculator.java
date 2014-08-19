package technion.ir.se.trec.eval;

import java.util.List;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

public class AveragePercisionCalculator {
	private TrecEvalDataFile qerls;
	private TrecEvalDataFile goldResults;
	
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
	

}

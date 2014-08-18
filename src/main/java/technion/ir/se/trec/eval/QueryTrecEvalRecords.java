package technion.ir.se.trec.eval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.dao.RelevenceType;

public class QueryTrecEvalRecords {
	private Set<QrelsRecord> releventRecords;
	private TreeMap<Integer, QrelsRecord> rankedRecords;
	
	public QueryTrecEvalRecords() {
		releventRecords = new HashSet<QrelsRecord>();
		rankedRecords = new TreeMap<Integer, QrelsRecord>();
	}
	public void add(QrelsRecord record) {
		rankedRecords.put(record.getRank(), record);
		if (RelevenceType.YES == record.isRelevence() ) {
			releventRecords.add(record);
		}
	}
	
	public Set<QrelsRecord> getRelevenceRecords() {
		return this.releventRecords;
	}
	
	public List<QrelsRecord> getRecords() {
		return new ArrayList<QrelsRecord>(rankedRecords.values());
	}
	
	public TreeMap<Integer, QrelsRecord> getRankedDocuments() {
		return this.rankedRecords;
	}
	
}

package technion.ir.se.dao;

import org.apache.commons.lang3.StringUtils;

import technion.ir.se.exception.FileNameNotExtracted;

public class RetrivalResult {
	private double score;
	private int indriDocId;
	private int begin;
	private int end;
	private long number;
	private int ordinal;
	private int parentOrdinal;
	private String nameIdDisk;
	private String documentId;
	
	public RetrivalResult(double score, int docid, int begin, int end, long number,
			int ordinal, int parentOrdinal, String nameIdDisk) throws FileNameNotExtracted {
		this.score = score;
		this.indriDocId = docid;
		this.begin = begin;
		this.end = end;
		this.number = number;
		this.ordinal = ordinal;
		this.parentOrdinal = parentOrdinal;
		this.nameIdDisk = nameIdDisk;
		this.documentId = extractDocumentId(nameIdDisk);
	}
	
	private String extractDocumentId(String nameIdDisk) throws FileNameNotExtracted {
		String documentId = StringUtils.substringBetween(nameIdDisk, "\\", ".txt");
		if (documentId == null) 
			{
				throw new FileNameNotExtracted("file Name returned NULL");
			}
		
		
		return documentId;
	}

	public int getIndriDocumentId() {
		return this.indriDocId;
	}

	public double getScore() {
		return this.score;
	}

	public String getDocumentId() {
		return documentId;
	}
	
}

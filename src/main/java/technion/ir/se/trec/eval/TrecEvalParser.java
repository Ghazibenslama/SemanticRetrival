package technion.ir.se.trec.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.dao.RelevenceType;
import technion.ir.se.exception.IllegalLineLength;

public class TrecEvalParser {
	private static final String MESSEAGE_FORMAT = "Line: '%s', contains %d elements";
	private static final int RETRIVAL_FILE = 6;
	private static final int QRELS_FILE = 4;

    static final Logger logger = Logger.getLogger(TrecEvalParser.class);

	
    public TrecEvalDataFile convertFile(File fileToConvert) throws FileNotFoundException, IOException {
    	TrecEvalDataFile dataFile = new TrecEvalDataFile();
    	try (BufferedReader br = new BufferedReader(new FileReader(fileToConvert))){
	    	String line = br.readLine();
	    	try {
				QrelsRecord record = this.parseLine(line);
				dataFile.addRecordsToQuery(record.getQueryID(), record);
			} catch (IllegalLineLength e) {
				logger.error("Failed to parse line" + line, e);
			}
    	}
    	return dataFile;
    }
    
	private QrelsRecord parseLine(String line) throws IllegalLineLength {
		String[] split = StringUtils.split(line);
		int numberOfMembers = split.length;
		QrelsRecord record;
		if (numberOfMembers == QRELS_FILE) {
			RelevenceType relevence = RelevenceType.isRelevence(split[3]);
			record = new QrelsRecord(split[0], split[2], -1, relevence);
		} else if (numberOfMembers == RETRIVAL_FILE) {
			record = new QrelsRecord(split[0], split[2], Integer.valueOf(split[3]), RelevenceType.YES);
		} else {
			logger.debug("Threw 'IllegalLineLength' exception");
			throw new IllegalLineLength(String.format(MESSEAGE_FORMAT, line, numberOfMembers));
		}
		return record;
	}
}

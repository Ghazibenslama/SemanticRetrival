package technion.ir.se.trec.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TrecDocument {
	private StringBuilder content;
	private String docno;
	private File fileInDisk;
	
	public File getFileFromDisk() {
		return fileInDisk;
	}

	public TrecDocument(String docno) {
		content = new StringBuilder();
		fileInDisk = null;
		this.docno = docno;
		initDocContent(docno);
	}

	private void initDocContent(String docno) {
		insertLine("<DOC>");
		insertLine(String.format("<DOCNO> %s </DOCNO>", docno));
		insertLine("<TEXT>");
	}
	private void insertLine(String line) {
		content.append(line);
		content.append("\n");
	}

	public void addLine(String line) {
		insertLine(line);
	}

	public void closeDocument(String dirPath) throws IOException {
		insertLine("</TEXT>");
		insertLine("</DOC>");
		writeFileDisk(dirPath);
	}

	private void writeFileDisk(String dirPath) throws IOException {
		File file = new File( dirPath + File.separator + docno + ".txt" );
		file.createNewFile();
		fileInDisk = file;
		writeContentToFile(file);
	}

	private void writeContentToFile(File file) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file) );
		bufferedWriter.write(content.toString());
		bufferedWriter.close();
	}
}

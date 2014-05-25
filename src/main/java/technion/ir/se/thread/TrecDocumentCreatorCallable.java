package technion.ir.se.thread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import technion.ir.se.trec.parser.TrecDocumentCreator;

public class TrecDocumentCreatorCallable implements Callable<List<File>> {

	private File file;
	private TrecDocumentCreator documentCreator;
	
	public TrecDocumentCreatorCallable(File fileToconvert) {
		this.file = fileToconvert;
		this.documentCreator = new TrecDocumentCreator();
	}
	public List<File> call() throws Exception {
		List<File> trecDocuments = null;;
		try {
			trecDocuments = documentCreator.convertTrecDocument(file);
		} catch (IOException e) {
			System.err.println("Filed converting " + file + " to documents into TrecFormat");
		}
		return trecDocuments;
	}

}

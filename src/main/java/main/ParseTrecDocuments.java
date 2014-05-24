package main;

import technion.ir.se.trec.parser.TrecDocumentCreator;

public class ParseTrecDocuments {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TrecDocumentCreator documentCreator = new TrecDocumentCreator();
		documentCreator.convertTrecDocumentsToTextDocuments("C:\\Temp\\InformationRetrival\\docs\\FBIS");
	}

}

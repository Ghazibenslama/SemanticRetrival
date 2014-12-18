package main;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import technion.ir.se.trec.parser.TrecDocumentCreator;

public class ParseTrecDocuments {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("You didn't given a path to dir with files");
			System.exit(1);
		}
		long startTime = System.currentTimeMillis();
		System.out.println(String.format("Job started at: %s", getCurrentTime(startTime)));
		TrecDocumentCreator documentCreator = new TrecDocumentCreator();
		documentCreator.convertTrecDocumentsToTextDocuments(args[0]);
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Job ended at: %s", getCurrentTime(endTime)));
		System.out.println(String.format("Total tome for executing Job: %s", getCurrentTime(endTime-startTime)));
	}
	
	private static String getCurrentTime(long timestamp) {
		Date date = new Date(timestamp);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
		String dateFormatted = formatter.format(date);
		return dateFormatted;
	}

}

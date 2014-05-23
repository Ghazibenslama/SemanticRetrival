package technion.ir.se.trec.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TrecDocumentCreator {

	public List<File> getAllFilesNames(String rootDirectory) throws FileNotFoundException {
		File root = new File(rootDirectory);
		checkIfDirectoryExists(rootDirectory, root);
		File[] listFiles = root.listFiles();
		return new ArrayList<File>(Arrays.asList(listFiles));
	}

	private void checkIfDirectoryExists(String rootDirectory, File root)
			throws FileNotFoundException {
		if (!root.isDirectory()) {
			throw new FileNotFoundException(rootDirectory + " isn't  drectory");
		}
	}
	
	public List<File> convertTrecDocument(File fileToConvert) throws IOException {
	    List<File> filesCreated= new ArrayList<File>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileToConvert))){
	    	String line = br.readLine();
	    	TrecDocument document = null;
	    	boolean isLineContainingText = false;
	    	while (line != null) {
				if (line.contains("<DOCNO>")) {
					document = createFile(line);
				}
				if (line.contains("<TEXT>")) {
					isLineContainingText = true;
				} else if (line.contains("</TEXT>")) {
					isLineContainingText = false;
					document.closeDocument(fileToConvert.getParentFile().getAbsolutePath());
					filesCreated.add(document.getFileFromDisk());
				} else if (isLineContainingText) {
					document.addLine(line);
				}

	    		line = br.readLine();
			}
	    } catch (IOException e) {
	    	System.err.println("Failed to create document from: " + fileToConvert.getAbsolutePath());
		} catch (IllegalArgumentException e) {
			System.err.println(String.format("File: %s doesn't contain <DOCNO>",  fileToConvert.getAbsolutePath()));
		}
		return filesCreated;
	}

	private TrecDocument createFile(String line) {
		String docno = StringUtils.substringBetween(line, "<DOCNO>", "</DOCNO>");
		if (docno == null) {
			throw new IllegalArgumentException(String.format("The given line: %s doesn't contain <DOCNO> tags", line)); 
		}
		TrecDocument document = new TrecDocument(docno.trim());
		return document;
	}
}

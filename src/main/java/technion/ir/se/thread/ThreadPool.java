package technion.ir.se.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class ThreadPool {
	private ExecutorService threadPool;
	private List<File> filesToConvert;
	private final Logger logger = Logger.getLogger(ThreadPool.class);

	
	public ThreadPool(List<File> filesToConvert) {
		this.filesToConvert = filesToConvert;
		int noOfThreads = Runtime.getRuntime().availableProcessors();
		this.threadPool = Executors.newFixedThreadPool(noOfThreads);
	}
	
	public List<File> performJobs() {
		List<File> result = new ArrayList<File>();
		ArrayList<Future<List<File>>> textFiles = new ArrayList<Future<List<File>>>();
		int totalOfFiles = 0;
		for (File file : filesToConvert) {
			TrecDocumentCreatorCallable worker = new TrecDocumentCreatorCallable(file);
			Future<List<File>> submit = threadPool.submit(worker);
			logger.debug(String.format("File '%s' was sent for extraction", file.getName()));
			textFiles.add(submit);
			totalOfFiles++;
		}
		
		logger.info(String.format("Total of #%d files were submited for parsing", totalOfFiles));
		JobReporter reporter = new JobReporter(textFiles, threadPool);
		reporter.start();
		
		int filesExtracted = 0;
		for (Future<List<File>> future : textFiles) {
			try {
				int size = future.get().size();
				logger.debug("Adding result of " + size + " files");
				filesExtracted += size;
				logger.debug(String.format("Extrarcted total of #%d files", filesExtracted));
				result.addAll(future.get());
			} catch (InterruptedException e) {
				logger.error("Failed to obtain result", e);
			} catch (ExecutionException e) {
				logger.error("Failed to obtain result", e);
			}
		}
		logger.info(String.format("Extrarcted total of #%d files", filesExtracted));
		return result;
	}
}

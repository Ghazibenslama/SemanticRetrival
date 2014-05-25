package technion.ir.se.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
	private ExecutorService threadPool;
	private List<File> filesToConvert;
	
	public ThreadPool(List<File> filesToConvert, int noOfThreads) {
		this.filesToConvert = filesToConvert;
		this.threadPool = Executors.newFixedThreadPool(noOfThreads);
	}
	
	public List<File> performJobs(){
		List<File> result = new ArrayList<File>();
		ArrayList<Future<List<File>>> textFiles = new ArrayList<Future<List<File>>>();
		for (File file : filesToConvert) {
			TrecDocumentCreatorCallable worker = new TrecDocumentCreatorCallable(file);
			Future<List<File>> submit = threadPool.submit(worker);
			textFiles.add(submit);
		}
		
		for (Future<List<File>> future : textFiles) {
			try {
				result.addAll(future.get());
			} catch (InterruptedException e) {
				System.err.println("Failed to obtain result");
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.err.println("Failed to obtain result");
				e.printStackTrace();
			}
		}
		return result;
	}

}

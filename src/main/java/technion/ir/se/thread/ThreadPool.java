package technion.ir.se.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private ExecutorService threadPool;
	private List<File> filesToConvert;
	
	public ThreadPool(List<File> filesToConvert) {
		this.filesToConvert = filesToConvert;
		int noOfThreads = Runtime.getRuntime().availableProcessors();
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
		
		shutdownAndAwaitTermination();
		
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
	
	void shutdownAndAwaitTermination() {
		threadPool.shutdown(); // Disable new tasks from being submitted
		try {
			int waitInSeconds = 60;
			if (!terminateTasksAndCheckIfSucced(waitInSeconds)) {
				threadPool.shutdownNow(); // Cancel currently executing tasks
				if (!terminateTasksAndCheckIfSucced(waitInSeconds)) {
					System.err.println("Pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			threadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private boolean terminateTasksAndCheckIfSucced(int secondsToWait) throws InterruptedException {
		return threadPool.awaitTermination(secondsToWait, TimeUnit.SECONDS);
	}


}

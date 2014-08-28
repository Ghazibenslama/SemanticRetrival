package technion.ir.se.thread;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class JobReporter extends Thread {

	private static final int NITHY_SECONDS = 90000;
	private final Logger logger = Logger.getLogger(JobReporter.class);

	private List<Future<List<File>>> futures;
	private ExecutorService threadPool;

	public JobReporter( List<Future<List<File>>> futures, ExecutorService threadPool) {
		this.futures = futures;
		this.threadPool = threadPool;
	}

	@Override
	public void run() {
		int lastJobsFinished = 0;
		do {
			try {
				Thread.sleep(NITHY_SECONDS);
			} catch (InterruptedException e) {
				logger.fatal("Failed to sleep process", e);
			}
			
			int jobsFinished = 0;
			int jobsInQue = 0;
			
			for (Future<List<File>> future : futures) {
				if (future.isDone()) {
					jobsFinished++;
				} else {
					jobsInQue++;
				}
			}
			logger.info(String.format("Finshed #%d jobs; has still #%d jobs", jobsFinished, jobsInQue));
			
			if (jobsFinished == lastJobsFinished) {
				shutdownAndAwaitTermination();
				break;
			}
			lastJobsFinished = jobsFinished;
		} while (lastJobsFinished != futures.size());
		logger.info("Finished tracking file converter jobs");
	}
	
	private void shutdownAndAwaitTermination() {
		threadPool.shutdown(); // Disable new tasks from being submitted
		try {
			int waitInSeconds = 60;
			if (!terminateTasksAndCheckIfSucced(waitInSeconds)) {
				threadPool.shutdownNow(); // Cancel currently executing tasks
				if (!terminateTasksAndCheckIfSucced(waitInSeconds)) {
					logger.error("Pool did not terminate");
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

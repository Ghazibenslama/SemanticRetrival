package main;

import java.io.IOException;

import technion.ir.se.baseline.BaseLine;
import technion.ir.se.baseline.Trainer;

public class RunExperiments {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			System.out.println("you are in performance\\debugging mode, press any key to continue");
			System.in.read();
		}
		BaseLine baseLine = new BaseLine();
//		baseLine.createBaseLine();
//		baseLine.createAlternatives(BaseLine.THOUSAND);
		Trainer trainer = new Trainer();
//		trainer.trainBaseLine("MU");
		trainer.trainMutualInformation();
	}

}

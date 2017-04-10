package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class PerformanceMeter {
	double limit;
	int count;
	double performanceValue;
	int minCount = 100;
	int maxCount = 100000;
	int microCounter = 0;
	PrintStream logFile;
	static int batchCounter = 0;
	
	public PerformanceMeter(int limit) {
		this.limit = 0.25;
		try {
			logFile = new PrintStream(new File("perfLog" + batchCounter + ".txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reset();
//		performanceValue = limit* 2;
//		count  = 0;
	}
	
	void newValue(int val) throws LowPerformanceException{
		count++;
		microCounter++;
		performanceValue += val*val;
		if(microCounter > minCount){
			logFile.print(performanceValue + " ");
			microCounter = 0;
			if(limit>Math.sqrt(performanceValue)/(double)count){
				throw new LowPerformanceException("Performance val: " + performanceValue + " limit: " + limit + " count: " + count);
			}
			if(maxCount < count){
				throw new LowPerformanceException("Run count exceeds the max run: " + count);
			}
		}
	}

	public void reset() {
		count = 0;
		performanceValue = limit* 2;
//		batchCounter++;
		logFile.println("");
	}

//	void newValue(Integer val) throws LowPerformanceException{
//		newValue(val.intValue());
//	}
}

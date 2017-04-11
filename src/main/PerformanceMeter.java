/*******************************************************************************
 * Copyright (C) 2017 Benedek Racz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package main;

/**
 * PerformanceMeter: Measure the performance of the generator/finder. If the
 * performance is low the generator should restart.
 * The cause:
 * 	The generation based on choosing random words, which match the other part of
 * 	the canvas. BUT if the first word is too "strange" there will be no solution,
 *  however to realise this will need a lot of time. So in the following two
 *  cases the generation will be reseted and restarted from the beginning:
 * 1: If the trials reached a maximum count. (aka. the maxCount field)
 * 2: If the trials are not so far from the "nothing". This measured the actual
 *  dimension.
 * 
 * @author Benedek Racz
 *
 */
public class PerformanceMeter {
	// A fictive minimum limit of the performance. Higher limit enable more
	// computing, lower limit resets earlier. See newValue() for more details.
	private double limit;
	
	// The count of the trial from the last reset. Each word matching/trying
	// increase this.
	private int count;
	
	// This is the value of the performance (from the last reset) Higher value
	// means a solution is near, lover value means we should reset the generator.
	// See newValue() for more details.
	private double performanceValue;
	
	// This is the global maximum limit. If the count of the trying reach this
	//	value the generator should be restarted with new seed.
	private int maxCount = 100000;
	
	// The performance measure is too complicated to calculate in each step.
	// This is a period to check the performance.
	private int minCount = 100;
	
	// This is the corresponding counter for the minCount period. 
	private int microCounter = 0;
	
	/**
	 * Constructor 
	 * @param limit
	 */
	public PerformanceMeter(int limit) {
		this.limit = 0.02;
		reset();
	}
	
	/**
	 * This function should be called in each trying. It counts the performance
	 * in each "minCount"-th step. If the performance is low this will throw a
	 * LowPerformanceException to restart the generator with new seed.
	 * @param val
	 * @throws LowPerformanceException
	 */
	void newValue(int val) throws LowPerformanceException{
		count++;			// Increase the global counter
		microCounter++;		// Increase the minCount counter
		
		// The performance is calculated by a SoS aka. Sum of Squares. (aka.
		// Pitagoras theorem). This is better than linear sum because states
		// near the solution has higher priority.
		performanceValue += val*val;
		
		// Microcounter period.
		if(microCounter > minCount){
			microCounter = 0;	// reset
			
			// The two condition of the low performance.
			if(limit>Math.sqrt(performanceValue)/(double)count){
				throw new LowPerformanceException("Performance val: " + performanceValue + " limit: " + limit + " count: " + count);
			}
			if(maxCount < count){
				throw new LowPerformanceException("Run count exceeds the max run: " + count);
			}
		}
	}
 
	/**
	 * Global reset
	 */
	public void reset() {
		count = 0;
		performanceValue = limit* 2;	// Do not reset to 0 to enable run a bit...
	}

}

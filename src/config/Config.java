package config;

import main.Canvas;

public class Config {
	Integer numOfBatch; // Generator will run numOfBatch times and different seed.
	
//	Integer xDim;
//	Integer yDim;
	
	public Canvas cnv;
	
	public Config(){
		numOfBatch = 100;
	}
}

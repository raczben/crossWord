package main;

/**
 * Simple class to store two elements.
 * This will be a struct in C.
 * This will be a tuple in python.
 * @author Benedek Racz
 *
 */
class Pair {
	String str;
	int spaceLen;
	
	Pair(String str, int spaceLen){
		this.str = str;
		this.spaceLen = spaceLen;
	}
}

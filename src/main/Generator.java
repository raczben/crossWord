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

import gui.GuiMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Generator implements Runnable{

	private String solution; 	// This should be find out by the user.

	// Contains all available words. This is a map to helps for matching. The
	// keys of the map is the starting letter(s) of the map.
	static Map<String, List<String>> dictionaryWordsMap;

	// The canvas. This stores the letters/characters of the game.
	Canvas _canvas_;

	// Increment this field for more debug information.
	private static int debug = 1;

	// Debug information file.
	private static PrintStream logFile;

	// The dimension of the canvas.
//	private int dimx, dimy;

	// The performance meter throw an exception when the performance of the
	// generation is low in order to restart the generation with new seed. 
	private PerformanceMeter perfMet;

	// The generator of the pseudo-random seed. 
	private Random rndGen;

	private int numOfBatch;

	final static int prefixIndexHelperArray[][] = {{}, {0}, {1}, {0, 1}, {2}, {0, 2}, {1, 2}, {0, 1, 2}}; 

	List<Thread> threads;

	GuiMain mainGui;

	private int seedcounter;

	List<Canvas> goodCanvasList;

	/**
	 *  Constructor 
	 * @param dimx
	 * @param dimy
	 */
	public Generator(int dimx, int dimy){
		setDimension(dimx, dimy);			// Set the dimension

		initCommon();
			
	}

	public Generator(Canvas cnv) {
		_canvas_ = cnv;
		setDimension(cnv.dimx, cnv.dimy);
		readWords();						// Read all words from the dictionary.
		perfMet = new PerformanceMeter(3);	// Create performance meter.

		// Open the debug logfile.
		try {
			logFile = new PrintStream(new File("log.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		goodCanvasList = Collections.synchronizedList(new ArrayList<Canvas>());
	}

	/**
	 * This constructor does not initialize anything just 
	 * @param debug
	 * @throws Exception
	 */
	Generator(boolean debug) throws Exception{
		if(! debug){
			throw new Exception("This constructor is for test/debug reason");
		}
		rndGen = new Random(0);
		dictionaryWordsMap = new HashMap<String, List<String>>();
	}

	/**
	 * Default constructor.
	 */
	Generator(){
		this(0, 0);
	}

	private void initCommon(){
		readWords();						// Read all words from the dictionary.
		generateCanvas();					// Generate canvas
		perfMet = new PerformanceMeter(3);	// Create performance meter.

		// Open the debug logfile.
		try {
			logFile = new PrintStream(new File("log.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Generator(String sol, int debugLevel) {
		this.debug = debugLevel; 
		setSolution(sol);
		initCommon();
	}

	public void setDimension(int dimx, int dimy){
//		this.dimx = dimx;					// Set the dimension
//		this.dimy = dimy;
	}

	/**
	 * Two generator equals if its canvas are equals.
	 */
	@Override
	public boolean equals(Object obj) {
		Generator gen = (Generator) obj;
		return gen._canvas_.equals(this._canvas_);
	}

	/**
	 * 
	 */
	private void generateCanvas() {
		_canvas_ = new Canvas(solution.length()+3, solution.length()+3);
	}

	/**
	 * Read all words and put them into the map.
	 */
	void readWords(){
		dictionaryWordsMap = new HashMap<String, List<String>>();
		String fileName = "hungarian" + ".txt";
		System.out.println(fileName);
		String path = "resources/" + fileName;
		InputStream is = Main.class.getResourceAsStream(path);
		assert is != null: "Cannot load resource file";
		if (is == null){
			System.out.println("is is null");
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));

			String line;
			while ((line = reader.readLine()) != null) {
				addDictionaryWord(line);
			}            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { is.close(); } catch (Throwable ignore) {}
		}
		//		dictionaryWords.remove(0);

	}

	/**
	 * @param word
	 */
	void addDictionaryWord(String word) {
		//				if(line.length()>2){
		word = word.toLowerCase();
		word = replaceSpecials(word);
		int prefixLen = 3;

		String[] prefixes = generatePrefixes(word, prefixLen);
		for(String prefix : prefixes){
			try{
				dictionaryWordsMap.get(prefix).add(word);
			}
			catch(Exception ex){
				dictionaryWordsMap.put(prefix,  new ArrayList<String>());
				dictionaryWordsMap.get(prefix).add(word);
			}
		}
	}

	String[] generatePrefixes(String word, int prefixLen) {
		String fullPrefix;
		try{
			fullPrefix = word.substring(0, prefixLen);
		} catch(IndexOutOfBoundsException ex){	// The word shorter than the prefixLen
			fullPrefix = word;
		}

		prefixLen = fullPrefix.length();
		int retLen = (int) (Math.pow(2, prefixLen))-1;
		String[] ret = new String[retLen];
		if(0 == retLen){
			return ret;
		}
		ret[0] = fullPrefix;

		for(int i = 0; i<retLen; i++){
			int[] idxGroup =  prefixIndexHelperArray[i];
			StringBuilder prefBuilder = new StringBuilder(fullPrefix);
			for(int idx: idxGroup){
				prefBuilder.setCharAt(idx, '.');
			}
			ret[i] = prefBuilder.toString();
		}
		return ret;
	}

	/**
	 * Replaces all special characer in a word to simpler one.
	 * Ex.: in Hungarian: long vowels to shorts.
	 * @param line
	 * @return
	 */
	static String replaceSpecials(String word) {
		word = word.replaceAll("\u00F3" , "o");	// long o
		word = word.replaceAll("\u0151" , "\u00F6");	// long o^
		word = word.replaceAll("\u0171" , "\u00FC");	// long u^
		word = word.replaceAll("\u00ED" , "i");	// long i^
		word = word.replaceAll("\u00FA" , "u");	// long u

		return word;
	}

	/**
	 * Set the solution to be find out by the user. This word will be the first
	 * vertical word on the canvas.
	 * @param sol
	 */
	public void setSolution(String sol) {
		solution = sol;
		setDimension(sol.length()+3, sol.length()+5);
	}

	/**
	 * This will be called recursively during the generation. 
	 * 
	 * @param x		Find word for this position
	 * @param y 	Find word for this position
	 * @param canvas This is the current state of the canvas.
	 * @param direction	Find word in this direction (from the x-y position)
	 * @return	null if cannot match or the canvas if it match.
	 * @throws LowPerformanceException	When the performance reach one of the criteria.
	 * @throws InterruptedException 
	 */
	private Canvas fitWordAt(Canvas canvas, Direction direction, int depth) throws LowPerformanceException, InterruptedException{

		if(Thread.interrupted()){
			throw new InterruptedException("IRQ in fitWord");
		}
		List<Coordinate> coordinates = canvas.getEmptyCoordinate2(direction);
		Coordinate dim;
		try {
			dim = coordinates.get(rndGen.nextInt(coordinates.size()));

			// The random generator throws this, if the coordinates.size() is 0 this
			// means that there is no place where any word can be fitted. This is the real end of the generation.
		} catch (IllegalArgumentException e) {
			System.out.println("REAL END:" + canvas);
			return canvas;
		}

		int x = dim.x;
		int y = dim.y;
		if(null != solution){
			if(depth > solution.length()*3){
				return canvas;
			}
		}
		if(debug>1){
			System.err.println("fitWordAt(): x =" + x + "  y: " +y);
			if(debug > 2){
				System.err.println("fitWordAt(): Canvas: >>\n" + toString() + "<<");
			}
		}
		String pattern = canvas.getPatternAt(x, y, direction);

		String[] words = getWordsMatchPattern(pattern);

		for(String word : words){
			if(debug > 0){
				if(1 == depth){
					System.err.println("fitWordAt(): START FROM THE BEGINNING!!! (with: " + word + " from: x: " + x + " y: " + y + ")");
				}
				if(debug > 3){
					System.err.println("fitWordAt(): setWord: " + word);
				}
			}

			perfMet.newValue(depth);
			if(x>3){
				logCanvas(canvas, x, y);
			}

			Canvas retcanv;
			if(direction.equals(Direction.VERTICAL)){
				retcanv = fitWordAt(canvas.addWord(word, x, y, Direction.VERTICAL), Direction.HORIZONTAL, depth+1);
			} else { // VERICAL
				retcanv = fitWordAt(canvas.addWord(word, x, y, Direction.HORIZONTAL), Direction.VERTICAL, depth+1);
			}

			if(retcanv != null){
				return retcanv;
			}
		}
		return null;

	}

	/**
	 * returns all  words from the dictionary, that matches the pattern. Pattern
	 * should be a "limited regexp": Missing letters denoted by "." .  The
	 * pattern belongs to the begin of the word. If the first 3 letters are
	 * missing (aka. the pattern starts with "...") an empty array will be
	 * return. Shorter words will matches longer patterns too.
	 * 
	 * Examples:
	 * 	1	pattern: "baby" mathces only "baby"
	 *  2	pattern: "b.b." mathces ex: "baby", "bab", "boba"...
	 *  3	pattern: "...y.." mathes nothing (first 3 letters are missing)
	 * 
	 * @param pattern
	 * @return
	 */
	String[] getWordsMatchPattern(String pattern) {
		String prefix;
		try{
			prefix = pattern.substring(0, 3);
		} catch(IndexOutOfBoundsException ex){	// The word shorter than the prefixLen
			prefix = pattern;
		}

		List<String> beginMatchWords;
		beginMatchWords = dictionaryWordsMap.get(prefix);
		if(null == beginMatchWords){
			return new String[0];
		}

		List<String> ret = new ArrayList<String>();
		for(String word :beginMatchWords){
			if(word.length() <= pattern.length()){ // if the word not longer than the space...
				char ch = Canvas.fillableCellCharacter;
				try{
					ch = pattern.charAt(word.length());
				} catch (Exception ex){}
				if(ch<64){	// if not letter
					String subPattern = pattern.substring(0, word.length());
					if(word.matches(subPattern)){
						ret.add(rndGen.nextInt(ret.size()+1), word);
					}
				}
			}
		}
		return ret.toArray(new String[0]);
	}

	/**
	 * Log for debug reason.
	 * @param canvas
	 * @param x
	 * @param y
	 */
	private void logCanvas(Canvas canvas, int x, int y) {
		logFile.println(x + y+ " >>");
		logFile.println(canvas.toString());
		logFile.println("<<");
		logFile.println("");
	}

	/**
	 * The string representation of the Main is the string representation of the canvas.
	 */
	public String toString() {
		return _canvas_.toString();
	}

	/**
	 * Returns an array of words that contains the "wordpart" at the beginning
	 * of the word.
	 * @param wordpart
	 * @param maxLen
	 * @return
	 */
	String[] getWordsContains(String wordpart, int maxLen){
		List<String> ret = new ArrayList<String>();
		int partLen = wordpart.length();
		List<String> dictionaryWords1;
		try{
			if(1 == partLen){
				dictionaryWords1 = dictionaryWordsMap.get(wordpart);
			}else{
				dictionaryWords1 = dictionaryWordsMap.get(wordpart.substring(0, 2));
			}
		}catch(Exception ex){
			return new String[0];
		}
		if(null == dictionaryWords1){
			return new String[0];
		}

		for(String word :dictionaryWords1){
			if(word.startsWith(wordpart) ){  //|| word.startsWith(wordpart, 1)
				if(word.length()<=maxLen){
					//					int insert = rndGen.nextInt(words.length);
					ret.add(rndGen.nextInt(ret.size()+1), word);
					//					ret.add(word);
				}
			}
		}
		//		Collections.shuffle(ret);
		return ret.toArray(new String[0]);
	}

	/**
	 * The GENERATE function. This starts/restarts the generation with different seeds.
	 */
	public void generate(int numOfBatch){
		generateCanvas();
		_canvas_.setWordV(solution, 1, 1);
		for(int i = 0; i<numOfBatch; i++){
			try {
				rndGen = new Random(i);
				perfMet.reset();
				Canvas cnv = null;
				try {
					cnv = fitWordAt(_canvas_, Direction.HORIZONTAL, 1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(cnv);
			} catch (LowPerformanceException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}
		}
		System.out.println(_canvas_);
	}


	/**
	 * The GENERATE function. This starts/restarts the generation with different seeds.
	 * @param mainGui 
	 */
	public void generateGui(GuiMain mainGui, int numOfBatch){
		seedcounter = 0;
		this.numOfBatch = numOfBatch;
		this.mainGui = mainGui;
		new Thread(this).start();
	}

	public String[] getAllDictionaryWords() {
		Set<String> ret = new TreeSet<String>(); 
		for(List<String> words : dictionaryWordsMap.values()){
			ret.addAll( words);
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public void run() {
		seedcounter = 0;
//		this.numOfBatch = numOfBatch;
//		this.mainGui = mainGui;
		threads = new ArrayList<Thread>();
		threads.add(new GeneratorWorker(this));
		threads.add(new GeneratorWorker(this));
		threads.get(0).start();
		threads.get(1).start();

		for (Thread thread : threads){
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mainGui.update(numOfBatch, numOfBatch, goodCanvasList.size());
		mainGui.setGoodCanvasList(goodCanvasList);
//		new Thread(this);
//		threadMe.start();
	}

	public void stop() {
		for (Thread thread : threads){
			thread.interrupt();
		}
	}

	public synchronized int getNextSeed() {
		if(seedcounter == numOfBatch){
			return -1;
		}
		mainGui.update(numOfBatch, seedcounter, goodCanvasList.size());
		return seedcounter++;
	}


}

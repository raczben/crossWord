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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Generator {

	private String solution; 	// This should be find out by the user.

	// Contains all available words. This is a map to helps for matching. The
	// keys of the map is the starting letter(s) of the map.
	private Map<String, List<String>> dictionaryWordsMap;

	// The canvas. This stores the letters/characters of the game.
	private Canvas _canvas_;

	// Increment this field for more debug information.
	private int debug = 1;

	// Debug information file.
	private PrintStream logFile;

	// The dimension of the canvas.
	private int dimx, dimy;

	// The performance meter throw an exception when the performance of the
	// generation is low in order to restart the generation with new seed. 
	private PerformanceMeter perfMet;

	// The generator of the pseudo-random seed. 
	private Random generator;

	
	/**
	 *  Constructor 
	 * @param dimx
	 * @param dimy
	 */
	public Generator(int dimx, int dimy){
		setDimension(dimx, dimy);			// Set the dimension
		
		initCommon();
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

	public Generator(String sol) {
		setSolution(sol);
		initCommon();
	}
	
	public void setDimension(int dimx, int dimy){
		this.dimx = dimx;					// Set the dimension
		this.dimy = dimy;
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
		_canvas_ = new Canvas(dimx, dimy);
	}

	/**
	 * Read all words and put them into the map.
	 */
	private void readWords(){
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
				//				if(line.length()>2){
				line = line.toLowerCase();
				line = replaceSpecials(line);
				try{
					dictionaryWordsMap.get(line.substring(0, 1)).add(line);
				}
				catch(Exception ex){
					dictionaryWordsMap.put(line.substring(0, 1),  new ArrayList<String>());
					dictionaryWordsMap.get(line.substring(0, 1)).add(line);
				}
				try{
					dictionaryWordsMap.get(line.substring(0, 2)).add(line);
				}
				catch(Exception ex){
					try{
						dictionaryWordsMap.put(line.substring(0, 2),  new ArrayList<String>());
						dictionaryWordsMap.get(line.substring(0, 2)).add(line);
					}
					catch(Exception ex1){
					}
				}
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
	 * Replaces all special characer in a word to simpler one.
	 * Ex.: in Hungarian: long vowels to shorts.
	 * @param line
	 * @return
	 */
	private String replaceSpecials(String word) {
		word.replaceAll("\u0243" , "o");	// long o
		word.replaceAll("\u0337" , "\u0246");	// long o^
		word.replaceAll("\u0369" , "\u0252");	// long u^
		word.replaceAll("\u0237" , "i");	// long i^
		return word;
	}

	/**
	 * Set the solution to be find out by the user. This word will be the first
	 * vertical word on the canvas.
	 * @param sol
	 */
	public void setSolution(String sol) {
		solution = sol;
		setDimension(sol.length()+5, sol.length()+8);
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
	 */
	private Canvas fitWordAt(int x, int y, Canvas canvas, Direction direction) throws LowPerformanceException{
		if(x==dimx || y == solution.length()){
			return canvas;
		}
		if(debug>1){
			System.err.println("fitWordAt(): x =" + x + "  y: " +y);
			if(debug > 2){
				System.err.println("fitWordAt(): Canvas: >>\n" + toString() + "<<");
			}
		}
		Pair wordpartPair = canvas.getWordAt(x, y, direction);
		String wordpart = wordpartPair.str;
		String[] words = getWordsContains(wordpart, wordpartPair.spaceLen);
		int l = wordpart.length();
		for(String word : words){
			if(debug > 0){
				if(x== 0 && y == 0){
					System.err.println("fitWordAt(): START FROM THE BEGINNING!!! (" + word + ")");
				}
				if(debug > 3){
					System.err.println("fitWordAt(): setWord: " + word);
				}
			}

			perfMet.newValue(Integer.max(x, y));
			if(x>3){
				logCanvas(canvas, x, y);//printCanvas();
			}

			Canvas retcanv;
			if(direction.equals(Direction.VERTICAL)){
				retcanv = fitWordAt(y, x, canvas.addWord(word.substring(l), x, y+l, Direction.VERTICAL), Direction.HORIZONTAL);
			} else { // VERICAL
				retcanv = fitWordAt(y+1, x, canvas.addWord(word.substring(l), x+l, y, Direction.HORIZONTAL), Direction.VERTICAL);
			}

			if(retcanv != null){
				return retcanv;
			}
		}
		return null;

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
					//					int insert = generator.nextInt(words.length);
					ret.add(generator.nextInt(ret.size()+1), word);
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
	void generate(){
		generateCanvas();
		_canvas_.setWordV(solution, 0, 0);
		for(int i = 0; i<200; i++){
			try {
				generator = new Random(i);
				perfMet.reset();
				System.out.println(fitWordAt(0, 0, _canvas_, Direction.HORIZONTAL));
			} catch (LowPerformanceException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}
		}
		System.out.println(_canvas_);
	}


}

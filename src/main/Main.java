package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
	String solution; 	// This should be find out by the user.
	List<String> dictionaryWords;	// All available words
	int dimx, dimy;
	Character canvas[];
	private int debug = 2;
	PrintStream logFile;
	
	

	Character getCharAt(int x, int y){
		int pos = x + y*dimx;
		if(x>=dimx || y>=dimy){
			return null;
		}
		return canvas[pos];
	}

	void setCharAt(Character ch, int x, int y){
		if(x>=dimx || y>=dimy){
			return;
		}

		int pos = x + y*dimx;
		canvas[pos] = ch;
	}

	void setWordH(String word, int x, int y){
		for(int i = 0; i<word.length(); i++){
			setCharAt(word.charAt(i), x+i, y);
		}
	}

	void setWordV(String word, int x, int y){
		for(int i = 0; i<word.length(); i++){
			setCharAt(word.charAt(i), x, y+i);
		}
	}

	String getWordAtH(int x, int y){
		String ret = new String();
		for(int i= x; i<dimx; i++){
			Character ch = getCharAt(i, y);
			if(ch == null || ch.equals(' ')){
				return ret;
			}
			ret += ch;
		}
		return ret;
	}

	String getWordAtV(int x, int y){
		String ret = new String();
		for(int i= y; i<dimy; i++){
			Character ch = getCharAt(x, i);
			if(ch == null || ch.equals(" ")){
				return ret;
			}
			ret += ch;
		}
		return ret;
	}

	Main(int dimx, int dimy){
		this.dimx = dimx;
		this.dimy = dimy;
		readWords();
		generateCanvas();
		try {
			logFile = new PrintStream(new File("log.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Main(){
		this(0, 0);
	}

	@Override
	public boolean equals(Object obj) {
		Main main = (Main) obj;
		return Arrays.equals(canvas, main.canvas);
	}

	private void generateCanvas() {
		canvas = new Character[dimx*dimy];

	}

	void readWords(){
		dictionaryWords = new ArrayList<String>();
		String fileName = "hungarian" + ".txt";
		System.out.println(fileName);
		String path = "resources/" + fileName;
		InputStream is = Main.class.getResourceAsStream(path);
		assert is != null: "Cannot load resource file";
		if (is == null){
			System.out.println("is is null");
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = reader.readLine()) != null) {
				if(line.length()>2){
					dictionaryWords.add(line);
				}
			}            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { is.close(); } catch (Throwable ignore) {}
		}
		dictionaryWords.remove(0);

	}

	private void setSolution(String sol) {
		solution = sol;
		dimy = sol.length();
		dimx = dimy;
	}

	void removeWordAtH(int x, int y){
		for(int i = x; i< dimx; i ++){
			Character ch = getCharAt(i, y);
			if(ch == null || ch.equals(" ")){
				return;
			}
			setCharAt(null, i, y);
		}
	}
	void removeWordAtV(int x, int y){
		for(int i = y; i< dimy; i ++){
			Character ch = getCharAt(x, i);
			if(ch == null || ch.equals(" ")){
				return;
			}
			setCharAt(null, x, i);
		}
	}

	boolean fitWordAtH(int x, int y){
		if(x==dimx || y == dimy){
			return true;
		}
		if(debug>1){
			System.err.println("fitWordAtH(): x =" + x + "  y: " +y);
			if(debug > 2){
				System.err.println("fitWordAtH(): Canvas: >>\n" + toString() + "<<");
			}
		}
		String wordpart = getWordAtH(x, y);
		String[] words = getWordsContains(wordpart);
		int l = wordpart.length();
		for(String word : words){
			if(debug > 0){
				if(x== 0 && y == 0){
					System.err.println("fitWordAtH(): START FROM THE BEGINNING!!! (" + word + ")");
				}
				if(debug > 3){
					System.err.println("fitWordAtH(): setWordH: " + word);
				}
			}
			setWordH(word.substring(l), x+l, y);
			if(fitWordAtV(y+1, x)){
				return true;
			}
			removeWordAtH(x+l, y);
		}
		return false;
	}


	boolean fitWordAtV(int x, int y){
		if(x==dimx || y == dimy){
			return true;
		}
		if(debug>1){
			System.err.println("fitWordAtV(): x =" + x + "  y: " +y);
			if(debug > 2){
				System.err.println("fitWordAtV(): Canvas: >>\n" + toString() + "<<");
			}
		}
		String wordpart = getWordAtV(x, y);
		String[] words = getWordsContains(wordpart);
		int l = wordpart.length();
		for(String word : words){
			if(debug > 3){
				System.err.println("fitWordAtV(): setWordV: " + word);
			}
			setWordV(word.substring(l), x, y+l);
			if(x>3){
				logCanvas();//printCanvas();
			}
			if(fitWordAtH(y, x)){
				return true;
			}
			removeWordAtV(x, y+l);
		}
		return false;

	}


	private void logCanvas() {
		logFile.println(">>");
		logFile.println(toString());
		logFile.println("<<");
		logFile.println("");
	}

	void printCanvas(){
		System.out.println(toString());
	}

	public String toString() {
		String ret = "";
		for(int i = 0; i < dimy; i ++){
			ret += getWordAtH(0, i) + "\n"; 
		}
		return ret;
	}

	String[] getWordsContains(String wordpart){
		List<String> ret = new ArrayList<String>();
		for(String word :dictionaryWords){
			if(word.startsWith(wordpart) ){  //|| word.startsWith(wordpart, 1)
				ret.add(word);
			}
		}
		Collections.shuffle(ret);
		return ret.toArray(new String[0]);
	}

	void generate(){
		generateCanvas();
		setWordV(solution, 0, 0);
		fitWordAtH(0, 0);
		System.out.println(canvas);
	}

	public static void  main(String[] args) {
		System.out.println("Hello");
		Main main = new Main();

		main.setSolution("szeret");
		main.generate();

	}


}

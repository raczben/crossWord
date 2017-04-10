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
import java.util.Map.Entry;

public class Main {
	String solution; 	// This should be find out by the user.
	List<String> dictionaryWords;	// All available words
	//	Character canvas[];
	Canvas _canvas_;
	private int debug = 1;
	PrintStream logFile;
	int dimx, dimy;
	PerformanceMeter perfMet;
	


	Main(int dimx, int dimy){
		_canvas_ = new Canvas(dimx, dimy);
		this.dimx = dimx;
		this.dimy = dimy;
		readWords();
		generateCanvas();
		perfMet = new PerformanceMeter(3);
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

	//	@Override
	//	public boolean equals(Object obj) {
	//		Main main = (Main) obj;
	//		return Arrays.equals(canvas, main.canvas);
	//	}

	private void generateCanvas() {
		_canvas_ = new Canvas(dimx, dimy);
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
		dimx = dimy+2;
	}

	Canvas fitWordAtH(int x, int y, Canvas canvas) throws LowPerformanceException{
		if(x==dimx || y == dimy){
			return canvas;
		}
		if(debug>1){
			System.err.println("fitWordAtH(): x =" + x + "  y: " +y);
			if(debug > 2){
				System.err.println("fitWordAtH(): Canvas: >>\n" + toString() + "<<");
			}
		}
		Pair wordpartPair = canvas.getWordAt(x, y, Direction.HORIZONTAL);
		String wordpart = wordpartPair.str;
		String[] words = getWordsContains(wordpart, wordpartPair.spaceLen);
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
			Canvas retcanv = fitWordAtV(y+1, x, canvas.addWord(word.substring(l), x+l, y, Direction.HORIZONTAL));
			if(retcanv != null){
				return retcanv;
			}
			//			removeWordAtH(x+l, y);
		}
		return null;
	}


	Canvas fitWordAtV(int x, int y, Canvas canvas) throws LowPerformanceException{
		if(x==dimx || y == dimy){
			return canvas;
		}
		if(debug>1){
			System.err.println("fitWordAtV(): x =" + x + "  y: " +y);
			if(debug > 2){
				System.err.println("fitWordAtV(): Canvas: >>\n" + toString() + "<<");
			}
		}
		Pair wordpartPair = canvas.getWordAt(x, y, Direction.VERTICAL);
		String wordpart = wordpartPair.str;
		String[] words = getWordsContains(wordpart, wordpartPair.spaceLen);
		int l = wordpart.length();
		for(String word : words){
			if(debug > 3){
				System.err.println("fitWordAtV(): setWordV: " + word);
			}

			perfMet.newValue(Integer.max(x, y));
			if(x>4){
				logCanvas(canvas, x, y);//printCanvas();
			}
			Canvas retcanv = fitWordAtH(y, x, canvas.addWord(word.substring(l), x, y+l, Direction.VERTICAL));
			if(retcanv != null){
				return retcanv;
			}
			//			removeWordAtV(x, y+l);
		}
		return null;

	}


	private void logCanvas(Canvas canvas, int x, int y) {

		logFile.println(x + y+ " >>");
		logFile.println(canvas.toString());
		logFile.println("<<");
		logFile.println("");
	}

	private void logCanvas() {
		logCanvas(_canvas_, -1, -1);
	}

	void printCanvas(){
		System.out.println(toString());
	}

	public String toString() {
		return _canvas_.toString();
	}

	String[] getWordsContains(String wordpart, int maxLen){
		List<String> ret = new ArrayList<String>();
		for(String word :dictionaryWords){
			if(word.startsWith(wordpart) ){  //|| word.startsWith(wordpart, 1)
				if(wordpart.length()<=maxLen){
					ret.add(word);
				}
			}
		}
		Collections.shuffle(ret);
		return ret.toArray(new String[0]);
	}

	void generate(){
		generateCanvas();
		_canvas_.setWordV(solution, 0, 0);
		for(int i = 0; i<100; i++){
			try {
				perfMet.reset();
				System.out.println(fitWordAtH(0, 0, _canvas_));
			} catch (LowPerformanceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(_canvas_);
	}

	public static void  main(String[] args) {
		System.out.println("Hello");
		Main main = new Main();

		main.setSolution("szer");
		main.generate();

	}


}

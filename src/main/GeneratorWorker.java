package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneratorWorker extends Thread {

	// Contains all available words. This is a map to helps for matching. The
	// keys of the map is the starting letter(s) of the map.
//	private static Map<String, List<String>> dictionaryWordsMap;

	// The canvas. This stores the letters/characters of the game.
	private Canvas _canvas_;

	// The generator of the pseudo-random seed. 
	private Random rndGen;
	

	// Increment this field for more debug information.
	private static int debug = 1;
	
	Generator generator;


	// The performance meter throw an exception when the performance of the
	// generation is low in order to restart the generation with new seed. 
	private PerformanceMeter perfMet;

//	GeneratorWorker(int seed, Canvas canvas){
//		rndGen = new Random(seed);
//	}
	
	public GeneratorWorker(Generator generator) {
		this.generator = generator;
		perfMet = new PerformanceMeter(3);
		this._canvas_ = generator._canvas_;
		
//		rndGen = new Random(seed);
	}

	@Override
	public void run() {
		
		int seed;

		ArrayList<Canvas> goodCanvasList = new ArrayList<Canvas>();
		try{
			while((seed = generator.getNextSeed()) >=0){
				try {
					if(Thread.interrupted()){
						throw new InterruptedException("IRQ in generate");
					}
					rndGen = new Random(seed);
					perfMet.reset();
					
					Canvas cnv = fitWordAt(_canvas_, Direction.HORIZONTAL, 1);
					if(null != cnv){
						generator.goodCanvasList.add(cnv);
					}
					System.out.println(cnv);
				} catch (LowPerformanceException e) {
					// TODO Auto-generated catch block
					System.err.println(e);
				}
			}
//			System.out.println(_canvas_);
		} catch (InterruptedException ex){ }
//		} finally{
//			mainGui.update(numOfBatch, numOfBatch, goodCanvasList.size());
//			mainGui.setGoodCanvasList(goodCanvasList);
//		}
		
		
		
		
	
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
//		if(null != solution){
//			if(depth > solution.length()*3){
//				return canvas;
//			}
//		}
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
//			if(x>3){
//				logCanvas(canvas, x, y);
//			}

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
		beginMatchWords = Generator.dictionaryWordsMap.get(prefix);
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

}

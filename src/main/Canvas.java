package main;

import java.util.Arrays;

import main.Pair;

public class Canvas {
	Character[] canvas;
	int dimx, dimy;
	

	// Where this character occurs the canvas can be filled with characters.
	static final Character fillableCellCharacter = ' ';		  

	public Canvas(int dimx, int dimy) {
		this.dimx = dimx;
		this.dimy = dimy;
		generateCanvas();
	}

	public String toString() {
		String ret = "";
		for(int i = 0; i < dimy; i ++){
			ret += getWordAt(0, i, Direction.HORIZONTAL).str + "\n"; 
		}
		return ret;
	}

	public Canvas(Canvas other) {
		this.dimx = other.dimx;
		this.dimy = other.dimy;
		this.canvas = other.canvas.clone();
	}


	private void generateCanvas() {
		canvas = new Character[dimx*dimy];
		Arrays.fill(canvas, fillableCellCharacter);
	}

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
		setCharAt(null, x+word.length(), y);
	}

	void setWordV(String word, int x, int y){
		for(int i = 0; i<word.length(); i++){
			setCharAt(word.charAt(i), x, y+i);
		}
		setCharAt(null, x, y+word.length());
	}

	@Override
	public boolean equals(Object obj) {
		Canvas other = (Canvas) obj;
		return Arrays.equals(canvas, other.canvas);
	}

	Pair getWordAt(int x, int y, Direction direction){
		String ret = new String();
		int spaceLen = 0;
		Character ch;
		int start = direction.equals(Direction.HORIZONTAL) ? x:y;
		for(int i= start ; i<dimx; i++){
			if(direction.equals(Direction.HORIZONTAL)){
				ch = getCharAt(i, y);
			}else{
				ch = getCharAt(x, i);
			}
			if(ch == null){
				return new Pair(ret, spaceLen);
			}
			if(ch != fillableCellCharacter){
				ret += ch;
			}
			spaceLen++;
		}
		return new Pair(ret, spaceLen);
	}


	public Canvas addWord(String word, int x, int y, Direction direction) {
		Canvas other = new Canvas(this);
		if(direction.equals(Direction.HORIZONTAL)){
			other.setWordH(word, x, y);
		}
		else{
			other.setWordV(word, x, y);
		}
		return other;
	}
	

}

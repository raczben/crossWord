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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.Pair;

public class Canvas implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6689475818605371120L;
	Character[] canvas;
	int dimx, dimy;

	List<String> listOfWords;
	private List<Coordinate> horizStarCoordinates;
	private List<Coordinate> vertStarCoordinates;


	// Where this character occurs the canvas can be filled with characters.
	// This must be "." due to regexp pattern matching
	public static final Character fillableCellCharacter = '.';
	// This cell should remain empty (Here will place the description of the words)
	public static final Character emptyStoneCellCharacter = '2';
	//	public static final int FILLABLE_AS_EMPTY = 1;		  

	public Canvas(int dimx, int dimy) {
		this.dimx = dimx;
		this.dimy = dimy;
		generateCanvas();
		listOfWords = new ArrayList<String>();
		horizStarCoordinates  = new ArrayList<Coordinate>();
		vertStarCoordinates  = new ArrayList<Coordinate>();
	}

	public Canvas(Canvas other) {
		this.dimx = other.dimx;
		this.dimy = other.dimy;
		this.canvas = other.canvas.clone();
		listOfWords = new ArrayList<String>(other.listOfWords);
		horizStarCoordinates = new ArrayList<Coordinate>(other.horizStarCoordinates);
		vertStarCoordinates = new ArrayList<Coordinate>(other.vertStarCoordinates);
	}


	public String toString() {
		String ret = listOfWords.toString()+ "\n\n";
		for(int y = 0; y < dimy; y ++){
			for(int x = 0; x < dimx; x ++){
				//			ret += getWordAt(0, i, Direction.HORIZONTAL).str + "\n";
				Character ch = getCharAt(x, y);
				if(ch.equals(emptyStoneCellCharacter)){
					ret += " ";
					//					continue;
				}else{
					ret += ch;}
			}
			ret += "\n";
		}
		return ret;
	}

	private void generateCanvas() {
		canvas = new Character[dimx*dimy];
		Arrays.fill(canvas, fillableCellCharacter);
		for(int x = 0; x<dimx; x++){
			setCharAt(emptyStoneCellCharacter, x, 0);
		}
		for(int y = 0; y<dimy; y++){
			setCharAt(emptyStoneCellCharacter, 0, y);
		}
		setCharAt(fillableCellCharacter, 0, 0);
	}

	public Character getCharAt(int x, int y, int flag) {
		int pos = x + y*dimx;
		if(x>=dimx || y>=dimy){
			return emptyStoneCellCharacter;
		}
		if(x<0 || y<0){
			return emptyStoneCellCharacter;
		}
		Character ch = canvas[pos];
		//		if((flag & FILLABLE_AS_EMPTY) >0){
		//			if (ch.equals(fillableCellCharacter)){
		//				ch = Character.MIN_VALUE;
		//			}
		//		}
		return ch;
	}
	public Character getCharAt(int x, int y){
		return getCharAt(x, y, 0);
	}

	public void setCharAt(Character ch, int x, int y){
		setCharAt(ch, x, y, false);
	}


	public void setCharAt(Character ch, int x, int y, boolean overWrite){
		if(x>=dimx || y>=dimy){
			return;
		}

		int pos = x + y*dimx;
		if(! overWrite){ // Do not overwrite letters
			if(! canvas[pos].equals(fillableCellCharacter)){
				return;
			}
		}
		canvas[pos] = ch;
	}


	void setWordH(String word, int x, int y){
		setWordH(word, x, y, true);
	}

	void setWordV(String word, int x, int y){
		setWordV(word, x, y, true);
	}

	void setWordV(String word, int x, int y, boolean overWrite){
		for(int i = 0; i<word.length(); i++){
			setCharAt(word.charAt(i), x, y+i);
		}
		listOfWords.add(word);
		vertStarCoordinates.add(new Coordinate(x, y));
		setCharAt(emptyStoneCellCharacter, x, y+word.length(), overWrite);
	}


	private void setWordH(String word, int x, int y, boolean overWrite) {
		for(int i = 0; i<word.length(); i++){
			setCharAt(word.charAt(i), x+i, y);
		}
		listOfWords.add(word);
		horizStarCoordinates.add(new Coordinate(x, y));
		setCharAt(emptyStoneCellCharacter, x+word.length(), y, overWrite);

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
			if(ch.equals(emptyStoneCellCharacter)){
				return new Pair(ret, spaceLen);
			}
			if(ch != fillableCellCharacter){
				ret += ch;
			}
			spaceLen++;
		}
		return new Pair(ret, spaceLen);
	}


	String[] getWordAt(int x, int y, Direction direction, int prefLen){
		String fullWord = new String();
		String pref = new String();
		String [] ret = new String[2];
		int prefixCounter = 0;
		ret[0] = pref;
		ret[1] = fullWord;
		String prefPart = "";


		Character ch;
		int start = direction.equals(Direction.HORIZONTAL) ? x:y;
		for(int i= start ; i<dimx; i++){
			if(direction.equals(Direction.HORIZONTAL)){
				ch = getCharAt(i, y);
			} else {	// VERTICAL
				ch = getCharAt(x, i);
			}
			if(ch.equals(emptyStoneCellCharacter)){
				return ret;
			}
			fullWord += ch;
			if(prefixCounter < prefLen){
				prefPart += ch;
			}
			if(ch != fillableCellCharacter){
				prefixCounter++;
				pref += prefPart;
				prefPart = "";
			}

		}
		return ret;
	}


	String getPatternAt(int x, int y, Direction direction){
		String pattern = new String();

		Character ch;
		int start = direction.equals(Direction.HORIZONTAL) ? x:y;
		for(int i= start ; i<dimx*2; i++){
			if(direction.equals(Direction.HORIZONTAL)){
				ch = getCharAt(i, y);
			} else {	// VERTICAL
				ch = getCharAt(x, i);
			}
			if(ch.equals(emptyStoneCellCharacter)){
				return pattern;
			}
			pattern += ch;
		}
		return pattern;
	}


	public Canvas addWord(String word, int x, int y, Direction direction) {
		Canvas other = new Canvas(this);
		if(direction.equals(Direction.HORIZONTAL)){
			other.setWordH(word, x, y, false);
		}
		else{
			other.setWordV(word, x, y, false);
		}
		return other;
	}


	public List<Coordinate> getEmptyCoordinates(Direction direction) {
		List<Coordinate> ret = new ArrayList<Coordinate>();
		for(int i = 0; i<canvas.length; i++){
			Character ch = canvas[i];
			if(ch == emptyStoneCellCharacter){
				Coordinate coordinate = index2Coordinate(i);

				if(direction.equals(Direction.HORIZONTAL)){
					coordinate.right();
					if(horizStarCoordinates.contains(coordinate) ){
						continue;
					}
				}
				else if(direction.equals(Direction.VERTICAL)){
					coordinate.down();
					if(vertStarCoordinates.contains(coordinate) ){
						continue;
					}
				}
				if(coordinate.isIn(dimx, dimy)){
					if( ! getCharAt(coordinate).equals(emptyStoneCellCharacter)){
						ret.add(coordinate);
					}
				}
			}
		}
		return ret;
	}


	public List<Coordinate> getEmptyCoordinate2(Direction direction) {
		List<Coordinate> ret = new ArrayList<Coordinate>();
		for(int i = 0; i<canvas.length; i++){
			Character ch = canvas[i];
			if(ch != emptyStoneCellCharacter){
				Coordinate coordinate = index2Coordinate(i);

				if(direction.equals(Direction.HORIZONTAL)){
					Coordinate coordLeft = coordinate.left();
					if(getCharAt(coordLeft).equals(emptyStoneCellCharacter)){
						if(!horizStarCoordinates.contains(coordinate) ){
							if(coordinate.isIn(dimx, dimy)){
								ret.add(coordinate);
							}
						}
					}
				}
				else if(direction.equals(Direction.VERTICAL)){
					Coordinate coordUp = coordinate.up();
					if(getCharAt(coordUp).equals(emptyStoneCellCharacter)){
						if(!vertStarCoordinates.contains(coordinate) ){
							if(coordinate.isIn(dimx, dimy)){
								ret.add(coordinate);
							}
						}
					}
				}
//				if(coordinate.isIn(dimx, dimy)){
//					if( ! getCharAt(coordinate).equals(emptyStoneCellCharacter)){
//						ret.add(coordinate);
//					}
//				}
			}
		}
		return ret;
	}

	private Object getCharAt(Coordinate coordinate) {
		return getCharAt(coordinate.x, coordinate.y);
	}

	Coordinate index2Coordinate(int idx){
		int x = idx;
		int y = -1;
		while(idx>=0){
			x = idx;
			y++;
			idx -= dimx;
		}
		return new Coordinate(x, y);
	}

	public int getHeight() {
		return dimy;
	}

	public int getWidth() {
		return dimx;
	}



}

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
 * Simple class to store two elements.
 * This will be a struct in C.
 * This will be a tuple in python.
 * @author Benedek Racz
 *
 */
class Coordinate implements Comparable<Coordinate> {
	int x;
	int y;
	
	Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		Coordinate other = (Coordinate) obj;
		return (other.x == x) && other.y == y;
	}

	public Coordinate right() {
		return new Coordinate(x+1, y);
	}
	
	public Coordinate down() {
		return new Coordinate(x, y+1);
	}
	

	public Coordinate up() {
		return new Coordinate(x, y-1);
	}
	
	public Coordinate left() {
		return new Coordinate(x-1, y);
	}

	public boolean isIn(int dimx, int dimy) {
		return (x<dimx) && (y< dimy);
	}
	
	@Override
	public String toString() {
		return "x: " + x + " y: " + y;
	}

	@Override
	public int compareTo(Coordinate other) {
		return 100*(x-other.x) + y-other.y;
	}
}

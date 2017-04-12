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
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CanvasTest {

	static String testWord1 = "Hello";
	static String testWord2 = "Word";
	
	@Test
	public void testSetWordH() {
		Canvas cnv = new Canvas(7, 7);
		cnv.setWordH(testWord1, 0, 0);
		assertEquals(testWord1, cnv.getWordAt(0, 0, Direction.HORIZONTAL).str);
		cnv.setWordH(testWord2, 0, 1);
		assertEquals(testWord2, cnv.getWordAt(0, 1, Direction.HORIZONTAL).str);
		assertEquals(testWord1, cnv.getWordAt(0, 0, Direction.HORIZONTAL).str);
	}

	@Test
	public void testSetWordV() {
		Canvas cnv = new Canvas(7, 7);
		cnv.setWordV(testWord1, 0, 0);
		assertEquals(testWord1, cnv.getWordAt(0, 0, Direction.VERTICAL).str);
		cnv.setWordV(testWord2, 0, 1);
		assertEquals(testWord2, cnv.getWordAt(0, 1, Direction.VERTICAL).str);
	}

	@Test
	public void testEquals() {

		Canvas cnv1 = new Canvas(7, 7);
		Canvas cnv2 = new Canvas(7, 7);

		assertEquals(cnv1, cnv2);
		
		cnv1.setWordV(testWord1, 0, 0);
		cnv2.setWordV(testWord1, 0, 0);
		
		assertEquals(cnv1, cnv2);
		
	}
	

	@Test
	public void testGetEmptyCoordinates() {
		Canvas cnv = new Canvas(7, 7);
		cnv.setWordV("hello", 1, 1);
		Coordinate[] exp1 = {new Coordinate(1, 1), new Coordinate(1, 2),
				new Coordinate(1, 3), new Coordinate(1, 4), new Coordinate(1, 5),
				new Coordinate(2, 6)};
		List<Coordinate> coordinates = cnv.getEmptyCoordinates(Direction.HORIZONTAL);
		assertEquals(6, coordinates.size());
		Coordinate[] actual = coordinates.toArray(new Coordinate[0]);
		assertArrayEqualsOrderless(exp1, actual);
	}

	static void  assertArrayEqualsOrderless(Object[] expected, Object[] prfises) {
		Arrays.sort(prfises);	// Do sorting because the order is not matter.
		Arrays.sort(expected);	// Do sorting because the order is not matter.
		assertArrayEquals(expected, prfises);		
	}


}

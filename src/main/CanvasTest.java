package main;
import static org.junit.Assert.*;
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
	

}

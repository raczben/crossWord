package main;
import static org.junit.Assert.*;
import org.junit.Test;

public class MainTest {

	static String testWord1 = "Hello";
	static String testWord2 = "Word";
	
	@Test
	public void testSetWordH() {
		Main main = new Main(7, 7);
		main.setWordH(testWord1, 0, 0);
		assertEquals(testWord1, main.getWordAtH(0, 0));
		main.setWordH(testWord2, 0, 1);
		assertEquals(testWord2, main.getWordAtH(0, 1));
		assertEquals(testWord1, main.getWordAtH(0, 0));
	}

	@Test
	public void testSetWordV() {
		Main main = new Main(7, 7);
		main.setWordV(testWord1, 0, 0);
		assertEquals(testWord1, main.getWordAtV(0, 0));
		main.setWordV(testWord2, 0, 1);
		assertEquals(testWord2, main.getWordAtV(0, 1));
	}

	@Test
	public void testEquals() {

		Main main1 = new Main(7, 7);
		Main main2 = new Main(7, 7);

		assertEquals(main1, main2);
		
		main1.setWordV(testWord1, 0, 0);
		main2.setWordV(testWord1, 0, 0);
		
		assertEquals(main1, main2);
		
	}
	
	@Test
	public void testRemoveWordV() {
		Main main1 = new Main(7, 7);
		Main main2 = new Main(7, 7);
		main1.setWordV(testWord1, 0, 0);
		main1.removeWordAtV(0, 0);
		assertEquals(main1, main2);
		main1.setWordV(testWord2, 0, 1);
		main1.removeWordAtV(0, 1);
		assertEquals(main1, main2);
		
		main1.setWordV("zuhatag", 2, 0);
		main1.removeWordAtV(2, 0);
		assertEquals(main1, main2);
		
		
	}
	
	@Test
	public void testRemoveWordH() {
		Main main1 = new Main(7, 7);
		Main main2 = new Main(7, 7);
		main1.setWordH(testWord1, 0, 0);
		main1.removeWordAtH(0, 0);
		assertEquals(main1, main2);
		main1.setWordH(testWord2, 0, 1);
		main1.removeWordAtH(0, 1);
		assertEquals(main1, main2);
	}


}

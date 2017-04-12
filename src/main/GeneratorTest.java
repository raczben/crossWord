/**
 * 
 */
package main;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author ebenera
 *
 */
public class GeneratorTest {

	@Test
	public void testGeneratePrefixes() {
		Generator generator = null;
		try {

			generator = new Generator(true);
		} catch (Exception e) {
			fail();
		}

		String[] prfises = generator.generatePrefixes("baba", 2);
		String[] expected = {"ba", ".a", "b."};
		assertArrayEqualsOrderless(expected, prfises);


		prfises = generator.generatePrefixes("", 2);
		String[] expected2 = {};
		assertArrayEqualsOrderless(expected2, prfises);


		prfises = generator.generatePrefixes("a", 2);
		String[] expected3 = {"a"};
		assertArrayEqualsOrderless(expected3, prfises);

		prfises = generator.generatePrefixes("papa", 3);
		String[] expected4 = {"pap", "pa.", "p.p", ".ap", "p..", "..p", ".a."};
		assertArrayEqualsOrderless(expected4, prfises);

		prfises = generator.generatePrefixes("mo", 3);
		String[] expected5 = {"mo", "m.", ".o"};
		assertArrayEqualsOrderless(expected5, prfises);
	}

	@Test
	public void testGetWordsMatchPattern() {
		Generator generator = null;
		try {
			generator = new Generator(true);
		} catch (Exception e) {
			fail();
		}
		
		String[] allWordsOrig = {"baba", "mama", "papa", "hideg", "mo", "m", "kova", "kov", "ko"};
		
		for(String word: allWordsOrig){
			generator.addDictionaryWord(word);
		}

		String[] allWords = generator.getAllDictionaryWords();
		assertArrayEqualsOrderless(allWordsOrig, allWords);

		String[] matchedWords = generator.getWordsMatchPattern("baba");
		String[] expected = {"baba"};
		assertArrayEqualsOrderless(expected, matchedWords);
		matchedWords = generator.getWordsMatchPattern("b.b.");
		assertArrayEqualsOrderless(expected, matchedWords);
		
		matchedWords = generator.getWordsMatchPattern("bab");
		String[] expected2 = {};
		assertArrayEqualsOrderless(expected2, matchedWords);
		
		
		matchedWords = generator.getWordsMatchPattern(".a.a...");
		String[] expected3 = {"baba", "mama", "papa"};
		assertArrayEqualsOrderless(expected3, matchedWords);
		
		
		matchedWords = generator.getWordsMatchPattern("...a...");
		String[] expected4 = {};
		assertArrayEqualsOrderless(expected4, matchedWords);

	}
	

	@Test
	public void testReplaceSpecials() { 
		String testWordStr =  new String("\u00E1rv\u00EDzt\u0171r\u0151t\u00FCk\u00F6rf\u00FAr\u00F3g\u00E9p");	// arvizturotukorfurogep
		
		System.out.println(testWordStr);
		
		assertEquals("\u00E1rvizt\u00FCr\u00F6t\u00FCk\u00F6rfurog\u00E9p", Generator.replaceSpecials(testWordStr));

	}

	static void  assertArrayEqualsOrderless(Object[] expected, Object[] prfises) {
		Arrays.sort(prfises);	// Do sorting because the order is not matter.
		Arrays.sort(expected);	// Do sorting because the order is not matter.
		assertArrayEquals(expected, prfises);		
	}

}

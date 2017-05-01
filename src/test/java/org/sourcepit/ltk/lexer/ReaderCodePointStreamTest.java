package org.sourcepit.ltk.lexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class ReaderCodePointStreamTest {

	@Test
	public void testEmpty() throws Exception {
		final String input = "";
		try (CodePointStream codePoints = new ReaderCodePointStream(new StringReader(input))) {
			assertEquals(-1, codePoints.next());
		}
	}

	@Test
	public void testBasicMultilingualPlaneCharacter() throws Exception {
		final String input = "a";
		try (CodePointStream codePoints = new ReaderCodePointStream(new StringReader(input))) {
			assertEquals(97, codePoints.next());
			assertEquals(-1, codePoints.next());
		}
	}

	@Test
	public void testBeyondBasicMultilingualPlaneCharacter() throws Exception {
		final String input = new String(Character.toChars(Character.MAX_CODE_POINT));
		assertEquals(2, input.length());

		try (CodePointStream codePoints = new ReaderCodePointStream(new StringReader(input))) {
			assertEquals(Character.MAX_CODE_POINT, codePoints.next());
			assertEquals(-1, codePoints.next());
		}
	}

	@Test
	public void testMalformedSurrogatePairCharacter() throws Exception {
		final char[] validPair = Character.toChars(Character.MAX_CODE_POINT);
		final char[] malformedPair = new char[] { validPair[0], 'a' };

		final String input = new String(malformedPair);
		assertEquals(2, input.length());

		try (CodePointStream codePoints = new ReaderCodePointStream(new StringReader(input))) {
			try {
				codePoints.next();
				fail();
			} catch (IOException e) {
				assertEquals("Malformed unicode surrogate characters sequenz", e.getMessage());
			}
		}
	}
}

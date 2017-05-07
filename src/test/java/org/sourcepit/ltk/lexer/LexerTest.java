
package org.sourcepit.ltk.lexer;

import static org.junit.Assert.assertEquals;
import static org.sourcepit.ltk.lexer.rules.LexerRules.anyChar;
import static org.sourcepit.ltk.lexer.rules.LexerRules.eof;
import static org.sourcepit.ltk.lexer.rules.LexerRules.group;
import static org.sourcepit.ltk.lexer.rules.LexerRules.noneOf;
import static org.sourcepit.ltk.lexer.rules.LexerRules.or;
import static org.sourcepit.ltk.lexer.rules.LexerRules.quantified;
import static org.sourcepit.ltk.lexer.rules.LexerRules.word;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sourcepit.ltk.lexer.rules.LexerRule;
import org.sourcepit.ltk.lexer.rules.Quantification;
import org.sourcepit.ltk.lexer.rules.SymbolSequenz;
import org.sourcepit.ltk.lexer.symbols.Eof;
import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.SymbolStream;
import org.sourcepit.ltk.lexer.symbols.UnicodeCharacterStream;

public class LexerTest {
	@Test
	public void test() throws Exception {

		List<LexerRule> rules = new ArrayList<>();

		rules.add(SymbolSequenz.valueOf("if"));
		rules.add(SymbolSequenz.valueOf("else"));
		rules.add(SymbolSequenz.valueOf(" "));
		rules.add(SymbolSequenz.valueOf(Eof.get()));

		SymbolStream symbolStream = new UnicodeCharacterStream(new StringReader("if else"));

		Lexer lexer = new Lexer(rules, symbolStream);

		assertEquals("if", asString(lexer.next()));
		assertEquals(" ", asString(lexer.next()));
		assertEquals("else", asString(lexer.next()));
		assertEquals("<EOF>", asString(lexer.next()));
	}

	@Test
	public void testQuantification() throws Exception {
		List<LexerRule> rules = new ArrayList<>();
		rules.add(new Quantification(SymbolSequenz.valueOf("ab")));
		rules.add(SymbolSequenz.valueOf(Eof.get()));

		SymbolStream symbolStream = new UnicodeCharacterStream(new StringReader("abab"));

		Lexer lexer = new Lexer(rules, symbolStream);
		assertEquals("abab", asString(lexer.next()));
		assertEquals("<EOF>", asString(lexer.next()));
	}

	@Test
	public void testNestedQuantification() throws Exception {

		List<LexerRule> rules = new ArrayList<>();
		rules.add(new Quantification(new Quantification(new Quantification(SymbolSequenz.valueOf("abab")))));
		rules.add(SymbolSequenz.valueOf(Eof.get()));

		SymbolStream symbolStream = asSymbolStream("abab");

		Lexer lexer = new Lexer(rules, symbolStream);
		assertEquals("abab", asString(lexer.next()));
		assertEquals("<EOF>", asString(lexer.next()));
	}

	@Test
	public void testString() throws Exception {


		// " ( [^\"] | \ . )* "

		List<LexerRule> rules = new ArrayList<>();
		rules.add(group(word("\""), quantified(or(noneOf("\\\""), group(word("\\"), anyChar())), 0, -1), word("\"")));
		rules.add(eof());

		Lexer lexer = new Lexer(rules, asSymbolStream("\"a\\n\""));

		assertEquals("\"a\\n\"", asString(lexer.next()));
		assertEquals("<EOF>", asString(lexer.next()));
	}

	public static String asString(Symbol[] symbols) {
		final StringBuilder sb = new StringBuilder();
		for (Symbol symbol : symbols) {
			symbol.append(sb);
		}
		return sb.toString();
	}

	public static UnicodeCharacterStream asSymbolStream(String input) {
		return new UnicodeCharacterStream(new StringReader(input));
	}
}

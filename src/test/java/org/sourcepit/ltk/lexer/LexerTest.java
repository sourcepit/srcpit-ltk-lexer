package org.sourcepit.ltk.lexer;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.multi.MultiRootPaneUI;

import org.junit.Test;
import org.sourcepit.ltk.lexer.rules.LexerRule;
import org.sourcepit.ltk.lexer.rules.Multi;
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
	public void testMultiRule() throws Exception {
		List<LexerRule> rules = new ArrayList<>();
		rules.add(new Multi(SymbolSequenz.valueOf("a")));
		rules.add(SymbolSequenz.valueOf(Eof.get()));
		
		SymbolStream symbolStream = new UnicodeCharacterStream(new StringReader("aaaa"));

		Lexer lexer = new Lexer(rules, symbolStream);
		assertEquals("aaaa", asString(lexer.next()));
		assertEquals("<EOF>", asString(lexer.next()));
	}

	private static String asString(Symbol[] symbols) {
		final StringBuilder sb = new StringBuilder();
		for (Symbol symbol : symbols) {
			symbol.append(sb);
		}
		return sb.toString();
	}
}

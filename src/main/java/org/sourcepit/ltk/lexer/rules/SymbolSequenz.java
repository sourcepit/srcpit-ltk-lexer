package org.sourcepit.ltk.lexer.rules;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.CodePointStream;
import org.sourcepit.ltk.lexer.symbols.ReaderCodePointStream;
import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.UnicodeCharacter;

public class SymbolSequenz extends AbstractLexerRule<Node> {

	private final List<? extends Symbol> symbols;

	public static SymbolSequenz valueOf(Symbol... symbols) {
		return new SymbolSequenz(Arrays.asList(symbols));
	}

	public static SymbolSequenz valueOf(String symbols) {
		try (CodePointStream codePoints = new ReaderCodePointStream(new StringReader(symbols))) {
			return fromStream(codePoints);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static SymbolSequenz fromStream(CodePointStream codePoints) throws IOException {
		final List<UnicodeCharacter> sequenz = new ArrayList<>();
		int codePoint = codePoints.next();
		while (codePoint != -1) {
			sequenz.add(UnicodeCharacter.valueOf(codePoint));
			codePoint = codePoints.next();
		}
		return new SymbolSequenz(sequenz);
	}

	public SymbolSequenz(List<? extends Symbol> symbols) {
		this.symbols = symbols;
	}

	@Override
	protected Node createNode() {
		return new Node();
	}

	@Override
	protected void onSymbol(Node lexeme, Symbol symbol) {
		final int lexemeLength = lexeme.getLength();
		if (symbols.size() >= lexemeLength && symbols.get(lexemeLength - 1).equals(symbol)) {
			lexeme.setState(symbols.size() == lexemeLength ? LexemeState.TERMINATED : LexemeState.INCOMPLETE);
		} else {
			lexeme.setState(LexemeState.DISCARDED);
		}
	}

}

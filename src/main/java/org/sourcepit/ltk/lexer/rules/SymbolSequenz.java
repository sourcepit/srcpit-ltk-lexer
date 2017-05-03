package org.sourcepit.ltk.lexer.rules;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sourcepit.ltk.lexer.rules.LexemeRef.LexemeRefBuilder;
import org.sourcepit.ltk.lexer.symbols.CodePointStream;
import org.sourcepit.ltk.lexer.symbols.ReaderCodePointStream;
import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.UnicodeCharacter;

import lombok.Value;

@Value
public class SymbolSequenz implements LexerRule {

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

	@Override
	public LexemeRef onSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol) {
		final LexemeRefBuilder result = LexemeRef.builder();
		result.rule(this);
		result.state(LexemeState.DISCARDED);
		result.offset(offset);
		result.length(length);
		if (symbols.size() >= length && symbols.get(length - 1).equals(symbol)) {
			result.state(symbols.size() == length ? LexemeState.TERMINATED : LexemeState.INCOMPLETE);
		}
		return result.build();
	}

}

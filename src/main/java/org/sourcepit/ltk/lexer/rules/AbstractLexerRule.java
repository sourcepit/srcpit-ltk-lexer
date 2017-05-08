package org.sourcepit.ltk.lexer.rules;

import java.util.ArrayList;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public abstract class AbstractLexerRule implements LexerRule {

	protected LexemeRef lexeme;

	private List<Symbol> visited;

	public void onStart(List<Symbol> symbolBuffer, int lexemeStart) {
		lexeme = new LexemeRef();
		lexeme.setRule(this);
		lexeme.setSymbolBuffer(symbolBuffer);
		lexeme.setState(LexemeState.INCOMPLETE);
		lexeme.setOffset(lexemeStart);

		visited = new ArrayList<Symbol>();
	}

	@Override
	public final LexemeRef onSymbol(int length, Symbol symbol) {

		switch (lexeme.getState()) {
		case INCOMPLETE:
			break;
		case DISCARDED:
		case TERMINATED:
		default:
			throw new IllegalStateException();
		}

		lexeme.setLength(lexeme.getLength() + 1);

		if (visited.contains(symbol)) {

			System.out.println(symbol);
		}

		visited.add(symbol);

		if (lexeme.getLength() != length) {
			System.out.println();
		}

		return onSymbol(symbol);
	}

	protected abstract LexemeRef onSymbol(Symbol symbol);

}

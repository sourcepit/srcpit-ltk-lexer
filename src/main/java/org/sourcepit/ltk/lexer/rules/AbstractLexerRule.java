package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public abstract class AbstractLexerRule implements LexerRule {

	protected LexemeState currentState = LexemeState.INCOMPLETE;

	protected List<Symbol> symbolBuffer;

	protected int lexemeStart;

	protected int lexemeLength;

	protected Symbol currentSymbol;

	public void onStart(List<Symbol> symbolBuffer, int lexemeStart) {
		this.currentState = LexemeState.INCOMPLETE;
		this.symbolBuffer = symbolBuffer;
		this.lexemeStart = lexemeStart;
	}

	@Override
	public final LexemeRef onSymbol(int length, Symbol symbol) {

		switch (currentState) {
		case INCOMPLETE:
			break;
		case DISCARDED:
		case TERMINATED:
		default:
			throw new IllegalStateException();
		}

		lexemeLength = length;
		currentSymbol = symbol;

		final LexemeRef result = onSymbol();

		currentState = result.getState();

		return result;
	}

	protected abstract LexemeRef onSymbol();

}

package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public abstract class AbstractLexerRule implements LexerRule {

	protected LexemeState currentState = LexemeState.INCOMPLETE;

	protected List<Symbol> symbolBuffer;

	protected int lexemeStart;

	protected int lexemeLength;

	protected Symbol currentSymbol;

	@Override
	public final LexemeRef onSymbol(List<Symbol> buff, int offset, int length, Symbol symbol) {

		if (length == 1) {
			init(buff, offset, length, symbol);
		} else {
			switch (currentState) {
			case INCOMPLETE:
				break;
			case DISCARDED:
			case TERMINATED:
			default:
				throw new IllegalStateException();
			}
		}

		lexemeLength = length;
		currentSymbol = symbol;

		final LexemeRef result = onSymbol();

		currentState = result.getState();

		return result;
	}

	protected void init(List<Symbol> symbolBuffer, int lexemeStart, int lexemeLength, Symbol currentSymbol) {
		this.currentState = LexemeState.INCOMPLETE;
		this.symbolBuffer = symbolBuffer;
		this.lexemeStart = lexemeStart;
		this.lexemeLength = lexemeLength;
		this.currentSymbol = currentSymbol;
	}

	protected abstract LexemeRef onSymbol();

}


package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Quantification extends AbstractLexerRule {

	private final LexerRule childRule;

	private final int min;
	private final int max;

	public Quantification(LexerRule childRule) {
		this(childRule, 0, -1);
	}

	public Quantification(LexerRule childRule, int min, int max) {
		this.childRule = childRule;
		this.min = min;
		this.max = max;
	}

	private int matchCount;

	private int childOffset, childLength;

	@Override
	public void onStart(List<Symbol> symbolBuffer, int lexemeStart) {
		super.onStart(symbolBuffer, lexemeStart);
		matchCount = 0;
		childOffset = lexemeStart;
		childLength = 1;
	}

	@Override
	protected LexemeRef onSymbol(Symbol currentSymbol) {

		if (childLength == 1) {
			childRule.onStart(lexeme.getSymbolBuffer(), childOffset);
		}

		final int lexemeLength = lexeme.getLength();

		final LexemeRef lexemeRef = childRule.onSymbol(childLength, currentSymbol);
		final int actualLexLength = lexemeRef.getOffset() + lexemeRef.getLength() - lexeme.getOffset();

		final LexemeState state;
		if (lexemeRef.getLength() == 0) {
			state = LexemeState.DISCARDED;
		} else {
			state = lexemeRef.getState();
		}

		switch (state) {
		case INCOMPLETE:
			childLength++;
			lexeme.setState(state);
			return lexeme;
		case TERMINATED:
			matchCount++;
			childOffset += lexemeRef.getLength();
			childLength = 1;
			if (max > -1) {
				if (matchCount == max) {
					lexeme.setState(LexemeState.TERMINATED);
					lexeme.setLength(actualLexLength);
					return lexeme;
				}
				if (matchCount > max) {
					lexeme.setState(LexemeState.DISCARDED);
					lexeme.setLength(actualLexLength);
					return lexeme;
				}
			}
			if (actualLexLength < lexemeLength) {
				return onSymbol(currentSymbol);
			} else {
				lexeme.setState(LexemeState.INCOMPLETE);
				lexeme.setLength(actualLexLength);
				return lexeme;
			}
		case DISCARDED:
			if (matchCount < min) {
				lexeme.setState(LexemeState.DISCARDED);
				return lexeme;
			}
			lexeme.setState(LexemeState.TERMINATED);
			lexeme.setLength(lexemeLength - 1);
			return lexeme;
		default:
			throw new IllegalStateException();
		}
	}

}


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
	protected void init(List<Symbol> symbolBuffer, int lexemeStart, int lexemeLength, Symbol currentSymbol) {
		super.init(symbolBuffer, lexemeStart, lexemeLength, currentSymbol);
		matchCount = 0;
		childOffset = lexemeStart;
		childLength = 1;
	}

	@Override
	protected LexemeRef onSymbol() {
		final LexemeRef lexemeRef = childRule.onSymbol(symbolBuffer, childOffset, childLength, currentSymbol);
		final int actualLexLength = lexemeRef.getOffset() + lexemeRef.getLength();

		final LexemeState state;
		if (lexemeRef.getLength() == 0) {
			state = LexemeState.DISCARDED;
		} else {
			state = lexemeRef.getState();
		}

		switch (state) {
		case INCOMPLETE:
			childLength++;
			return new LexemeRef(this, LexemeState.INCOMPLETE, lexemeStart, lexemeLength);
		case TERMINATED:
			matchCount++;
			childOffset += lexemeRef.getLength();
			childLength = 1;
			if (max > -1) {
				if (matchCount == max) {
					return new LexemeRef(this, LexemeState.TERMINATED, lexemeStart, actualLexLength);
				}
				if (matchCount > max) {
					return new LexemeRef(this, LexemeState.DISCARDED, lexemeStart, actualLexLength);
				}
			}
			if (actualLexLength < lexemeLength) {
				return onSymbol();
			} else {
				return new LexemeRef(this, LexemeState.INCOMPLETE, lexemeStart, actualLexLength);
			}
		case DISCARDED:
			if (matchCount < min) {
				return new LexemeRef(this, LexemeState.DISCARDED, lexemeStart, lexemeLength);
			}
			return new LexemeRef(this, LexemeState.TERMINATED, lexemeStart, lexemeLength - 1);
		default:
			throw new IllegalStateException();
		}
	}

}

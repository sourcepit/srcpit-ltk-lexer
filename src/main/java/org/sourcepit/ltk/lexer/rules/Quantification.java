
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

	// @Override
	// public LexemeRef onSymbol(List<Symbol> buff, int offset, int length,
	// Symbol symbol) {
	// if (currentState == LexemeState.INCOMPLETE) {
	// if (length == 1) {
	// matchCount = 0;
	// childOffset = offset;
	// childLength = 1;
	// }
	// return onSymbol(offset, length, symbol);
	// } else {
	// return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
	// }
	// }

	@Override
	protected void init(List<Symbol> symbolBuffer, int lexemeStart, int lexemeLength, Symbol currentSymbol) {
		super.init(symbolBuffer, lexemeStart, lexemeLength, currentSymbol);
		matchCount = 0;
		childOffset = lexemeStart;
		childLength = 1;
	}

	@Override
	protected LexemeRef onSymbol(int offset, int length, Symbol symbol) {
		final LexemeRef lexemeRef = childRule.onSymbol(symbolBuffer, childOffset, childLength, symbol);
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
			return new LexemeRef(this, LexemeState.INCOMPLETE, offset, length);
		case TERMINATED:
			matchCount++;
			childOffset += lexemeRef.getLength();
			childLength = 1;
			if (max > -1) {
				if (matchCount == max) {
					return new LexemeRef(this, LexemeState.TERMINATED, offset, actualLexLength);
				}
				if (matchCount > max) {
					return new LexemeRef(this, LexemeState.DISCARDED, offset, actualLexLength);
				}
			}
			if (actualLexLength < length) {
				return onSymbol(symbolBuffer, offset, length, symbol);
			} else {
				return new LexemeRef(this, LexemeState.INCOMPLETE, offset, actualLexLength);
			}
		case DISCARDED:
			if (matchCount < min) {
				return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
			}
			return new LexemeRef(this, LexemeState.TERMINATED, offset, length - 1);
		default:
			throw new IllegalStateException();
		}
	}

}

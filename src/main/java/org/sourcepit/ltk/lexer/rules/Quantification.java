
package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Quantification implements LexerRule {

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
	public LexemeRef onSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol) {
		if (prev.getState() == LexemeState.INCOMPLETE) {
			if (length == 1) {
				matchCount = 0;
				childOffset = offset;
				childLength = 1;
			}
			return internalOnSymbol(prev, buff, offset, length, symbol);
		}
		else {
			return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
		}
	}

	private LexemeRef internalOnSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol) {
		final LexemeRef lexemeRef = childRule.onSymbol(prev, buff, childOffset, childLength, symbol);
		final int actualLexLength = lexemeRef.getOffset() + lexemeRef.getLength();
		final LexemeState state = lexemeRef.getState();
		switch (state) {
			case INCOMPLETE :
				childLength++;
				return new LexemeRef(this, LexemeState.INCOMPLETE, offset, length);
			case TERMINATED :
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
					return internalOnSymbol(lexemeRef, buff, offset, length, symbol);
				}
				else {
					return new LexemeRef(this, LexemeState.INCOMPLETE, offset, actualLexLength);
				}
			case DISCARDED :
				if (matchCount < min) {
					return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
				}
				return new LexemeRef(this, LexemeState.TERMINATED, offset, length - 1);
			default :
				throw new IllegalStateException();
		}
	}

}

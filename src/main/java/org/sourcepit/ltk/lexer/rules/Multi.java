package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Multi implements LexerRule {

	private final LexerRule childRule;

	public Multi(LexerRule childRule) {
		this.childRule = childRule;
	}

	private int matchCount;

	private int childOffset, childLength;

	@Override
	public LexemeRef onSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol) {

		if (length == 1) {
			matchCount = 0;
			childOffset = 0;
			childLength = 1;
		}

		if (prev.getState() == LexemeState.INCOMPLETE) {
			final LexemeRef lexemeRef = childRule.onSymbol(prev, buff, childOffset, childLength, symbol);
			final LexemeState state = lexemeRef.getState();
			switch (state) {
			case INCOMPLETE:
				return new LexemeRef(this, LexemeState.INCOMPLETE, offset, length);
			case TERMINATED:
				matchCount++;
				childOffset = 0;
				childLength = 1;
				return new LexemeRef(this, LexemeState.INCOMPLETE, lexemeRef.getOffset(), lexemeRef.getLength());
			case DISCARDED:
				if (length == 1) {
					return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
				} else {
					return new LexemeRef(this, LexemeState.TERMINATED, offset, length - 1);
				}
			default:
				throw new IllegalStateException();
			}
		}

		return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
	}

}

package org.sourcepit.ltk.lexer.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Or implements LexerRule {

	private final List<LexemeRef> start;

	private List<LexemeRef> terminated;

	private List<LexemeRef> incomplete;

	public Or(List<LexerRule> rules) {
		start = new ArrayList<LexemeRef>(rules.size());
		for (LexerRule rule : rules) {
			start.add(new LexemeRef(rule, LexemeState.INCOMPLETE, 0, 0));
		}
	}

	@Override
	public LexemeRef onSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol) {

		final List<LexemeRef> prevLexemeRefs;

		if (length == 1) {
			terminated = new ArrayList<LexemeRef>();
			incomplete = new ArrayList<LexemeRef>();
			prevLexemeRefs = start;
		} else {
			prevLexemeRefs = new ArrayList<LexemeRef>(incomplete);
			incomplete = new ArrayList<LexemeRef>();
		}

		for (LexemeRef prevLexemeRef : prevLexemeRefs) {
			LexemeRef lexemeRef = prevLexemeRef.getRule().onSymbol(prevLexemeRef, buff, offset, length, symbol);
			LexemeState state = lexemeRef.getState();
			switch (state) {
			case DISCARDED:
				break;
			case INCOMPLETE:
				incomplete.add(lexemeRef);
				break;
			case TERMINATED:
				terminated.add(lexemeRef);
				break;
			default:
				throw new IllegalArgumentException();
			}
		}

		if (!incomplete.isEmpty()) {
			return new LexemeRef(this, LexemeState.INCOMPLETE, offset, length);
		}

		if (!terminated.isEmpty()) {
			if (terminated.size() > 1) {
				Collections.sort(terminated, new Comparator<LexemeRef>() {
					@Override
					public int compare(LexemeRef o1, LexemeRef o2) {
						return o1.getLength() - o2.getLength();
					}
				});
			}
			final LexemeRef lexemeRef = terminated.get(0);
			return new LexemeRef(this, LexemeState.TERMINATED, lexemeRef.getOffset(), lexemeRef.getLength());
		}

		return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
	}
}

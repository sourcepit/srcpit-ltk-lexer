
package org.sourcepit.ltk.lexer.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Or extends AbstractLexerRule {

	private final List<LexerRule> rules;

	private final List<LexemeRef> start;

	private List<LexemeRef> terminated;

	private List<LexemeRef> incomplete;

	public Or(List<LexerRule> rules) {
		this.rules = rules;
		start = new ArrayList<LexemeRef>(rules.size());
		for (LexerRule rule : rules) {
			start.add(new LexemeRef(rule, LexemeState.INCOMPLETE, 0, 0));
		}
	}

	@Override
	public void onStart(List<Symbol> symbolBuffer, int lexemeStart) {
		super.onStart(symbolBuffer, lexemeStart);
		for (LexerRule rule : rules) {
			rule.onStart(symbolBuffer, lexemeStart);
		}
	}

	@Override
	protected LexemeRef onSymbol() {
		final List<LexemeRef> prevLexemeRefs;

		if (lexemeLength == 1) {
			terminated = new ArrayList<LexemeRef>();
			incomplete = new ArrayList<LexemeRef>();
			prevLexemeRefs = start;
		} else {
			prevLexemeRefs = new ArrayList<LexemeRef>(incomplete);
			incomplete = new ArrayList<LexemeRef>();
		}

		for (LexemeRef prevLexemeRef : prevLexemeRefs) {
			LexemeRef lexemeRef = prevLexemeRef.getRule().onSymbol(lexemeLength, currentSymbol);
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
			return new LexemeRef(this, LexemeState.INCOMPLETE, lexemeStart, lexemeLength);
		}

		if (!terminated.isEmpty()) {
			if (terminated.size() > 1) {
				Collections.sort(terminated, new Comparator<LexemeRef>() {
					@Override
					public int compare(LexemeRef o1, LexemeRef o2) {
						final int lenDiff = o2.getLength() - o1.getLength();
						if (lenDiff == 0) {
							int i1 = rules.indexOf(o1.getRule());
							int i2 = rules.indexOf(o2.getRule());
							return i1 - i2;
						}
						return o2.getLength() - o1.getLength();
					}
				});
			}
			final LexemeRef lexemeRef = terminated.get(0);
			return new LexemeRef(lexemeRef.getRule(), LexemeState.TERMINATED, lexemeRef.getOffset(),
					lexemeRef.getLength());
		}

		return new LexemeRef(this, LexemeState.DISCARDED, lexemeStart, lexemeLength);
	}
}

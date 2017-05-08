/*
 * Copyright 2017 Bernd Vogt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Group extends AbstractLexerRule {

	private final List<LexerRule> rules;

	public Group(List<LexerRule> rules) {
		this.rules = rules;
	}

	private int ruleIndex, ruleOffset, ruleLength;
	private LexerRule rule;

	@Override
	protected void init(List<Symbol> symbolBuffer, int lexemeStart, int lexemeLength, Symbol currentSymbol) {
		super.init(symbolBuffer, lexemeStart, lexemeLength, currentSymbol);
		ruleIndex = 0;
		ruleOffset = lexemeStart;
		ruleLength = 1;
		rule = rules.get(ruleIndex);
	}

	@Override
	protected LexemeRef onSymbol(int offset, int length, Symbol symbol) {
		if (ruleIndex == rules.size()) {
			return new LexemeRef(this, LexemeState.DISCARDED, 0, length);
		}

		final LexemeRef lexemeRef = rule.onSymbol(symbolBuffer, ruleOffset, ruleLength, symbol);

		final LexemeState state = lexemeRef.getState();
		if (state == LexemeState.INCOMPLETE) {
			ruleLength++;
			return new LexemeRef(this, state, offset, length);
		}

		if (state == LexemeState.DISCARDED) {
			return new LexemeRef(this, state, offset, length);
		}

		if (state == LexemeState.TERMINATED) {

			final int actualLexLength = lexemeRef.getOffset() + lexemeRef.getLength() - offset;

			ruleIndex++;

			if (ruleIndex == rules.size()) {
				return new LexemeRef(this, state, offset, actualLexLength);
			}

			rule = rules.get(ruleIndex);

			ruleLength = 1;

			if (actualLexLength < length) {
				ruleOffset = offset + length - 1;
				return onSymbol(offset, ruleLength, symbol);
			} else {
				ruleOffset = offset + length;
				return new LexemeRef(this, LexemeState.INCOMPLETE, offset, actualLexLength);
			}

		}

		throw new IllegalStateException();
	}

}

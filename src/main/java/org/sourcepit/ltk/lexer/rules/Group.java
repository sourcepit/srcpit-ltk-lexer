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
	public void onStart(List<Symbol> symbolBuffer, int lexemeStart) {
		super.onStart(symbolBuffer, lexemeStart);
		ruleIndex = 0;
		ruleOffset = lexemeStart;
		ruleLength = 1;
		rule = rules.get(ruleIndex);
	}

	@Override
	protected LexemeRef onSymbol(Symbol symbol) {
		if (ruleIndex == rules.size()) {
			lexeme.setState(LexemeState.DISCARDED);
			return lexeme;
		}

		if (ruleLength == 1) {
			rule.onStart(lexeme.getSymbolBuffer(), ruleOffset);
		}

		final LexemeRef lexemeRef = rule.onSymbol(ruleLength, symbol);

		final LexemeState state = lexemeRef.getState();
		lexeme.setState(state);

		if (state == LexemeState.INCOMPLETE) {
			ruleLength++;
			return lexeme;
		}

		if (state == LexemeState.DISCARDED) {
			return lexeme;
		}

		if (state == LexemeState.TERMINATED) {

			final int actualLexLength = lexemeRef.getOffset() + lexemeRef.getLength() - lexeme.getOffset();

			ruleIndex++;

			if (ruleIndex == rules.size()) {
				lexeme.setLength(actualLexLength);
				return lexeme;
			}

			rule = rules.get(ruleIndex);

			ruleLength = 1;

			if (actualLexLength < lexeme.getLength()) {
				ruleOffset = lexeme.getOffset() + lexeme.getLength() - 1;
				return onSymbol(symbol);
			} else {
				ruleOffset = lexeme.getOffset() + lexeme.getLength();
				lexeme.setState(LexemeState.INCOMPLETE);
				lexeme.setLength(actualLexLength);
				return lexeme;
			}

		}

		throw new IllegalStateException();
	}

}

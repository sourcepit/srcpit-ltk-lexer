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

import java.util.ArrayList;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Group extends AbstractLexerRule<GroupNode> {

	private final List<LexerRule<?>> rules;

	public Group(List<LexerRule<?>> rules) {
		this.rules = rules;
	}

	@Override
	protected GroupNode createNode() {
		return new GroupNode(rules, new ArrayList<>(rules.size()));
	}

	@Override
	public GroupNode onStart(Node parent, List<Symbol> symbolBuffer, int lexemeStart) {
		final GroupNode node = super.onStart(parent, symbolBuffer, lexemeStart);
		node.setRuleIndex(0);
		node.setRuleOffset(lexemeStart);
		node.setRuleLength(1);
		return node;
	}

	@Override
	protected void onSymbol(GroupNode lexeme, Symbol symbol) {
		if (lexeme.getRuleIndex() == rules.size()) {
			lexeme.setState(LexemeState.DISCARDED);
			return;
		}

		final LexerRule<?> rule = rules.get(lexeme.getRuleIndex());
		foo(lexeme, rule, symbol);
	}

	@SuppressWarnings("unchecked")
	private <T extends Node> void foo(GroupNode lexeme, LexerRule<T> rule, Symbol symbol) {

		final T ruleNode;
		if (lexeme.getRuleLength() == 1) {
			ruleNode = rule.onStart(lexeme, lexeme.getSymbolBuffer(), lexeme.getRuleOffset());
			lexeme.getRuleNodes().add(ruleNode);
		} else {
			ruleNode = (T) lexeme.getRuleNodes().get(lexeme.getRuleIndex());
		}

		rule.onSymbol(ruleNode, lexeme.getRuleLength(), symbol);

		final LexemeState state = ruleNode.getState();
		lexeme.setState(state);

		if (state == LexemeState.INCOMPLETE) {
			lexeme.setRuleLength(lexeme.getRuleLength() + 1);
			return;
		}

		if (state == LexemeState.DISCARDED) {
			return;
		}

		if (state == LexemeState.TERMINATED) {

			final int actualLexLength = ruleNode.getOffset() + ruleNode.getLength() - lexeme.getOffset();

			lexeme.setRuleIndex(lexeme.getRuleIndex() + 1);

			if (lexeme.getRuleIndex() == rules.size()) {
				lexeme.setLength(actualLexLength);
				return;
			}

			// rule = (LexerRule<T>) lexeme.getRules().get(ruleIndex);

			lexeme.setRuleLength(1);
			if (actualLexLength < lexeme.getLength()) {
				lexeme.setRuleOffset(lexeme.getOffset() + lexeme.getLength() - 1);
				onSymbol(lexeme, symbol);
				return;
			} else {
				lexeme.setRuleOffset(lexeme.getOffset() + lexeme.getLength());
				lexeme.setState(LexemeState.INCOMPLETE);
				lexeme.setLength(actualLexLength);
				return;
			}

		}

		throw new IllegalStateException();
	}

}

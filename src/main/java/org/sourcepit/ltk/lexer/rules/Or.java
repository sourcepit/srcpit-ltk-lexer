
package org.sourcepit.ltk.lexer.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Or extends AbstractLexerRule<OrNode> {

	private final List<LexerRule<?>> rules;

	public Or(List<LexerRule<?>> rules) {
		this.rules = rules;
	}

	@Override
	public OrNode onStart(Node parent, List<Symbol> symbolBuffer, int lexemeStart) {
		final OrNode node = super.onStart(parent, symbolBuffer, lexemeStart);
		for (LexerRule<?> rule : rules) {
			final Node ruleNode = rule.onStart(node, symbolBuffer, lexemeStart);
			node.getRuleNodes().add(ruleNode);
		}
		return node;
	}

	@Override
	protected OrNode createNode() {
		final List<Node> start = new ArrayList<Node>(rules.size());
		for (LexerRule<?> rule : rules) {
			final Node lexeme = new Node();
			lexeme.setRule(rule);
			lexeme.setState(LexemeState.INCOMPLETE);
			start.add(lexeme);
		}
		return new OrNode(rules, new ArrayList<>(rules.size()), start);
	}

	@Override
	protected void onSymbol(OrNode lexeme, Symbol symbol) {
		final List<Node> prevLexemeRefs;

		if (lexeme.getLength() == 1) {
			lexeme.setTerminated(new ArrayList<>());
			lexeme.setIncomplete(new ArrayList<>());
			prevLexemeRefs = lexeme.getStart();
		} else {
			prevLexemeRefs = new ArrayList<>(lexeme.getIncomplete());
			lexeme.setIncomplete(new ArrayList<>());
		}

		for (Node prevLexemeRef : prevLexemeRefs) {
			LexerRule<?> rule = prevLexemeRef.getRule();
			foo(lexeme, symbol, lexeme.getLength(), rule);
		}

		if (!lexeme.getIncomplete().isEmpty()) {
			lexeme.setState(LexemeState.INCOMPLETE);
			return;
		}
		final List<Node> terminated = lexeme.getTerminated();
		if (!lexeme.getTerminated().isEmpty()) {
			if (terminated.size() > 1) {
				Collections.sort(terminated, new Comparator<Node>() {
					@Override
					public int compare(Node o1, Node o2) {
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
			final Node lexemeRef = terminated.get(0);
			lexeme.setRule(lexemeRef.getRule());
			lexeme.setState(lexemeRef.getState());
			lexeme.setOffset(lexemeRef.getOffset());
			lexeme.setLength(lexemeRef.getLength());
			return;
		}

		lexeme.setState(LexemeState.DISCARDED);

		return;
	}

	private <T extends Node> void foo(OrNode lexeme, Symbol symbol, final int lexemeLength, LexerRule<T> rule) {
		@SuppressWarnings("unchecked")
		T node = (T) lexeme.getRuleNodes().get(lexeme.getRules().indexOf(rule));
		rule.onSymbol(node, lexemeLength, symbol);
		LexemeState state = node.getState();
		switch (state) {
		case DISCARDED:
			break;
		case INCOMPLETE:
			lexeme.getIncomplete().add(node);
			break;
		case TERMINATED:
			lexeme.getTerminated().add(node);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
}

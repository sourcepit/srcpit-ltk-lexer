
package org.sourcepit.ltk.lexer.rules;

import java.util.ArrayList;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public class Quantification extends AbstractLexerRule<QuantificationNode> {

	private final LexerRule<?> childRule;
	private final int min;
	private final int max;

	public Quantification(LexerRule<?> childRule) {
		this(childRule, 0, -1);
	}

	public Quantification(LexerRule<?> childRule, int min, int max) {
		this.childRule = childRule;
		this.min = min;
		this.max = max;
	}

	@Override
	public QuantificationNode onStart(Node parent, List<Symbol> symbolBuffer, int lexemeStart) {
		final QuantificationNode node = super.onStart(parent, symbolBuffer, lexemeStart);
		node.setMatchCount(0);
		node.setChildOffset(lexemeStart);
		node.setChildLength(1);
		return node;
	}

	@Override
	protected QuantificationNode createNode() {
		return new QuantificationNode(childRule, new ArrayList<>());
	}

	@SuppressWarnings("unchecked")
	private <T extends Node> void foo(LexerRule<T> rule, Node lexemeRef, int length, Symbol symbol) {
		rule.onSymbol((T) lexemeRef, length, symbol);
	}

	@Override
	protected void onSymbol(QuantificationNode lexeme, Symbol currentSymbol) {

		if (lexeme.getChildLength() == 1) {
			final Node childNode = childRule.onStart(lexeme, lexeme.getSymbolBuffer(), lexeme.getChildOffset());
			lexeme.setChildNode(childNode);
			lexeme.getChildNodes().add(childNode);
		}

		Node lexemeRef = lexeme.getChildNode();
		foo(childRule, lexemeRef, lexeme.getChildLength(), currentSymbol);
		final int actualLexLength = lexemeRef.getOffset() + lexemeRef.getLength() - lexeme.getOffset();

		final LexemeState state;
		if (lexemeRef.getLength() == 0) {
			state = LexemeState.DISCARDED;
		} else {
			state = lexemeRef.getState();
		}

		switch (state) {
		case INCOMPLETE:
			lexeme.setChildLength(lexeme.getChildLength() + 1);
			lexeme.setState(state);
			return;
		case TERMINATED:
			lexeme.setMatchCount(lexeme.getMatchCount() + 1);
			lexeme.setChildOffset(lexeme.getChildOffset() + lexemeRef.getLength());
			lexeme.setChildLength(1);
			if (max > -1) {
				if (lexeme.getMatchCount() == max) {
					lexeme.setState(LexemeState.TERMINATED);
					lexeme.setLength(actualLexLength);
					return;
				}
				if (lexeme.getMatchCount() > max) {
					lexeme.setState(LexemeState.DISCARDED);
					lexeme.setLength(actualLexLength);
					return;
				}
			}
			if (actualLexLength < lexeme.getLength()) {
				onSymbol(lexeme, currentSymbol);
				return;
			} else {
				lexeme.setState(LexemeState.INCOMPLETE);
				lexeme.setLength(actualLexLength);
				return;
			}
		case DISCARDED:
			if (lexeme.getMatchCount() < min) {
				lexeme.setState(LexemeState.DISCARDED);
				return;
			}
			lexeme.setState(LexemeState.TERMINATED);
			lexeme.setLength(lexeme.getLength() - 1);
			return;
		default:
			throw new IllegalStateException();
		}
	}

}

package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public abstract class AbstractLexerRule<T extends Node> implements LexerRule<T> {

	@Override
	public T onStart(Node parent, List<Symbol> symbolBuffer, int lexemeStart) {
		T node = createNode();
		node.setParent(parent);
		node.setRule(this);
		node.setSymbolBuffer(symbolBuffer);
		node.setState(LexemeState.INCOMPLETE);
		node.setOffset(lexemeStart);
		return node;
	}

	protected abstract T createNode();

	@Override
	public void onSymbol(T lexeme, int length, Symbol symbol) {

		switch (lexeme.getState()) {
		case INCOMPLETE:
			break;
		case DISCARDED:
		case TERMINATED:
		default:
			throw new IllegalStateException();
		}

		lexeme.setLength(lexeme.getLength() + 1);

		onSymbol(lexeme, symbol);
	}

	protected abstract void onSymbol(T lexeme, Symbol symbol);

}

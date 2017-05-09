package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public interface LexerRule<T extends Node> {

	T onStart(Node parent, List<Symbol> symbolBuffer, int lexemeStart);

	void onSymbol(T node, int length, Symbol symbol);
}

package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public interface LexerRule {

	void onStart(List<Symbol> symbolBuffer, int lexemeStart);

	LexemeRef onSymbol(int length, Symbol symbol);
}

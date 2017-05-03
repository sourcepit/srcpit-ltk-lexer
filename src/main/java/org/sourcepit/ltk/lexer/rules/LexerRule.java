package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

public interface LexerRule {

	LexemeRef onSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol);
}

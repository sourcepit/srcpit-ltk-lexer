package org.sourcepit.ltk.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sourcepit.ltk.lexer.rules.LexemeState;
import org.sourcepit.ltk.lexer.rules.LexerRule;
import org.sourcepit.ltk.lexer.rules.Or;
import org.sourcepit.ltk.lexer.rules.OrNode;
import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.SymbolBuffer;
import org.sourcepit.ltk.lexer.symbols.SymbolStream;

public class Lexer {

	private final Or rule;

	private final SymbolBuffer symbolBuffer;

	public Lexer(List<LexerRule<?>> rules, SymbolStream symbolStream) {
		this.rule = new Or(rules);
		this.symbolBuffer = new SymbolBuffer(symbolStream);
	}

	public Symbol[] next() throws IOException {
		Symbol symbol = symbolBuffer.next();
		final List<Symbol> buffer = new ArrayList<Symbol>();
		OrNode node = rule.onStart(null, buffer, 0);
		buffer.add(symbol);
		do {
			rule.onSymbol(node, buffer.size(), symbol);
			LexemeState state = node.getState();
			switch (state) {
			case DISCARDED:
				// error
				return null;
			case INCOMPLETE:
				symbol = symbolBuffer.next();
				buffer.add(symbol);
				continue;
			case TERMINATED:
				return symbolBuffer.consume(node.getLength());
			default:
				throw new IllegalStateException();
			}
		} while (true);
	}

}

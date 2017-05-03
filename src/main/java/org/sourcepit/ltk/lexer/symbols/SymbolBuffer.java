package org.sourcepit.ltk.lexer.symbols;

import java.io.IOException;
import java.util.LinkedList;

public class SymbolBuffer implements SymbolStream {

	private final SymbolStream symbolStream;

	private final LinkedList<Symbol> buffer = new LinkedList<>();

	private int offset = 0;
	private int length = 0;
	
	public SymbolBuffer(SymbolStream symbolStream) {
		this.symbolStream = symbolStream;
	}

	@Override
	public Symbol next() throws IOException {
		final Symbol next;
		if (length > 0 && offset < length) {
			next = buffer.get(offset);
		} else {
			next = symbolStream.next();
			buffer.add(next);
		}
		offset++;
		return next;
	}
	
	public Symbol[] consume(int length) {
		final Symbol[] symbols = new Symbol[length];
		for (int i = 0; i < length; i++) {
			symbols[i] = buffer.removeFirst();
		}
		this.offset = 0;
		this.length = buffer.size();
		return symbols;
	}

	@Override
	public void close() throws IOException {
		symbolStream.close();
	}

}

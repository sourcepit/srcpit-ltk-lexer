package org.sourcepit.ltk.lexer.symbols;

import lombok.Value;

@Value
public final class Eof implements Symbol {

	private final static Eof INSTANCE = new Eof();

	private Eof() {
		super();
	}

	@Override
	public void append(StringBuilder stringBuilder) {
		stringBuilder.append("<EOF>");
	}

	public static Eof get() {
		return INSTANCE;
	}
}

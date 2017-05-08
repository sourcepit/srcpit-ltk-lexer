package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;

import lombok.Data;

@Data
public class LexemeRef {
	private List<Symbol> symbolBuffer;
	private LexerRule rule;
	private LexemeState state;
	private int offset;
	private int length;
}
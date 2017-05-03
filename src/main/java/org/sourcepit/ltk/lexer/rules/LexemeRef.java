package org.sourcepit.ltk.lexer.rules;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LexemeRef {
	LexerRule rule;
	LexemeState state;
	int offset;
	int length;
}
package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuantificationNode extends Node {
	private final LexerRule<?> childRule;
	private final List<Node> childNodes;
	private Node childNode;

	private int matchCount;

	private int childOffset, childLength;
}

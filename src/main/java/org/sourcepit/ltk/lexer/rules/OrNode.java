package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrNode extends Node {
	private final List<LexerRule<?>> rules;

	private final List<Node> ruleNodes;

	private final List<Node> start;

	private List<Node> terminated;

	private List<Node> incomplete;
}

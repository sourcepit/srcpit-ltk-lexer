package org.sourcepit.ltk.lexer.rules;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroupNode extends Node {

	private int ruleIndex, ruleOffset, ruleLength;

	private final List<LexerRule<?>> rules;

	private final List<Node> ruleNodes;

}

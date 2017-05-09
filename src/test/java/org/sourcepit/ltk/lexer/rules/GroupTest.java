/*
 * Copyright 2017 Bernd Vogt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sourcepit.ltk.lexer.rules;

import static org.junit.Assert.*;
import static org.sourcepit.ltk.lexer.rules.LexerRules.group;
import static org.sourcepit.ltk.lexer.rules.LexerRules.quantified;
import static org.sourcepit.ltk.lexer.rules.LexerRules.word;

import java.io.IOException;

import org.junit.Test;

public class GroupTest extends AbstractLexerRuleTest<GroupNode> {

	@Test
	public void testSimpleGroup() throws IOException {

		setRule(group(word("a"), word("bc"), word("def")));
		setInput("abcdefg");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(4, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(5, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(6, lex.getLength());

		try {
			next();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void testGroupWithQuantifier() throws IOException {

		setRule(group(word("a"), quantified(word("b"), 1, -1), word("c")));
		setInput("abbcd");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(4, lex.getLength());

		try {
			next();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void testNestedGroups() throws IOException {

		setRule(group(word("a"), group(word("b")), word("c")));
		setInput("abc");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());

		try {
			next();
			fail();
		} catch (IllegalStateException e) {
		}

	}
	
	@Test
	public void testOffset() throws Exception {
		setRule(group(word("a"), group(word("b")), word("c")));
		setInput("xxabc", 2);
		
		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(1, lex.getLength());
		
		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(2, lex.getLength());
		
		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(3, lex.getLength());
	}

}

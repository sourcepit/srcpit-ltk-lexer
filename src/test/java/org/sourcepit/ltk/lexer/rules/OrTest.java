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
import static org.sourcepit.ltk.lexer.rules.LexerRules.or;
import static org.sourcepit.ltk.lexer.rules.LexerRules.quantified;
import static org.sourcepit.ltk.lexer.rules.LexerRules.word;

import java.io.IOException;

import org.junit.Test;

public class OrTest extends AbstractLexerRuleTest {

	@Test
	public void testPrecedence1() throws IOException {

		LexerRule rule1 = word("aaa");
		LexerRule rule2 = quantified(word("a"), 0, -1);
		setRule(or(rule1, rule2));

		setInput("aaa");

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

		assertEquals(rule1, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());
	}

	@Test
	public void testPrecedence2() throws IOException {

		LexerRule rule1 = quantified(word("a"), 0, -1);
		LexerRule rule2 = word("aaa");
		setRule(or(rule1, rule2));

		setInput("aaa");

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

		assertEquals(rule1, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());
	}

	@Test
	public void testPrecedence3() throws IOException {

		LexerRule rule1 = quantified(word("a"), 0, -1);
		LexerRule rule2 = word("aa");
		setRule(or(rule1, rule2));

		setInput("aaa");

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

		assertEquals(rule1, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());
	}

	@Test
	public void testOffset() throws Exception {
		LexerRule rule1 = word("ab");
		LexerRule rule2 = word("bc");
		setRule(or(rule1, rule2));

		setInput("xxabbc", 2);

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule1, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(2, lex.getLength());
	}
}

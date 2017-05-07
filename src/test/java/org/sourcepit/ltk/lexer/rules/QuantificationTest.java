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

public class QuantificationTest extends AbstractLexerRuleTest {

	@Test
	public void testSimple() throws IOException {

		setRule(quantified(word("a"), 0, -1));
		setInput("aa");

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
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(4, lex.getLength());
	}

	@Test
	public void testEmpty() throws IOException {

		setRule(quantified(word("a"), 0, -1));
		setInput("");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(0, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());
	}

	@Test
	public void testMin1() throws Exception {
		setRule(quantified(word("a"), 1, -1));
		setInput("");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());
	}

	@Test
	public void testMin2() throws Exception {
		setRule(quantified(word("a"), 2, -1));
		setInput("a");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		setInput("aa");

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
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(4, lex.getLength());
	}

	@Test
	public void testMax() throws Exception {
		setRule(quantified(word("a"), 0, 2));
		setInput("aaa");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());
	}

	@Test
	public void testMinMax() throws Exception {
		setRule(quantified(word("a"), 2, 2));
		setInput("ab");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		setInput("aab");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());
	}

	@Test
	public void testNestedMinMax() throws Exception {

		// (a+|b+){2,2}

		setRule(quantified(or(quantified(word("a"), 1, -1), quantified(word("b"), 1, -1)), 2, 2));

		setInput("ab");

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
		assertEquals(2, lex.getLength());
	}

}

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
import static org.sourcepit.ltk.lexer.rules.LexerRules.noneOf;

import java.io.IOException;

import org.junit.Test;

public class NoneOfTest extends AbstractLexerRuleTest {

	@Test
	public void testEof() throws IOException {
		setRule(noneOf("abc"));
		setInput("");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());
		
		setInput("xx", 2);

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(1, lex.getLength());
	}

	@Test
	public void testNad() throws IOException {
		setRule(noneOf("abc"));
		setInput("a");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		setInput("xxa", 2);

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(1, lex.getLength());
	}

	@Test
	public void testGood() throws IOException {
		setRule(noneOf("abc"));
		setInput("d");

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());
		
		setInput("xxd", 2);

		next();

		assertEquals(rule, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(2, lex.getOffset());
		assertEquals(1, lex.getLength());
	}

}

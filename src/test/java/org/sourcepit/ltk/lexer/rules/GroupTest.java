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

import static org.junit.Assert.assertEquals;
import static org.sourcepit.ltk.lexer.rules.LexerRules.group;
import static org.sourcepit.ltk.lexer.rules.LexerRules.quantified;
import static org.sourcepit.ltk.lexer.rules.LexerRules.word;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sourcepit.ltk.lexer.LexerTest;
import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.SymbolStream;

public class GroupTest {

	private LexemeRef prev;

	private List<Symbol> buff;
	private Symbol symbol;
	private LexemeRef lex;

	private LexerRule group;

	private SymbolStream symbolStream;

	@Before
	public void setUp() {
		lex = new LexemeRef(null, LexemeState.INCOMPLETE, 0, 0);
		buff = new ArrayList<>();
	}

	private void next() throws IOException {
		prev = lex;
		symbol = symbolStream.next();
		buff.add(symbol);
		lex = group.onSymbol(prev, buff, 0, buff.size(), symbol);
	}


	@Test
	public void testSimpleGroup() throws IOException {

		group = group(word("a"), word("bc"), word("def"));

		symbolStream = LexerTest.asSymbolStream("abcdefg");

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(4, lex.getLength());

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(5, lex.getLength());

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(6, lex.getLength());

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(7, lex.getLength());
	}

	@Test
	public void testGroupWithQuantifier() throws IOException {

		group = group(word("a"), quantified(word("b"), 1, -1), word("c"));

		symbolStream = LexerTest.asSymbolStream("abbcd");

		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(1, lex.getLength());
		
		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(2, lex.getLength());
		
		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.INCOMPLETE, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(3, lex.getLength());
		
		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.TERMINATED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(4, lex.getLength());
		
		next();

		assertEquals(group, lex.getRule());
		assertEquals(LexemeState.DISCARDED, lex.getState());
		assertEquals(0, lex.getOffset());
		assertEquals(5, lex.getLength());
	}

}

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

import static org.sourcepit.ltk.lexer.LexerTest.asSymbolStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.SymbolStream;

public abstract class AbstractLexerRuleTest<T extends Node> {

	protected List<Symbol> buff;
	protected int offset = 0;
	protected Symbol symbol;

	protected LexerRule<T> rule;

	protected SymbolStream symbolStream;
	
	protected T lex;

	protected void setRule(LexerRule<T> rule) {
		this.rule = rule;
	}

	protected void setInput(String input) throws IOException {
		setInput(input, 0);
	}

	protected void setInput(String input, int offset) throws IOException {
		buff = new ArrayList<>();
		symbolStream = asSymbolStream(input);

		for (int i = 0; i < offset; i++) {
			buff.add(symbolStream.next());
		}

		this.offset = offset;
	}

	protected void next() throws IOException {
		symbol = symbolStream.next();
		if (buff.size() == offset) {
			lex = rule.onStart(null, buff, offset);
		}
		buff.add(symbol);
		rule.onSymbol(lex, buff.size() - offset, symbol);
	}

}

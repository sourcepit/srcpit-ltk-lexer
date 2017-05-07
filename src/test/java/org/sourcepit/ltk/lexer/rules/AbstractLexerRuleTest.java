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

public abstract class AbstractLexerRuleTest {

	protected LexemeRef prev;

	protected List<Symbol> buff;
	protected Symbol symbol;
	protected LexemeRef lex;

	protected LexerRule rule;

	protected SymbolStream symbolStream;

	protected void setRule(LexerRule rule) {
		this.rule = rule;
	}

	protected void setInput(String input) {
		lex = new LexemeRef(null, LexemeState.INCOMPLETE, 0, 0);
		buff = new ArrayList<>();
		symbolStream = asSymbolStream(input);
	}

	protected void next() throws IOException {
		prev = lex;
		symbol = symbolStream.next();
		buff.add(symbol);
		lex = rule.onSymbol(prev, buff, 0, buff.size(), symbol);
	}

}

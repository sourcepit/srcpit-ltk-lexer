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

import java.util.Arrays;

import org.sourcepit.ltk.lexer.symbols.Eof;
import org.sourcepit.ltk.lexer.symbols.UnicodeCharacter;

public final class LexerRules {
	private LexerRules() {
		super();
	}

	public static LexerRule word(String word) {
		return SymbolSequenz.valueOf(word);
	}

	public static LexerRule or(LexerRule... rules) {
		return new Or(Arrays.asList(rules));
	}

	public static LexerRule quantified(LexerRule rule, int min, int max) {
		return new Quantification(rule, min, max);
	}

	public static LexerRule group(LexerRule... rules) {
		return new Group(Arrays.asList(rules));
	}

	public static LexerRule anyChar() {
		return new AnyUnicodeCharacter();
	}

	public static LexerRule noneOf(String chars) {
		return new NoneOf(UnicodeCharacter.toCharacters(chars));
	}

	public static LexerRule eof() {
		return SymbolSequenz.valueOf(Eof.get());
	}
}

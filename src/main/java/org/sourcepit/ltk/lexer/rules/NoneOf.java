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

import java.util.List;

import org.sourcepit.ltk.lexer.symbols.Symbol;
import org.sourcepit.ltk.lexer.symbols.UnicodeCharacter;

public class NoneOf implements LexerRule {

	private final List<UnicodeCharacter> characters;

	public NoneOf(List<UnicodeCharacter> characters) {
		this.characters = characters;
	}

	@Override
	public LexemeRef onSymbol(LexemeRef prev, List<Symbol> buff, int offset, int length, Symbol symbol) {
		if (length == 1 && symbol instanceof UnicodeCharacter) {
			for (UnicodeCharacter c : characters) {
				if (c.equals(symbol)) {
					return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
				}
			}
			return new LexemeRef(this, LexemeState.TERMINATED, offset, length);
		}
		return new LexemeRef(this, LexemeState.DISCARDED, offset, length);
	}

}

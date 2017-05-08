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

public class NoneOf extends AbstractLexerRule {

	private final List<UnicodeCharacter> characters;

	public NoneOf(List<UnicodeCharacter> characters) {
		this.characters = characters;
	}

	@Override
	protected LexemeRef onSymbol(Symbol symbol) {
		lexeme.setState(LexemeState.DISCARDED);
		if (lexeme.getLength() == 1 && symbol instanceof UnicodeCharacter) {
			for (UnicodeCharacter c : characters) {
				if (c.equals(symbol)) {
					return lexeme;
				}
			}
			lexeme.setState(LexemeState.TERMINATED);
		}
		return lexeme;
	}

}

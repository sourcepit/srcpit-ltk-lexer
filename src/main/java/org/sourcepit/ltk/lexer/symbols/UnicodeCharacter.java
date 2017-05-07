
package org.sourcepit.ltk.lexer.symbols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Value;

@Value
public final class UnicodeCharacter implements Symbol {

	private final int codePoint;

	public static List<UnicodeCharacter> toCharacters(String str) {
		try (UnicodeCharacterStream stream = UnicodeCharacterStream.fromString(str)) {
			final List<UnicodeCharacter> characters = new ArrayList<>(str.length());
			Symbol s = stream.next();
			while (s instanceof UnicodeCharacter) {
				characters.add((UnicodeCharacter) s);
				s = stream.next();
			}
			return characters;
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static UnicodeCharacter valueOf(int codePoint) {
		return new UnicodeCharacter(codePoint);
	}

	private UnicodeCharacter(int codePoint) {
		Character.isValidCodePoint(codePoint);
		this.codePoint = codePoint;
	}

	@Override
	public void append(StringBuilder stringBuilder) {
		stringBuilder.append(Character.toChars(codePoint));
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("UnicodeCharacter [codePoint=");
		builder.append(codePoint);
		builder.append(", javaChars=");
		builder.append(Arrays.toString(Character.toChars(codePoint)));
		builder.append("]");
		return builder.toString();
	}

}

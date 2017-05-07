
package org.sourcepit.ltk.lexer.symbols;

import java.io.IOException;
import java.io.Reader;

public class UnicodeCharacterStream implements SymbolStream {

	private final CodePointStream codePointStream;

	public static UnicodeCharacterStream fromString(String str) {
		return new UnicodeCharacterStream(ReaderCodePointStream.fromString(str));
	}

	public UnicodeCharacterStream(Reader reader) {
		this(new ReaderCodePointStream(reader));
	}

	public UnicodeCharacterStream(CodePointStream codePointStream) {
		this.codePointStream = codePointStream;
	}

	@Override
	public Symbol next() throws IOException {
		final int codePoint = codePointStream.next();
		if (codePoint == -1) {
			return Eof.get();
		}
		else {
			return UnicodeCharacter.valueOf(codePoint);
		}
	}

	@Override
	public void close() throws IOException {
		codePointStream.close();
	}

}

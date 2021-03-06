package org.sourcepit.ltk.lexer.symbols;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ReaderCodePointStream implements CodePointStream {

	private final Reader charSource;
	
	public static ReaderCodePointStream fromString(String str) {
		return new ReaderCodePointStream(new StringReader(str));
	}

	public ReaderCodePointStream(Reader charSource) {
		this.charSource = charSource;
	}

	@Override
	public int next() throws IOException {
		final int c = charSource.read();
		return c > -1 ? next((char) c) : c;
	}

	private int next(final char c) throws IOException {
		if (Character.isHighSurrogate(c)) {
			final char low = (char) charSource.read();
			if (Character.isLowSurrogate(low)) {
				return Character.toCodePoint(c, low);
			} else {
				throw new IOException("Malformed unicode surrogate characters sequenz");
			}
		} else {
			return c;
		}
	}

	@Override
	public void close() throws IOException {
		charSource.close();
	}

}

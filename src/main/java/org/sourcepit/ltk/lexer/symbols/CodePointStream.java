package org.sourcepit.ltk.lexer.symbols;

import java.io.Closeable;
import java.io.IOException;

public interface CodePointStream extends Closeable {

	int next() throws IOException;

}
package org.sourcepit.ltk.lexer.symbols;

import java.io.Closeable;
import java.io.IOException;

public interface SymbolStream extends Closeable {

	Symbol next() throws IOException;

}
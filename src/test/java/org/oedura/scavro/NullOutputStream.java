package org.oedura.scavro;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

// TODO: get rid of this duplicate class
public class NullOutputStream extends OutputStream {
    public NullOutputStream() {}

    @Override
    public void write(int b) throws IOException {/* noop */}

    public static PrintStream getPrintStream() {
        return new PrintStream(new NullOutputStream());
    }
}

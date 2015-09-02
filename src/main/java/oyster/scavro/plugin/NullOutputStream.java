package oyster.scavro.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class NullOutputStream extends OutputStream {
    public NullOutputStream() {}

    @Override
    public void write(int b) throws IOException {/* noop */}

    public static PrintStream getPrintStream() {
        return new PrintStream(new NullOutputStream());
    }
}

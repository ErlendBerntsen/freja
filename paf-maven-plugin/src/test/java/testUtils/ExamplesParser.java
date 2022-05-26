package testUtils;

import no.hvl.Parser;

import java.io.IOException;

public class ExamplesParser {

    public Parser parser;
    public static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
    }

}

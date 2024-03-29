package testUtils;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.Parser;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.builders.ReplacementBuilder;

import java.io.IOException;
import java.util.HashMap;

import static testUtils.TestUtils.getNodeWithId;

public class ExamplesParser {
    public Parser parser;
    public static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";
    public HashMap<String, Replacement> replacementMap;

    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
        createReplacementMap();
    }

    private void createReplacementMap() {
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        Replacement replacement = new ReplacementBuilder(node).build();
        BodyDeclaration<?> node2 = getNodeWithId(parser.getCompilationUnitCopies(), 14);
        Replacement replacement2 = new ReplacementBuilder(node2).build();
        BodyDeclaration<?> node3 = getNodeWithId(parser.getCompilationUnitCopies(), 39);
        Replacement replacement3 = new ReplacementBuilder(node3).build();
        replacementMap = new HashMap<>();
        replacementMap.put(replacement.getId(), replacement);
        replacementMap.put(replacement2.getId(), replacement2);
        replacementMap.put(replacement3.getId(), replacement3);
    }

}

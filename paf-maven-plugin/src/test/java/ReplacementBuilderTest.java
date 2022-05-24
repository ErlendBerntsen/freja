import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.Parser;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.builders.ReplacementBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.getNodeWithId;

class ReplacementBuilderTest {
    Parser parser;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
    }

    @Test
    void testBuildingRegularReplacement(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        Replacement replacement = new ReplacementBuilder(node).build();
        assertEquals("1", replacement.getId());
        Statement actualReplacementCode = StaticJavaParser
                .parseStatement("throw new UnsupportedOperationException(TODO.construtor(\"GPSPoint\"));");
        BlockStmt actualReplacementCodeAsBlockStmt = new BlockStmt(new NodeList<>(actualReplacementCode));
        assertEquals(actualReplacementCodeAsBlockStmt, replacement.getReplacementCode());
        Optional<CompilationUnit> file = node.findCompilationUnit();
        file.ifPresent(compilationUnit -> assertEquals(compilationUnit, replacement.getFile()));
    }

    @Test
    void testBuildingReplacementWithoutReplacementCodeAnnotation(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        assertThrows(IllegalArgumentException.class, replacementBuilder::build);
    }

    @Test
    void testBuildingReplacementWithoutBody(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        BlockStmt body = getBlockStmtFromBodyDeclaration(node);
        node.remove(body);
        assertThrows(IllegalArgumentException.class, replacementBuilder::build);
    }

    @Test
    void testBuildingReplacementWithEmptyBody(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 12);
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        assertThrows(IllegalArgumentException.class, replacementBuilder::build);
    }

    @Test
    void testBuildingReplacementWithoutFile(){
        BodyDeclaration<?> node = StaticJavaParser.parseBodyDeclaration("""
                @ReplacementCode(id = "2")
                public void throwExceptionForUnImplementedMethod(){
                    throw new UnsupportedOperationException(TODO.method());
                }""".indent(4));
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        assertThrows(IllegalStateException.class, replacementBuilder::build);
    }
}

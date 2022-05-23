import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import no.hvl.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class NodeUtilsTest {

    Parser parser;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
    }

    @Test
    void testGettingBodyDeclarationCopyInFiles(){
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        BodyDeclaration<?> node = getNodeWithId(files, 1);
        BodyDeclaration<?> nodeCopy = findBodyDeclarationCopyInFiles(files, node.clone());
        assertEquals(node, nodeCopy);
    }

    @Test
    void testGettingBodyDeclarationThatDoesNotExistInFiles(){
        BodyDeclaration<?> node = new MethodDeclaration();
        BodyDeclaration<?> nodeClone = node.clone();
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        assertThrows(IllegalStateException.class, () -> findBodyDeclarationCopyInFiles(files, nodeClone));
    }
}

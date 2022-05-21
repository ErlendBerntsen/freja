import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.Parser;
import no.hvl.annotations.CopyOption;
import no.hvl.utilities.AnnotationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class AnnotationUtilsTest {

    Parser parser;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
    }

    @Test
    void testNormalCopyOption(){
        Node node = TestUtils.getNodeWithId(parser.getCompilationUnitCopies(), 1);
        CopyOption copyOption = AnnotationUtils.getCopyOptionValueInImplementAnnotation((NodeWithAnnotations<?>) node);
        assertEquals(CopyOption.REMOVE_EVERYTHING, copyOption);
    }


}

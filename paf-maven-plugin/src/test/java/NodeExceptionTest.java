import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.EmptyStmt;
import no.hvl.exceptions.NodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;

import java.io.IOException;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.getNodeWithId;

public class NodeExceptionTest extends ExamplesParser {

    private String errorMessage;

    @BeforeEach
    void setup() throws IOException {
        init();
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        NodeException exception = new NodeException(node);
        errorMessage = exception.getMessage();
    }

    @Test
    void testFileNameIsListed(){
        assertTrue(errorMessage.contains("File name: Example.java"));
    }

    @Test
    void testLineStartIsListed(){
        assertTrue(errorMessage.contains("Line start: 9"));
    }

    @Test
    void testLineEndIsListed(){
        assertTrue(errorMessage.contains("Line end: 11"));
    }

    @Test
    void testEntireErrorMessage(){
        assertEquals("""
                There was an error with a node @
                File name: Example.java
                Line start: 9
                Line end: 11
                
                """, errorMessage);
    }

    @Test
    void testSubExceptionErrorMessage(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        try{
            getNumberValueInImplementAnnotation(node);
            fail("Should throw exception");
        }catch (Exception e){
            assertTrue(e instanceof NodeException);
            assertEquals(String.format("""
                There was an error with a node @
                File name: Example.java
                Line start: 17
                Line end: 18
                                
                Cause: Node is not annotated with "@%s" and thus can't get "%s" value""",
                    IMPLEMENT_NAME, IMPLEMENT_NUMBER_NAME), e.getMessage());
        }
    }

    @Test
    void testNodeWithoutFile(){
        EmptyStmt stmt = new EmptyStmt();
        NodeException exception = new NodeException(stmt);
        assertEquals("""
                There was an error with a node @
                File name: UNKNOWN
                Line start: UNKNOWN
                Line end: UNKNOWN
                
                """, exception.getMessage());
    }
}

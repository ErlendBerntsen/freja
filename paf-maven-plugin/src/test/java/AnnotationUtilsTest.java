import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.Parser;
import no.hvl.annotations.CopyOption;
import no.hvl.exceptions.MissingAnnotationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;


class AnnotationUtilsTest {

    Parser parser;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
    }

    @Test
    void testGettingCopyOptionFromNormalImplementAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        CopyOption copyOption = getCopyOptionValueInImplementAnnotation(node);
        assertEquals(CopyOption.REMOVE_EVERYTHING, copyOption);
    }

    @Test
    void testGettingCopyOptionFromNodeNotAnnotatedWithImplement() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(MissingAnnotationException.class, () -> getCopyOptionValueInImplementAnnotation(node));
    }

    @Test
    void testGettingAnnotationMemberValueFromNormalImplementAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertDoesNotThrow(() -> getAnnotationMemberValue(node, IMPLEMENT_NAME, IMPLEMENT_NUMBER_NAME));
    }

    @Test
    void testGettingAnnotationMemberValueFromNonExistingAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertThrows(IllegalArgumentException.class,
                () -> getAnnotationMemberValue(node, "", IMPLEMENT_NUMBER_NAME));
    }

    @Test
    void testGettingAnnotationMemberValueFromNonExistingAnnotationMember() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertThrows(IllegalArgumentException.class,
                () -> getAnnotationMemberValue(node, IMPLEMENT_NAME, ""));
    }

    @Test
    void testGettingAnnotationMemberValueFromSingleValueAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(IllegalArgumentException.class,
                () -> getAnnotationMemberValue(node, TestIdAnnotationName, IMPLEMENT_NUMBER_NAME));
    }

    @Test
    void testGettingNumberValueFromNormalImplementAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertNumberArraysAreEqual(new int[]{1, 2}, getNumberValueInImplementAnnotation(node));
    }

    private void assertNumberArraysAreEqual(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for(int i = 0; i < expected.length; i++){
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void testGettingNumberValueFromNonImplementAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(MissingAnnotationException.class, () -> getNumberValueInImplementAnnotation(node));
    }

    @Test
    void testGettingReplacementIdValueFromImplementAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        assertEquals("2", getReplacementIdInImplementAnnotation(node));
    }

    @Test
    void testGettingReplacementIdValueFromNonImplementAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(MissingAnnotationException.class, () -> getReplacementIdInImplementAnnotation(node));
    }

    @Test
    void testGettingReplacementIdValueFromImplementAnnotationWithoutReplacementId() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertThrows(IllegalArgumentException.class, () -> getReplacementIdInImplementAnnotation(node));

    }
}

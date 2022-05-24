import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.Parser;
import no.hvl.annotations.CopyOption;
import no.hvl.exceptions.MissingAnnotationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
                () -> getAnnotationMemberValue(node, TEST_ID_ANNOTATION_NAME, IMPLEMENT_NUMBER_NAME));
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
        assertEquals("1", getReplacementIdInImplementAnnotation(node));
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

    @Test
    void testFilteringOutNoAnnotationImports() throws IOException {
        CompilationUnit file = StaticJavaParser.parse(Path.of("src/test/java/examples/TODO.java"));
        List<ImportDeclaration> imports = file.getImports();
        assertEquals(imports, getNewListWithoutAnnotationImports(imports));
    }

    @Test
    void testFilteringOutRegularAnnotationImport() throws IOException {
        CompilationUnit file = StaticJavaParser.parse(Path.of("src/test/java/examples/ReplacementMethods.java"));
        List<ImportDeclaration> imports = getNewListWithoutAnnotationImports(file.getImports());
        assertTrue(isEmptyOrOnlyNonAnnotationImports(imports));
    }

    private boolean isEmptyOrOnlyNonAnnotationImports(List<ImportDeclaration> imports) {
        if(!imports.isEmpty()){
            for(ImportDeclaration importDecl : imports){
                if(!isNonAnnotationImportDeclaration(importDecl)){
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    void testFilteringOutStarAnnotationImport() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(Path.of("src/test/java/examples/Example.java"));
        List<ImportDeclaration> imports = getNewListWithoutAnnotationImports(file.getImports());
        assertTrue(isEmptyOrOnlyNonAnnotationImports(imports));
    }

    @Test
    void testRemovingAnnotationImportsFromFile() throws IOException {
        CompilationUnit file = StaticJavaParser.parse(Path.of("src/test/java/examples/Example.java"));
        removeAnnotationImportsFromFile(file);
        assertTrue(isEmptyOrOnlyNonAnnotationImports(file.getImports()));
    }

    @Test
    void testGettingImplementAnnotationsFromFile() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(
                Path.of("src/test/java/examples/AnnotatedNodesGetterTestClass.java"));
        List<BodyDeclaration<?>> annotatedNodes = getNodesInFileAnnotatedWith(file, IMPLEMENT_NAME);
        assertEquals(2, annotatedNodes.size());
        for(BodyDeclaration<?> annotatedNode : annotatedNodes){
            assertTrue(annotatedNode.isAnnotationPresent(IMPLEMENT_NAME));
        }
    }

    @Test
    void testGettingImplementAnnotationsFromMultipleFiles() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(
                Path.of("src/test/java/examples/AnnotatedNodesGetterTestClass.java"));
        List<CompilationUnit> files = List.of(file, file.clone());
        List<BodyDeclaration<?>> annotatedNodes = getAllNodesInFilesAnnotatedWith(files, IMPLEMENT_NAME);
        assertEquals(4, annotatedNodes.size());
        for(BodyDeclaration<?> annotatedNode : annotatedNodes){
            assertTrue(annotatedNode.isAnnotationPresent(IMPLEMENT_NAME));
        }
    }

    @Test
    void testRemovingAnnotationsFromFile() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(
                Path.of("src/test/java/examples/AnnotatedNodesGetterTestClass.java"));
        removeAnnotationTypeFromFile(file, IMPLEMENT_NAME);
        List<BodyDeclaration<?>> annotatedNodes = getNodesInFileAnnotatedWith(file, IMPLEMENT_NAME);
        assertEquals(0, annotatedNodes.size());
    }

    @Test
    void testRemovingAnnotationDoesNotRemoveAnyOtherAnnotation(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        removeAnnotationTypeFromNode(node, IMPLEMENT_NAME);
        assertTrue(node.isAnnotationPresent(TEST_ID_ANNOTATION_NAME));
    }

    @Test
    void testRemovingMultiValueAnnotationFromNode(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        removeAnnotationTypeFromNode(node, IMPLEMENT_NAME);
        assertFalse(node.isAnnotationPresent(IMPLEMENT_NAME));
    }

    @Test
    void testRemovingSingleValueAnnotationFromNode(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        removeAnnotationTypeFromNode(node, TEST_ID_ANNOTATION_NAME);
        assertFalse(node.isAnnotationPresent(TEST_ID_ANNOTATION_NAME));
    }

}

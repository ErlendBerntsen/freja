import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.annotations.TransformOption;
import no.hvl.exceptions.MissingAnnotationException;
import no.hvl.exceptions.NodeException;
import no.hvl.utilities.AnnotationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;
import testUtils.TestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;


class AnnotationUtilsTest extends ExamplesParser {

    @BeforeEach
    public void setUp() throws IOException {
        init();
    }

    @Test
    void testGettingTransformOptionFromNormalExerciseAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        TransformOption transformOption = getTransformOptionValueInExerciseAnnotation(node);
        assertEquals(TransformOption.REMOVE_EVERYTHING, transformOption);
    }

    @Test
    void testGettingTransformOptionValueWithStaticImportOfTransformOption(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 15);
        TransformOption transformOption = getTransformOptionValueInExerciseAnnotation(node);
        assertEquals(TransformOption.REMOVE_EVERYTHING, transformOption);
    }

    @Test
    void testGettingTransformOptionWithFullPackageName(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 17);
        TransformOption transformOption = getTransformOptionValueInExerciseAnnotation(node);
        assertEquals(TransformOption.REMOVE_EVERYTHING, transformOption);
    }

    @Test
    void testGettingTransformOptionFromNodeNotAnnotatedWithExercise() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(MissingAnnotationException.class, () -> getTransformOptionValueInExerciseAnnotation(node));
    }

    @Test
    void testGettingAnnotationMemberValueFromNormalExerciseAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertDoesNotThrow(() -> getAnnotationMemberValue(node, EXERCISE_NAME, EXERCISE_ID_NAME));
    }

    @Test
    void testGettingAnnotationMemberValueFromNonExistingAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertThrows(NodeException.class,
                () -> getAnnotationMemberValue(node, "", EXERCISE_ID_NAME));
    }

    @Test
    void testGettingAnnotationMemberValueFromNonExistingAnnotationMember() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertThrows(NodeException.class,
                () -> getAnnotationMemberValue(node, EXERCISE_NAME, ""));
    }

    @Test
    void testGettingAnnotationMemberValueFromSingleValueAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(NodeException.class,
                () -> getAnnotationMemberValue(node, TEST_ID_ANNOTATION_NAME, EXERCISE_ID_NAME));
    }

    @Test
    void testGettingIdValueFromNormalExerciseAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertIdArraysAreEqual(new int[]{1, 2}, getIdValueInExerciseAnnotation(node));
    }

    private void assertIdArraysAreEqual(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for(int i = 0; i < expected.length; i++){
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void testGettingIdValueFromNonExerciseAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(MissingAnnotationException.class, () -> getIdValueInExerciseAnnotation(node));
    }

    @Test
    void testGettingReplacementIdValueFromExerciseAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        assertEquals("1", getReplacementIdInExerciseAnnotation(node));
    }

    @Test
    void testGettingReplacementIdValueFromNonExerciseAnnotation() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(MissingAnnotationException.class, () -> getReplacementIdInExerciseAnnotation(node));
    }

    @Test
    void testGettingReplacementIdValueFromExerciseAnnotationWithoutReplacementId() {
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        assertThrows(NodeException.class, () -> getReplacementIdInExerciseAnnotation(node));

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
    void testGettingExerciseAnnotationsFromFile() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(
                Path.of("src/test/java/examples/AnnotatedNodesGetterTestClass.java"));
        List<BodyDeclaration<?>> annotatedNodes = getNodesInFileAnnotatedWith(file, EXERCISE_NAME);
        assertEquals(2, annotatedNodes.size());
        for(BodyDeclaration<?> annotatedNode : annotatedNodes){
            assertTrue(annotatedNode.isAnnotationPresent(EXERCISE_NAME));
        }
    }

    @Test
    void testGettingExerciseAnnotationsFromMultipleFiles() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(
                Path.of("src/test/java/examples/AnnotatedNodesGetterTestClass.java"));
        List<CompilationUnit> files = List.of(file, file.clone());
        List<BodyDeclaration<?>> annotatedNodes = getAllNodesInFilesAnnotatedWith(files, EXERCISE_NAME);
        assertEquals(4, annotatedNodes.size());
        for(BodyDeclaration<?> annotatedNode : annotatedNodes){
            assertTrue(annotatedNode.isAnnotationPresent(EXERCISE_NAME));
        }
    }

    @Test
    void testRemovingAnnotationsFromFile() throws IOException{
        CompilationUnit file = StaticJavaParser.parse(
                Path.of("src/test/java/examples/AnnotatedNodesGetterTestClass.java"));
        removeAnnotationTypeFromFile(file, EXERCISE_NAME);
        List<BodyDeclaration<?>> annotatedNodes = getNodesInFileAnnotatedWith(file, EXERCISE_NAME);
        assertEquals(0, annotatedNodes.size());
    }

    @Test
    void testRemovingAnnotationDoesNotRemoveAnyOtherAnnotation(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        removeAnnotationTypeFromNode(node, EXERCISE_NAME);
        assertTrue(node.isAnnotationPresent(TEST_ID_ANNOTATION_NAME));
    }

    @Test
    void testRemovingMultiValueAnnotationFromNode(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        removeAnnotationTypeFromNode(node, EXERCISE_NAME);
        assertFalse(node.isAnnotationPresent(EXERCISE_NAME));
    }

    @Test
    void testRemovingSingleValueAnnotationFromNode(){
        NodeWithAnnotations<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        removeAnnotationTypeFromNode(node, TEST_ID_ANNOTATION_NAME);
        assertFalse(node.isAnnotationPresent(TEST_ID_ANNOTATION_NAME));
    }



}

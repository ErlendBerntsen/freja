import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.exceptions.NodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class AssignmentBuilderTest {

    private Parser parser;
    private final List<String> filesToRemove = List.of("Assignment3RemoveClass", "Assignment3RemoveEnum",
            "Assignment3RemoveInterface", "Assignment3RemoveAnnotation");

    @BeforeEach
    void setUp() throws IOException {
        parser = new Parser();
    }

    @Test
    void testBuildingSimpleAssignment() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertEquals(1, assignment.getParsedFiles().size());
        assertEquals(1, assignment.getExercises().size());
        assertEquals(1, assignment.getReplacements().size());
    }

    @Test
    void testNodesAnnotatedWithRemoveAreRemovedCorrectly() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment3");
        Assignment assignment = new AssignmentBuilder(parser).build();
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove = getAllNodesInFilesAnnotatedWith(parser.getCompilationUnitCopies(), REMOVE_NAME);
        assertNodesAndFilesAreRemoved(assignment.getSolutionCodeFiles(), nodesAnnotatedWithRemove);
        assertNodesAndFilesAreRemoved(assignment.getStartCodeFiles(), nodesAnnotatedWithRemove);
    }

    private void assertNodesAndFilesAreRemoved(List<CompilationUnit> files, List<BodyDeclaration<?>> nodesAnnotatedWithRemove) {
        int amountOfNodesThatAreNotRemoved = 0;
        for(BodyDeclaration<?> node : nodesAnnotatedWithRemove){
            for(CompilationUnit file : files){
                Node nodeWithoutRemoveAnnotation = (Node) removeAnnotationTypeFromNode(node.clone(), REMOVE_NAME);
                if(file.toString().contains(nodeWithoutRemoveAnnotation.toString())){
                    amountOfNodesThatAreNotRemoved++;
                }
            }
        }
        assertEquals(0, amountOfNodesThatAreNotRemoved);
        assertEquals(1, files.size());
        for(CompilationUnit file : files){
            assertTrue(file.getClassByName("Assignment3Exercises").isPresent());
        }
    }

    @Test
    void testClassesAnnotatedWithRemoveAreAddedToList() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment3");
        Assignment assignment = new AssignmentBuilder(parser).build();
        HashSet<String> fileNamesToRemove = assignment.getFileNamesToRemove();
        assertEquals(4, fileNamesToRemove.size());
        List<String> filesToRemoveWithJavaExtension = new ArrayList<>();
        for(String file : filesToRemove){
            filesToRemoveWithJavaExtension.add(file + ".java");
        }
        for(String fileName : fileNamesToRemove){
            assertTrue(filesToRemoveWithJavaExtension.contains(fileName));
        }
    }

    @Test
    void testOriginalCompilationUnitsDoesNotGetMutated() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        List<CompilationUnit> originalFiles = parser.getCompilationUnitCopies();
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertEquals(originalFiles, assignment.getParsedFiles());
    }

    @Test
    void testStartCodeCompilationUnitsDoesNotGetMutated() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertNotEquals(assignment.getStartCodeFiles(), assignment.getSolutionCodeFiles());
    }

    @Test
    void testAllFrejaInformationIsRemoved() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertFrejaInformationIsRemoved(assignment.getStartCodeFiles());
        assertFrejaInformationIsRemoved(assignment.getSolutionCodeFiles());
    }

    private void assertFrejaInformationIsRemoved(List<CompilationUnit> files){
        assertTrue(getAllNodesInFilesAnnotatedWith(files, REMOVE_NAME).isEmpty());
        assertTrue(getAllNodesInFilesAnnotatedWith(files, REPLACEMENT_CODE_NAME).isEmpty());
        assertTrue(getAllNodesInFilesAnnotatedWith(files, EXERCISE_NAME).isEmpty());
        for(CompilationUnit file : files){
            file.getImports()
                    .forEach(importDeclaration -> assertTrue(isNonAnnotationImportDeclaration(importDeclaration)));
        }
    }

    @Test
    void testBuildingAssigmentWithDuplicateReplacementIds() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment2");
        AssignmentBuilder assignmentBuilder = new AssignmentBuilder(parser);
        assertThrows(NodeException.class, assignmentBuilder::build);
    }

    @Test
    void testRemovingNodeOnlyFromStartCode() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment4");
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove =
                getAllNodesInFilesAnnotatedWith(parser.getCompilationUnitCopies(), REMOVE_NAME);
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertNodesAndFilesAreRemoved(assignment.getSolutionCodeFiles(),1, nodesAnnotatedWithRemove);
        assertNodesAndFilesAreRemoved(assignment.getStartCodeFiles(),0, nodesAnnotatedWithRemove);
    }

    private void assertNodesAndFilesAreRemoved(List<CompilationUnit> files, int amountOfNodesThatShouldNotBeRemoved,
                                               List<BodyDeclaration<?>> nodesAnnotatedWithRemove) {
        int amountOfNodesThatAreNotRemoved = 0;
        for(BodyDeclaration<?> node : nodesAnnotatedWithRemove){
            for(CompilationUnit file : files){
                Node nodeWithoutRemoveAnnotation = (Node) removeAnnotationTypeFromNode(node.clone(), REMOVE_NAME);
                if(file.toString().contains(nodeWithoutRemoveAnnotation.toString())){
                    amountOfNodesThatAreNotRemoved++;
                }
            }
        }
        assertEquals(amountOfNodesThatShouldNotBeRemoved,  amountOfNodesThatAreNotRemoved);
    }

    @Test
    void testRemovingNodeOnlyFromSolution() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment5");
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove =
                getAllNodesInFilesAnnotatedWith(parser.getCompilationUnitCopies(), REMOVE_NAME);
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertNodesAndFilesAreRemoved(assignment.getSolutionCodeFiles(),0, nodesAnnotatedWithRemove);
        assertNodesAndFilesAreRemoved(assignment.getStartCodeFiles(),1, nodesAnnotatedWithRemove);
    }

    @Test
    void testRemovingNodeFromAllProjects() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment6");
        Assignment assignment = new AssignmentBuilder(parser).build();
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove =
                getAllNodesInFilesAnnotatedWith(parser.getCompilationUnitCopies(), REMOVE_NAME);
        assertNodesAndFilesAreRemoved(assignment.getSolutionCodeFiles(),0, nodesAnnotatedWithRemove);
        assertNodesAndFilesAreRemoved(assignment.getStartCodeFiles(),0, nodesAnnotatedWithRemove);
    }


}

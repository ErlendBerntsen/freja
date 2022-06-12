import com.github.javaparser.ast.CompilationUnit;
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

public class AssignmentBuilderTest {

    private Parser parser;
    private final List<String> filesToRemove = List.of("Assignment3RemoveClass", "Assignment3RemoveEnum",
            "Assignment3RemoveInterface", "Assignment3RemoveAnnotation");

    @BeforeEach
    void setUp() throws IOException {
        parser = new Parser();
    }

    //TODO Test proper assignment with multiple files and all copy options
    @Test
    void testBuildingSimpleAssignment() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertEquals(1, assignment.getParsedFiles().size());
        assertEquals(1, assignment.getExercises().size());
        assertEquals(1, assignment.getReplacements().size());
    }

    @Test
    void testNodesAnnotatedWithRemoveAreRemoved() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment3");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertNodesAreRemoved(assignment.getSolutionCodeFiles());
        assertNodesAreRemoved(assignment.getStartCodeFiles());
    }

    private void assertNodesAreRemoved(List<CompilationUnit> files) {
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove = getAllNodesInFilesAnnotatedWith(files, REMOVE_NAME);
        assertTrue(nodesAnnotatedWithRemove.isEmpty());
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
    void testAllPafInformationIsRemoved() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertPafInformationIsRemoved(assignment.getStartCodeFiles());
        assertPafInformationIsRemoved(assignment.getSolutionCodeFiles());
    }

    private void assertPafInformationIsRemoved(List<CompilationUnit> files){
        assertTrue(getAllNodesInFilesAnnotatedWith(files, REMOVE_NAME).isEmpty());
        assertTrue(getAllNodesInFilesAnnotatedWith(files, REPLACEMENT_CODE_NAME).isEmpty());
        assertTrue(getAllNodesInFilesAnnotatedWith(files, IMPLEMENT_NAME).isEmpty());
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


}

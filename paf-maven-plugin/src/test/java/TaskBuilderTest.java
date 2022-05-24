import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.Parser;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.Solution;
import no.hvl.concepts.builders.ReplacementBuilder;
import no.hvl.concepts.builders.SolutionBuilder;
import no.hvl.concepts.builders.TaskBuilder;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.tasks.ReplaceSolutionTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class TaskBuilderTest {
    private Parser parser;
    private HashMap<String,Replacement> replacementMap;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        Replacement replacement = new ReplacementBuilder(node).build();
        replacementMap = new HashMap<>();
        replacementMap.put(replacement.getId(), replacement);
    }

    //TODO
    // Add test for replacement imports?
    //Add tests for build methods for:
    //    REMOVE_EVERYTHING
    //    REMOVE_BODY
    //    REPLACE_BODY
    //    REMOVE_SOLUTION

    @Test
    void testBuildingTask(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        assertEquals("1_2_3_1_", task.getFullNumberAsString());
        assertEquals(CopyOption.REPLACE_SOLUTION, task.getCopyOption());
        assertEquals(node, task.getNode());
    }

    @Test
    void testBuildingReplaceSolutionTask(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        assertTrue(task instanceof ReplaceSolutionTask);
        ReplaceSolutionTask replaceSolutionTask = (ReplaceSolutionTask) task;
        assertEquals(replacementMap.get("1"), replaceSolutionTask.getReplacement());
        BlockStmt nodeBody = getBlockStmtFromBodyDeclaration(node);
        Solution solution = new SolutionBuilder(nodeBody).build();
        assertEquals(solution.getStatementsIncludingSolutionMarkers(), replaceSolutionTask.getSolution().getStatementsIncludingSolutionMarkers());
    }

    @Test
    void testBuildingReplaceSolutionTaskWithoutReplacementId(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 7);
        TaskBuilder taskBuilder = new TaskBuilder(node, new Exercise(), replacementMap);
        assertThrows(IllegalStateException.class, taskBuilder::build);
    }

    @Test
    void testBuildingReplaceSolutionTaskWithNonExistingReplacementId(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 11);
        TaskBuilder taskBuilder = new TaskBuilder(node, new Exercise(), replacementMap);
        assertThrows(IllegalArgumentException.class, taskBuilder::build);
    }

}

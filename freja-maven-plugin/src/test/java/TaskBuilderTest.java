import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.TransformOption;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Solution;
import no.hvl.concepts.builders.ReplacementBuilder;
import no.hvl.concepts.builders.SolutionBuilder;
import no.hvl.concepts.builders.TaskBuilder;
import no.hvl.concepts.tasks.Task;
import no.hvl.concepts.tasks.ReplaceSolutionTask;
import no.hvl.exceptions.NodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;

import java.io.IOException;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class TaskBuilderTest extends ExamplesParser {

    @BeforeEach
    public void setUp() throws IOException {
        init();
    }

    @Test
    void testBuildingTask(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        Task task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        assertEquals("1_2_3_1_", task.getFullIdAsString());
        assertEquals(TransformOption.REPLACE_SOLUTION, task.getTransformOption());
        assertEquals(node, task.getNode());
    }

    @Test
    void testBuildingReplaceSolutionTask(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        Task task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        assertTrue(task instanceof ReplaceSolutionTask);
        ReplaceSolutionTask replaceSolutionTask = (ReplaceSolutionTask) task;
        assertEquals(replacementMap.get("1"), replaceSolutionTask.getReplacement());
        BlockStmt nodeBody = getBlockStmtFromBodyDeclaration(node);
        Solution solution = new SolutionBuilder(nodeBody).build();
        assertEquals(solution.getStatementsIncludingSolutionMarkers(),
                replaceSolutionTask.getSolution().getStatementsIncludingSolutionMarkers());
    }

    @Test
    void testBuildingMethodWithReplaceSolutionTaskWithoutReplacementId(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 36);
        Task task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        assertTrue(task instanceof ReplaceSolutionTask);
        ReplaceSolutionTask replaceSolutionTask = (ReplaceSolutionTask) task;
        assertEquals(ReplacementBuilder.getDefaultReplacement(node), replaceSolutionTask.getReplacement());
        BlockStmt nodeBody = getBlockStmtFromBodyDeclaration(node);
        Solution solution = new SolutionBuilder(nodeBody).build();
        assertEquals(solution.getStatementsIncludingSolutionMarkers(),
                replaceSolutionTask.getSolution().getStatementsIncludingSolutionMarkers());
    }

    @Test
    void testBuildingReplaceSolutionTaskWithNonExistingReplacementId(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 11);
        TaskBuilder taskBuilder = new TaskBuilder(node, new Exercise(), replacementMap);
        assertThrows(NodeException.class, taskBuilder::build);
    }

}

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.builders.ExerciseBuilder;
import no.hvl.concepts.builders.ReplacementBuilder;
import no.hvl.concepts.builders.TaskBuilder;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.exceptions.NoFileFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

public class ExerciseBuilderTest extends ExamplesParser {

    @BeforeEach
    public void setUp() throws IOException {
        init();
    }

    @Test
    void testBuildingExerciseWithNoSubExercises(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 15);
        Exercise exercise = new ExerciseBuilder(node, new ArrayList<>(), replacementMap).build();
        assertEquals(1, exercise.getAmountOfAbstractTasks());
        assertEquals(0, exercise.getAmountOfSubExercises());
        assertTrue(exercise.hasAbstractTasks());
        assertTrue(exercise.getParentExercise().isEmpty());
        assertEquals(findFile(node), exercise.getFile());
        assertEquals("1_", exercise.getFullNumberAsString());
    }

    @Test
    void testBuildingExerciseBuildsTaskCorrectly(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 15);
        Exercise exercise = new ExerciseBuilder(node, new ArrayList<>(), replacementMap).build();
        AbstractTask exerciseTask = exercise.getAbstractTasks().get(0);
        exercise.setAbstractTasks(new ArrayList<>());
        AbstractTask task = new TaskBuilder(node, exercise, replacementMap).build();
        assertEquals(task, exerciseTask);
    }

    @Test
    void testBuildingSubExercise(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 16);
        Exercise exercise = new ExerciseBuilder(node, new ArrayList<>(), replacementMap).build();
        assertEquals("2_1_", exercise.getFullNumberAsString());
        Optional<Exercise> parentExercise = exercise.getParentExercise();
        assertTrue(parentExercise.isPresent());
        assertEquals(2, parentExercise.get().getNumberAmongSiblingExercises());
        assertEquals("2_", parentExercise.get().getFullNumberAsString());
        List<Exercise> siblingExercises = parentExercise.get().getSubExercises();
        assertEquals(1, siblingExercises.size());
        assertEquals(List.of(exercise), siblingExercises);
    }

    @Test
    void testBuildingMultipleSiblingExercises(){
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        List<Exercise> rootExercises = new ArrayList<>();
        BodyDeclaration<?> node = getNodeWithId(files, 18);
        Exercise exercise_3_1 = new ExerciseBuilder(node, rootExercises, replacementMap).build();
        BodyDeclaration<?> node2 = getNodeWithId(parser.getCompilationUnitCopies(), 19);
        Exercise exercise_3_2 = new ExerciseBuilder(node2, rootExercises, replacementMap).build();
        assertEquals(1, rootExercises.size());
        assertTrue(exercise_3_1.getParentExercise().isPresent());
        assertTrue(exercise_3_2.getParentExercise().isPresent());
        assertEquals(rootExercises.get(0), exercise_3_1.getParentExercise().get());
        assertEquals(rootExercises.get(0), exercise_3_2.getParentExercise().get());
        assertEquals(1, exercise_3_1.getNumberAmongSiblingExercises());
        assertEquals(2, exercise_3_2.getNumberAmongSiblingExercises());
        assertEquals("3_1_", exercise_3_1.getFullNumberAsString());
        assertEquals("3_2_", exercise_3_2.getFullNumberAsString());
    }

    @Test
    void testBuildingMultipleTasksForSameExercise(){
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        List<Exercise> rootExercises = new ArrayList<>();
        BodyDeclaration<?> node = getNodeWithId(files, 20);
        Exercise exercise = new ExerciseBuilder(node, rootExercises, replacementMap).build();
        BodyDeclaration<?> node2 = getNodeWithId(parser.getCompilationUnitCopies(), 21);
        Exercise sameExercise = new ExerciseBuilder(node2, rootExercises, replacementMap).build();
        assertEquals(exercise, sameExercise);
        assertEquals(2, exercise.getAmountOfAbstractTasks());
        assertEquals("4_1_", exercise.getAbstractTasks().get(0).getFullNumberAsString());
        assertEquals("4_2_", exercise.getAbstractTasks().get(1).getFullNumberAsString());
    }

    @Test
    void testBuildingExerciseWithoutFile(){
        BodyDeclaration<?> node = StaticJavaParser.parseBodyDeclaration("""
                @Implement(number = {1}, copyOption = REMOVE_EVERYTHING)
                int task1;
                """.indent(4));
        ExerciseBuilder exerciseBuilder = new ExerciseBuilder(node, new ArrayList<>(), replacementMap);
        assertThrows(NoFileFoundException.class, exerciseBuilder::build);
    }
}

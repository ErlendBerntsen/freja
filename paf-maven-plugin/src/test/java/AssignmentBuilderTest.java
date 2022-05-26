import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.exceptions.ExerciseNumberException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentBuilderTest {

    Parser parser;

    @BeforeEach
    void setUp() throws IOException {
        parser = new Parser();
    }

    /*
    Ensure immutiablity between parsedfiles, startcode, and solution code
    Test generator things
     */

    @Test
    void testBuildingSimpleAssignment() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        assertEquals(1, assignment.getParsedFiles().size());
        assertEquals(1, assignment.getExercises().size());
        assertEquals(1, assignment.getReplacements().size());
    }

    @Test
    void testBuildingAssigmentWithDuplicateReplacementIds() throws IOException {
        parser.parseDirectory("src/test/java/examples/assignment2");
        AssignmentBuilder assignmentBuilder = new AssignmentBuilder(parser);
        assertThrows(IllegalStateException.class, assignmentBuilder::build);
    }


}

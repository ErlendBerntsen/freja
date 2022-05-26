import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.exceptions.ExerciseNumberException;
import no.hvl.utilities.GeneralUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static no.hvl.utilities.GeneralUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class GeneralUtilsTest {

    @Test
    void testComparingOneLengthIntegerArrays(){
        int comparison = compareIntegerArrays(new int[]{1}, new int[]{2});
        assertEquals(-1, comparison);
        comparison = compareIntegerArrays(new int[]{1}, new int[]{1});
        assertEquals(0, comparison);
        comparison = compareIntegerArrays(new int[]{2}, new int[]{1});
        assertEquals(1, comparison);
    }

    @Test
    void testComparingTwoLengthIntegerArrays(){
        int comparison = compareIntegerArrays(new int[]{1,2}, new int[]{1,2});
        assertEquals(0, comparison);
        comparison = compareIntegerArrays(new int[]{1,1}, new int[]{1,2});
        assertEquals(-1, comparison);
        comparison = compareIntegerArrays(new int[]{1,2}, new int[]{1,1});
        assertEquals(1, comparison);
    }

    @Test
    void testComparingDifferentLengthArrays(){
        int comparison = compareIntegerArrays(new int[]{1,2}, new int[]{2});
        assertEquals(-1, comparison);
        comparison = compareIntegerArrays(new int[]{1}, new int[]{1,2});
        assertEquals(-1, comparison);
    }

    @Test
    void testComparingEmptyArrays(){
        int comparison = compareIntegerArrays(new int[]{}, new int[]{1});
        assertEquals(-1, comparison);
        comparison = compareIntegerArrays(new int[]{1}, new int[]{});
        assertEquals(1, comparison);
        comparison = compareIntegerArrays(new int[]{}, new int[]{});
        assertEquals(0, comparison);
    }

    @Test
    void testIllegalExerciseNumbers(){

    }


    @Test
    void testBuildingAssigmentWithLegalExerciseNumbers(){
        List<int[]> exerciseNumbers = List.of(new int[]{1}, new int[]{2,1}, new int[]{2,1,1},
                new int[]{2,2}, new int[]{3,1,1});
        assertDoesNotThrow(() -> checkIfExerciseNumbersAreLegal(exerciseNumbers));
    }

    @Test
    void testBuildingAssigmentWithIllegalExerciseNumbers(){
        List<int[]> exerciseNumbers = List.of(new int[]{2});
        assertExceptionIsCorrect(exerciseNumbers, List.of(2), List.of(1));
        exerciseNumbers = List.of(new int[]{1,1,1}, new int[]{1,3});
        assertExceptionIsCorrect(exerciseNumbers, List.of(1,3), List.of(1,2));
        exerciseNumbers = List.of(new int[]{1,1,1}, new int[]{1,1,2});
        assertExceptionIsCorrect(exerciseNumbers, List.of(1,1,2), List.of(1,1,1));
        exerciseNumbers = List.of(new int[]{1,1,1,5});
        assertExceptionIsCorrect(exerciseNumbers, List.of(1,1,1,5), List.of(1,1,1,4));
    }

    private void assertExceptionIsCorrect(List<int[]> exerciseNumbers,
                                          List<Integer> exerciseNumber, List<Integer> requiredNumber){
        try{
            checkIfExerciseNumbersAreLegal(exerciseNumbers);
        }catch (ExerciseNumberException e){
            assertEquals(String.format("The assignment has an exercise with the number %s" +
                    " without having an exercise with the number %s", exerciseNumber, requiredNumber),
                    e.getMessage());
        }
    }

    @Test
    void testBuildingZeroBasedExerciseNumber(){
        List<int[]> exerciseNumbers = List.of(new int[]{0});
        try{
            checkIfExerciseNumbersAreLegal(exerciseNumbers);
        }catch (ExerciseNumberException e){
            assertEquals("The assignment has an exercise with the number [0]." +
                            " Exercise numbers are not zero-based and starts counting at 1.",
                    e.getMessage());
        }
    }

    @Test
    void testBuildingZeroBasedExerciseNumber2(){
        List<int[]> exerciseNumbers = List.of(new int[]{1,0,1});
        try{
            checkIfExerciseNumbersAreLegal(exerciseNumbers);
        }catch (ExerciseNumberException e){
            assertEquals("The assignment has an exercise with the number [1, 0]." +
                            " Exercise numbers are not zero-based and starts counting at 1.",
                    e.getMessage());
        }
    }
}

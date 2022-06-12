import no.hvl.exceptions.ExerciseNumberException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    void testBuildingAssigmentWithLegalExerciseNumbers(){
        List<int[]> exerciseNumbers = List.of(new int[]{1}, new int[]{2,1}, new int[]{2,1,1},
                new int[]{2,2}, new int[]{3,1,1});
        assertDoesNotThrow(() -> checkIfExerciseNumberIsLegal(getLegalNumbers(exerciseNumbers), new int[]{3,1,1}));
    }

    private Set<List<Integer>> getLegalNumbers(List<int[]> exerciseNumbers){
        Set<List<Integer>> legalNumbers = new HashSet<>();
        for(int i = 0; i < exerciseNumbers.size()-1; i++){
            int[] number = exerciseNumbers.get(i);
            checkIfExerciseNumberIsLegal(legalNumbers, number);
        }
        return legalNumbers;
    }

    @Test
    void testBuildingAssigmentWithIllegalExerciseNumbers(){
        List<int[]> exerciseNumbers = List.of(new int[]{2});
        assertExceptionIsCorrect(exerciseNumbers, List.of(2), List.of(1));
        exerciseNumbers = List.of(new int[]{1,1,1}, new int[]{1,3});
        assertExceptionIsCorrect(exerciseNumbers, List.of(1,3), List.of(1,2));
        exerciseNumbers = List.of(new int[]{1,1}, new int[]{1,1,2});
        assertExceptionIsCorrect(exerciseNumbers, List.of(1,1,2), List.of(1,1,1));
        exerciseNumbers = List.of(new int[]{1,1,1,5});
        assertExceptionIsCorrect(exerciseNumbers, List.of(1,1,1,5), List.of(1,1,1,4));
    }

    private void assertExceptionIsCorrect(List<int[]> exerciseNumbers,
                                          List<Integer> exerciseNumber, List<Integer> requiredNumber){
        try{
            Set<List<Integer>> legalNumbers = getLegalNumbers(exerciseNumbers);
            int[] numberToCheck = exerciseNumbers.get(exerciseNumbers.size()-1);
            checkIfExerciseNumberIsLegal(legalNumbers, numberToCheck);
            fail("Exception was not thrown");
        }catch (ExerciseNumberException e){
            assertEquals(String.format("The assignment has an exercise with the number %s" +
                    " without having an exercise with the number %s", exerciseNumber, requiredNumber),
                    e.getMessage());
        }
    }

    @Test
    void testBuildingZeroBasedExerciseNumber(){
        try{
            checkIfExerciseNumberIsLegal(new HashSet<>(), new int[]{0});
            fail("Exception was not thrown");
        }catch (ExerciseNumberException e){
            assertEquals("The assignment has an exercise with the number [0]." +
                            " Exercise numbers are not zero-based and starts counting at 1.",
                    e.getMessage());
        }
    }

    @Test
    void testBuildingZeroBasedExerciseNumber2(){
        try{
            checkIfExerciseNumberIsLegal(new HashSet<>(), new int[]{1,0,1});
            fail("Exception was not thrown");
        }catch (ExerciseNumberException e){
            assertEquals("The assignment has an exercise with the number [1, 0]." +
                            " Exercise numbers are not zero-based and starts counting at 1.",
                    e.getMessage());
        }
    }
}

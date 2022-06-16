import no.hvl.exceptions.ExerciseIdException;
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
    void testBuildingAssigmentWithLegalExerciseIds(){
        List<int[]> exerciseIds = List.of(new int[]{1}, new int[]{2,1}, new int[]{2,1,1},
                new int[]{2,2}, new int[]{3,1,1});
        assertDoesNotThrow(() -> checkIfExerciseIdIsLegal(getLegalIds(exerciseIds), new int[]{3,1,1}));
    }

    private Set<List<Integer>> getLegalIds(List<int[]> exerciseIds){
        Set<List<Integer>> legalIds = new HashSet<>();
        for(int i = 0; i < exerciseIds.size()-1; i++){
            int[] id = exerciseIds.get(i);
            checkIfExerciseIdIsLegal(legalIds, id);
        }
        return legalIds;
    }

    @Test
    void testBuildingAssigmentWithIllegalExerciseIds(){
        List<int[]> exerciseIds = List.of(new int[]{2});
        assertExceptionIsCorrect(exerciseIds, List.of(2), List.of(1));
        exerciseIds = List.of(new int[]{1,1,1}, new int[]{1,3});
        assertExceptionIsCorrect(exerciseIds, List.of(1,3), List.of(1,2));
        exerciseIds = List.of(new int[]{1,1}, new int[]{1,1,2});
        assertExceptionIsCorrect(exerciseIds, List.of(1,1,2), List.of(1,1,1));
        exerciseIds = List.of(new int[]{1,1,1,5});
        assertExceptionIsCorrect(exerciseIds, List.of(1,1,1,5), List.of(1,1,1,4));
    }

    private void assertExceptionIsCorrect(List<int[]> exerciseIds,
                                          List<Integer> exerciseId, List<Integer> requiredId){
        try{
            Set<List<Integer>> legalIds = getLegalIds(exerciseIds);
            int[] IdToCheck = exerciseIds.get(exerciseIds.size()-1);
            checkIfExerciseIdIsLegal(legalIds, IdToCheck);
            fail("Exception was not thrown");
        }catch (ExerciseIdException e){
            assertEquals(String.format("The assignment has an exercise with the id %s" +
                    " without having an exercise with the id %s", exerciseId, requiredId),
                    e.getMessage());
        }
    }

    @Test
    void testBuildingZeroBasedExerciseId(){
        try{
            checkIfExerciseIdIsLegal(new HashSet<>(), new int[]{0});
            fail("Exception was not thrown");
        }catch (ExerciseIdException e){
            assertEquals("The assignment has an exercise with the id [0]." +
                            " Exercise ids are not zero-based and starts counting at 1.",
                    e.getMessage());
        }
    }

    @Test
    void testBuildingZeroBasedExerciseId2(){
        try{
            checkIfExerciseIdIsLegal(new HashSet<>(), new int[]{1,0,1});
            fail("Exception was not thrown");
        }catch (ExerciseIdException e){
            assertEquals("The assignment has an exercise with the id [1, 0]." +
                            " Exercise ids are not zero-based and starts counting at 1.",
                    e.getMessage());
        }
    }
}

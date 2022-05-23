import org.junit.jupiter.api.Test;

import static no.hvl.utilities.GeneralUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

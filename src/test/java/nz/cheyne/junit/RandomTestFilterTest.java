package nz.cheyne.junit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Some basic tests across the limit filter mapping.
 */
class RandomTestFilterTest {

    @Test
    void whenTestLimitIsSevenThereShouldBeSevenTrue() {
        int totalTestCount = 20;
        int testLimit = 7;
        boolean[] mapping = RandomTestFilter.generateTestIncludeMapping(testLimit, totalTestCount);
        int trueCount = 0;
        int falseCount = 0;
        for (int i = 0; i < totalTestCount; i++) {
            if (mapping[i]){
                trueCount+= 1;
            } else {
                falseCount += 1;
            }
        }
        assertEquals(7, trueCount);
        assertEquals(13, falseCount);
        assertEquals(20, mapping.length);
    }

    @Test
    void shouldHandleNegativeTestLimit() {

        int totalTestCount = 20;
        int testLimit = -1;

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> RandomTestFilter.generateTestIncludeMapping(testLimit, totalTestCount));

        assertEquals("Test limit must be positive.", exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception);
    }

    @Test
    void shouldHandleNegativeTestCount() {

        int totalTestCount = -1;
        int testLimit = 20;

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> RandomTestFilter.generateTestIncludeMapping(testLimit, totalTestCount));

        assertEquals("Total test count must be positive.", exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception);
    }

    @Test
    void whenTestLimitIsZeroThereShouldBeNoTrue() {
        int totalTestCount = 20;
        int testLimit = 0;
        boolean[] mapping = RandomTestFilter.generateTestIncludeMapping(testLimit, totalTestCount);
        int trueCount = 0;
        int falseCount = 0;
        for (int i = 0; i < totalTestCount; i++) {
            if (mapping[i]){
                trueCount+= 1;
            } else {
                falseCount += 1;
            }
        }
        assertEquals(0, trueCount);
        assertEquals(20, falseCount);
        assertEquals(20, mapping.length);
    }

    @Test
    void expectAllTrueWhenTestLimitIsEqualToTotalTestCount() {
        int totalTestCount = 50;
        int testLimit = 50;
        boolean[] mapping = RandomTestFilter.generateTestIncludeMapping(testLimit, totalTestCount);
        int trueCount = 0;
        int falseCount = 0;
        for (int i = 0; i < totalTestCount; i++) {
            if (mapping[i]){
                trueCount+= 1;
            } else {
                falseCount += 1;
            }
        }
        assertEquals(50, trueCount);
        assertEquals(0, falseCount);
        assertEquals(50, mapping.length);
    }
}
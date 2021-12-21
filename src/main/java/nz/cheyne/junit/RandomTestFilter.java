package nz.cheyne.junit;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

/**
 * This filter limits the number of tests run.
 */
public class RandomTestFilter implements PostDiscoveryFilter {

    protected static Logger log = LoggerFactory.getLogger(RandomTestFilter.class);

    // The maximum number of tests to run
    private final int testCountLimit;

    // The filter is only enabled if the limit is set
    private final boolean isEnabled;

    // This integer system property is equal to the number of tests to run (the limit).
    private final String TEST_COUNT_LIMIT_PROP = "nz.cheyne.junit.test.limit";

    // The number of tests included
    private int testIndex = 0;
    private boolean[] includeTestMapping;

    /**
     *
     */
    public RandomTestFilter() {
        String limit = System.getProperty(TEST_COUNT_LIMIT_PROP);
        if (limit == null) {
            log.debug("RandomTestFilter is not active. Defaulting to all tests.");
            log.debug("Set the system property {} to configure the filter limit.", TEST_COUNT_LIMIT_PROP);
            testCountLimit = Integer.MAX_VALUE;
            isEnabled = false;
        } else {
            log.debug("RandomTestFilter is active.");
            testCountLimit = Integer.parseInt(limit);
            isEnabled = true;
        }
    }

    @Override
    public FilterResult apply(TestDescriptor object) {
        // Don't filter when a limit is not set.
        if (!isEnabled) {
            return FilterResult.included(null);
        }

        // Count the number of total number of tests and generate a mapping that is later used to filter tests at random.
        if (object.isRoot()) {
            long count = object.getDescendants()
                    .stream().filter(TestDescriptor::isTest)
                    .count();

            log.info("Total test count is: {}.", count);
            log.info("Test limit is: {}.", testCountLimit);

            // Limited to 2 million total tests with current impl
            int totalTestCount = Math.toIntExact(count);
            includeTestMapping = generateTestIncludeMapping(testCountLimit, totalTestCount);
        }

        // We have assumed container the container root was processed first. Based on the JUnit 5 implementation, while
        // there is no guarantee, this assumption should hold. If this assumption ever breaks, includeTestMapping would
        // not be set at this point and a different implementation required.
        if (object.isTest()) {
            FilterResult result;
            if (includeTestMapping[testIndex]) {
                result = FilterResult.included(null);
            } else {
                result = FilterResult.excluded("Maximum number of tests reached.");
            }
            testIndex += 1;
            return result;
        } else {
            return FilterResult.included(null);
        }
    }

    /**
     * Creates a shuffled true/false mapping used to filter the tests to run.
     *
     * @param testCountLimit the number tests to run (number o true values)
     * @param totalTestCount the total number of tests
     * @return a shuffled array of true/false values
     */
    protected static boolean[] generateTestIncludeMapping(int testCountLimit, int totalTestCount) {
        if (testCountLimit < 0) {
            throw new IllegalArgumentException("Test limit must be positive.");
        }
        if (totalTestCount < 0) {
            throw new IllegalArgumentException("Total test count must be positive.");
        }

        if (testCountLimit > totalTestCount) {
            testCountLimit = totalTestCount;
            log.info("Test limit is greater than total test count.");
        }

        // Include testCountLimit tests
        boolean[] includeMapping = new boolean[totalTestCount];
        Arrays.fill(includeMapping, 0, testCountLimit, true);

        if (testCountLimit == totalTestCount) {
            log.info("No test limit filtering required.");
            // Every test should be included - nothing to do
        } else {
            // Fill the remained with false
            Arrays.fill(includeMapping, testCountLimit, includeMapping.length, false);

            // Shuffle
            Random rand = new Random();
            for (int i = 0; i < includeMapping.length; i++) {
                int randomIndexToSwap = rand.nextInt(includeMapping.length);
                boolean temp = includeMapping[randomIndexToSwap];
                includeMapping[randomIndexToSwap] = includeMapping[i];
                includeMapping[i] = temp;
            }
        }
        return includeMapping;
    }
}

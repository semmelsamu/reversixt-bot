package test;

import util.Logger;
import util.TestLogger;

public class Test {

    public static void main(String[] args) {
        testAll();
    }

    public static void testAll() {

        Logger.get().priority = 5;

        int failedTests = 0;

        failedTests += MapReadTest.test();
        failedTests += CountTilesTest.test();
        failedTests += Exercise2Test.test();

        if(failedTests > 0)
            TestLogger.get().fatal("Failed tests: " + failedTests);
        else TestLogger.get().log("All tests passed");

    }
}

package test;

import util.Logger;

public class Test {

    public static void main(String[] args) {
        testAll();
    }

    public static void testAll() {

        //Logger.PRIORITY = 5;
        //Logger.NAME = "TEST_ALL";
        Logger.get().log("Testing all");

        int failedTests = 0;

        failedTests += MapReadTest.test();
        failedTests += CountTilesTest.test();
        failedTests += Exercise2Test.test();

        if(failedTests > 0)
            Logger.get().fatal("Failed tests: " + failedTests);
        else Logger.get().log("All tests passed");

    }
}

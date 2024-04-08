package test;

import util.Logger;

public class Test {

    public static void main(String[] args) {
        testAll();
    }

    public static void testAll() {

        Logger.PRIORITY = 7;
        Logger.NAME = "TEST_ALL";
        Logger.log("Testing all", 5);

        int failedTests = 0;

        failedTests += MapReadTest.test();
        failedTests += CountTilesTest.test();
        failedTests += Exercise2Test.test();

        if(failedTests > 0)
            Logger.fatal("Failed tests: " + failedTests, 5);
        else Logger.log("All tests passed", 5);

    }
}

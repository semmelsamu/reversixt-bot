package test;

import util.Logger;

public class Test {

    public static void main(String[] args) {
        testAll();
    }
    public static void testAll() {

        Logger.DEBUG = true;
        Logger.log("Testing all");
        Logger.ON = false;

        int failedTests = 0;
        failedTests += MapReadTest.testAll();

        Logger.ON = true;
        Logger.log("Failed tests: " + failedTests);
    }
}

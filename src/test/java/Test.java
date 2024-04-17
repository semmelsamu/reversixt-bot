import tests.MapReadingTest;
import tests.MoveExecutionTest;
import util.Logger;
import util.TestLogger;

public class Test {

    public static void main(String[] args) {

        Logger.get().priority = 5;
        TestLogger.get().priority = 2;

        (new MapReadingTest()).testAllMaps();
        (new MoveExecutionTest()).testEveryMove();
    }

}

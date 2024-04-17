import tests.MapReadingTest;
import tests.MoveExecutionTest;

public class Test {

    public static void main(String[] args) {
        (new MapReadingTest()).testAllMaps();
        (new MoveExecutionTest()).testEveryMove();
    }

}

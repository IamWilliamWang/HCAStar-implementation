package comp2019_Assignment1;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PathFinderTest {

    /* each test shall complete in less than 3 seconds */
    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(3, TimeUnit.SECONDS)); // requires JUnit >= 4.12


    @Test
    public void test01_Q1Example() {
        RectangularMap map = RectangularMapParser.fromFile("resources/terrain01.txt");
        String correctPath = "(5,7)(6,7)(6,8)(6,9)(5,9)(4,9)(3,9)(2,9)(2,8)(2,7)(1,7)(0,7)(0,6)(0,5)(0,4)(0,3)(0,2)(0,1)(0,0)";
        PathFinder tester = new PathFinder(map, new Location(5, 7), new Location(0, 0));
        Path calculatedPath = tester.findPath();

        assertNotNull("Solution must exist", calculatedPath);
        assertEquals("Path incorrect", correctPath, calculatedPath.toLocationsString());
        assertEquals("Path cost incorrect", 18, calculatedPath.getCost());
    }

    @Test
    public void test02_ImpossibleStart() {
        RectangularMap map = RectangularMapParser.fromFile("resources/terrain01.txt");
        ((ArrayMap) map).setValueAt(5,7,1);
        PathFinder tester = new PathFinder(map, new Location(5, 7), new Location(0, 0));
        Path calculatedPath = tester.findPath();

        assertNull("Solution must not exist", calculatedPath);
    }

    @Test
    public void test03_ImpossibleGoal() {
        RectangularMap map = RectangularMapParser.fromFile("resources/terrain01.txt");
        ((ArrayMap) map).setValueAt(0,0,1);
        PathFinder tester = new PathFinder(map, new Location(5, 7), new Location(0, 0));
        Path calculatedPath = tester.findPath();

        assertNull("Solution must not exist", calculatedPath);
    }

    @Test
    public void test04_LargeWorld() {
        RectangularMap map = RectangularMapParser.fromFile("resources/terrain02.txt");
        Location start = new Location(100,100);
        Location goal = new Location(10, 10);
        PathFinder tester = new PathFinder(map, start, goal);
        Path calculatedPath = tester.findPath();

        assertNotNull("Solution must exist", calculatedPath);
        assertEquals("Path cost incorrect", 180, calculatedPath.getCost());
        PathTestUtils.assertIsValidAcyclicPathFromTo(calculatedPath, start, goal, map,false);
    }

    @Test
    public void test05_LargeWorldNoSolution() {
        RectangularMap map = RectangularMapParser.fromFile("resources/terrain03.txt");
        Location start = new Location(50,10);
        Location goal = new Location(50, 50);
        PathFinder tester = new PathFinder(map, start, goal);
        Path calculatedPath = tester.findPath();

        assertNull("Solution must not exist", calculatedPath);
    }

}

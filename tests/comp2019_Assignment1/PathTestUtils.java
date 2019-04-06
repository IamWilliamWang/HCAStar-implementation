package comp2019_Assignment1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.lang.Math.abs;
import static org.junit.Assert.*;

public class PathTestUtils {

    static void assertIsValidAcyclicPathFromTo(Path path, Location start, Location end, RectangularMap map, boolean allowWait) {
        assertIsValidPathFromTo(path, start, end, map, allowWait);
        assertNoCycles(path);
    }

    static void assertIsValidPathFromTo(Path path, Location start, Location end, RectangularMap map, boolean allowWait) {
        assertWithinBounds(path,map);
        assertBeginsAt(path, start);
        assertEndsAt(path, end);
        assertLocationsAreAccessible(path,map);
        assertValidPathSteps(path, allowWait);
    }

    static void assertBeginsAt(Path path, Location start) {
        assertEquals("Path does not begin at correct location", path.getLocation(0), start);
    }

    static void assertEndsAt(Path path, Location end) {
        assertEquals("Path does not end at correct location", path.getLocation(path.getLength() - 1), end);
    }

    static void assertValidPathSteps(Path path, boolean allowWait) {
        for(int i=1; i<path.getLength(); i++) {
            assertIsValidStep(path.getLocation(i-1), path.getLocation(i), allowWait);
        }
    }

    static void assertIsValidStep(Location prev, Location next, boolean allowWait) {
        int dr = abs(prev.getRow() - next.getRow());
        int dx = abs(prev.getColumn() - next.getColumn());
        assertTrue("Moved to non-adjacent location",
                (dr==0 && dx==1) || (dr==1 && dx==0) || (allowWait && dr==0 && dx==0));
    }

    static void assertWithinBounds(Path path, RectangularMap map) {
        for (int i = 0; i < path.getLength(); i++) {
            Location loc = path.getLocation(i);
            assertTrue("Path goes out of bounds",
                    loc.getRow() >= 0 && loc.getRow() < map.getRows() &&
                            loc.getColumn() >= 0 && loc.getColumn() < map.getColumns());
        }
    }

    static void assertLocationsAreAccessible(Path path, RectangularMap map) {
        for (int i = 0; i < path.getLength(); i++) {
            Location loc = path.getLocation(i);
            assertEquals("Path traverses an inaccessible location", 0, map.getValueAt(loc));
        }
    }

    static void assertNoCycles(Path path) {
        HashSet<Location> visited = new HashSet<>();
        Location prevLoc = null;
        for(int i=0; i<path.getLength(); i++) {
            Location loc = path.getLocation(i);
            if (prevLoc != null && prevLoc.equals(loc)) continue; // skip over spans of identical locations
            assertFalse("Path is cyclic", visited.contains(loc));
            visited.add(loc);
            prevLoc = loc;
        }
    }

    static void assertValidPaths(List<Path> paths, List<Agent> agents, RectangularMap map) {
        assertNotNull("Solution must exist", paths);
        assertEquals("Number of paths does not match number of agents", agents.size(), paths.size());
        for(int i=0; i<paths.size(); i++) {
            Path path = paths.get(i);
            assertNotNull("Path must exist", path);
            assertTrue("Path cannot be empty", path.getLength()>0);
            Agent agent = agents.get(i);
            assertIsValidPathFromTo(path, agent.getStart(), agent.getGoal(), map, true);
        }
        assertNoCollisions(paths);
    }

    static int getMaxTimeStep(List<Path> paths) {
        int max_time = 0;
        for(Path path: paths) {
            max_time = Math.max(max_time, path.getLength());
        }
        return max_time;
    }

    static void assertNoCollisions(List<Path> paths) {
        Location[] positions = new Location[paths.size()];
        int max_time = getMaxTimeStep(paths);
        for(int t=0; t<max_time; t++) {
            Location[] prev_positions = positions.clone();
            // update current locations of all agents at time t
            for(int agent=0; agent<paths.size(); agent++) {
                Path agentPath = paths.get(agent);
                if (agentPath.getLength()>t) {
                    positions[agent] = agentPath.getLocation(t);
                }
            }
            // verify that there are no collisions
            // check that all locations are unique at each time step
            assertEquals("Agents must not collide", positions.length, new HashSet<>(Arrays.asList(positions)).size());
            // check for head-on-collisions
            if (t>0) {
                for (int agent = 0; agent < paths.size(); agent++) {
                    for (int agent2 = agent + 1; agent2 < paths.size(); agent2++) {
                        boolean collision = prev_positions[agent].equals(positions[agent2]) && prev_positions[agent2].equals(positions[agent]);
                        assertFalse("Agents must not collide", collision);
                    }
                }
            }
        }
    }

    public static void assertCorrectPathCosts(List<Path> paths, int[] correctPathCosts) {
        assertNotNull("Solution must exist", paths);
        assertEquals("Number of paths does not match number of agents", correctPathCosts.length, paths.size());
        for(int i=0; i<correctPathCosts.length; i++) {
            Path path = paths.get(i);
            assertNotNull("Path must exist", path);
            assertTrue("Path cannot be empty", path.getLength()>0);
            assertEquals("Path cost incorrect", correctPathCosts[i], path.getCost());
        }
    }

    public static void assertPathsEqual(List<Path> paths, String[] correctPaths) {
        assertNotNull("Solution must exist", paths);
        assertEquals("Number of paths does not match number of agents", correctPaths.length, paths.size());
        for(int i=0; i<correctPaths.length; i++) {
            Path path = paths.get(i);
            assertNotNull("Path must exist", path);
            assertTrue("Path cannot be empty", path.getLength()>0);
            String correctPath = correctPaths[i];
            if (correctPath != null) {
                assertEquals("Path incorrect", correctPaths[i], path.toLocationsString());
            }
        }
    }
}

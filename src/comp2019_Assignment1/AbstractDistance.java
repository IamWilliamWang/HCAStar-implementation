package comp2019_Assignment1;

/**
 * This class finds the abstract distance from any location to the agent's goal.
 * This distance is used as the heuristic estimate in the HCA* search.
 * The entry point for your code is in method distance(Location).
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS AND VARIABLES.
 * Otherwise, JUnit tests will fail and you will receive no credit for your code.
 * Of course, you can add additional methods and classes in your implementation.
 *
 */
public class AbstractDistance {

    public static final int INFINITY = Integer.MAX_VALUE;

    RectangularMap map;
    Location agentGoal, agentInitialLoc;

    public AbstractDistance(RectangularMap map, Location agentGoal, Location agentInitialLoc) {
        this.map = map;
        this.agentGoal = agentGoal;
        this.agentInitialLoc = agentInitialLoc;
    }

    public int distance(Location loc) {
        // TODO: Question 2:
        //  Implement the Reverse Resumable A* to calculate the heuristic distance from this.agentGoal to loc.
        //  Return INFINITY if there there is no feasible path fro agentGoal to loc.

        //  You may be able to reuse your Question 1 A* implementation here

        // not yet implemented
        throw new UnsupportedOperationException();
    }
}

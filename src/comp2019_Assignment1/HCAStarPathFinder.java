package comp2019_Assignment1;

import java.util.List;

/**
 * This class finds the best path for multiple agents using the Hierarchical Cooperative A* algorithm.
 * The entry point for your code is in method findPaths().
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS AND VARIABLES.
 * Otherwise, JUnit tests will fail and you will receive no credit for your code.
 * Of course, you can add additional methods and classes in your implementation.
 *
 */
public class HCAStarPathFinder {

    private RectangularMap map;   // the map
    private List<Agent> agents;     // the list of agents
    private int maxTimeSteps;     // max time steps to consider in each path

    public HCAStarPathFinder(RectangularMap map, List<Agent> agents, int maxTimeSteps) {
        this.map = map;
        this.agents = agents;
        this.maxTimeSteps = maxTimeSteps;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public RectangularMap getMap() {
        return map;
    }

    public int maxTimeSteps() {
        return maxTimeSteps;
    }

    /* DO NOT CHANGE THE CODE ABOVE */
    /* adding imports and variables is okay. */
  
    /* Question 2
     * add your code below. 
     * you can add extra methods.
     */

   
    public List<Path> findPaths() {
        //
        //TODO:
        // Question2
        // Implement HCA* algorithm and use class AbstractDistance as the heuristic distance estimator
        // You will need to implement the RRA* algorithm in AbstractDistance.

        // not yet implemented
        throw new UnsupportedOperationException();
    }

}

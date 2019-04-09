package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.agentPaths = new ArrayList<>(this.agents.size());
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

    List<AbstractDistance> distanceMaps;
    private boolean[] movedThisRound;
    ArrayList<Path> agentPaths;
    
    public List<Path> findPaths() {
        //
        //TODO:
        // Question2
        // Implement HCA* algorithm and use class AbstractDistance as the heuristic distance estimator
        // You will need to implement the RRA* algorithm in AbstractDistance.

        // not yet implemented
    	distanceMaps = new LinkedList<AbstractDistance>();
    	int agentCount = this.agents.size();
    	movedThisRound = new boolean[agentCount];
    	for(Agent agent : agents) 
    		distanceMaps.add(new AbstractDistance(map, agent.getGoal(), agent.getStart()));
    	for(int i=0;i<this.agents.size();i++) {
    		agentPaths.add(new Path(this.agents.get(i).getStart()));
    	}
    	this.agents.sort((o1, o2) -> o1.getPriority() - o2.getPriority());
    	
    	while(!allAgentArrived()) {
    		clearMovedThisRound();
    		for(int agentI=0;agentI<this.agents.size();agentI++) {
    			Agent currentAgent = agents.get(agentI);
    			if(movedThisRound[agentI])
    				continue;
    			if(currentAgent.getLocation()==currentAgent.getGoal())
    				continue;
    			Location nextStep = getNextStep(currentAgent);
    			if(moveTo(currentAgent,nextStep,currentAgent.getLocation())==false)
    				waitHere(currentAgent);
    		}
    	}
    	return this.agentPaths;
    }

	private void waitHere(Agent currentAgent) {
		int agentIndex = this.getAgentIndex(currentAgent);
		this.movedThisRound[agentIndex]=true;
		this.agentPaths.get(agentIndex).moveTo(currentAgent.getLocation());
	}

	private Agent getAgentAt(Location location) {
    	List<Agent> findedAgent = this.agents.stream().filter(agent -> agent.getLocation().equals(location)).collect(Collectors.toList());
    	if(findedAgent.isEmpty())
    		return null;
    	return findedAgent.get(0);
    }
    
	private boolean moveTo(Agent currentAgent, Location nextStep, Location curLoc) {
		Agent conflictAgent = getAgentAt(nextStep);
		if(conflictAgent!=null) {
			if(conflictAgent.getPriority()<currentAgent.getPriority())
				return false;
			if(goAway(conflictAgent,curLoc)==false)
				throw new RuntimeException("Somebody cannot move!");
		}
		currentAgent.setLocation(nextStep);
		this.agentPaths.get(this.getAgentIndex(currentAgent)).moveTo(nextStep);
		this.movedThisRound[getAgentIndex(currentAgent)]=true;
		return true;
	}

	private boolean canPass(int row,int col,Location bullyLoc) {
		if(bullyLoc.getRow()==row && bullyLoc.getColumn()==col)
			return false;
		if(row<0||row>=this.map.getRows())
			return false;
		if(col<0||col>=this.map.getColumns())
			return false;
		if(this.map.getValueAt(row, col)==1)
			return false;
		return true;
	}
	
	private boolean goAway(Agent kickedAgent, Location bullyLocation) {
		Location nowLocation = kickedAgent.getLocation();
		if(canPass(nowLocation.getRow()+1, nowLocation.getColumn(), bullyLocation)) { //下
			if(moveTo(kickedAgent,new Location(nowLocation.getRow()+1,nowLocation.getColumn()),nowLocation)) {
				return true;
			}
		} else if(canPass(nowLocation.getRow(), nowLocation.getColumn()+1, bullyLocation)) { //右
			if(moveTo(kickedAgent,new Location(nowLocation.getRow(),nowLocation.getColumn()+1),nowLocation)) {
				return true;
			}
		} else if(canPass(nowLocation.getRow(), nowLocation.getColumn()-1, bullyLocation)) { //左
			if(moveTo(kickedAgent,new Location(nowLocation.getRow(),nowLocation.getColumn()-1),nowLocation)) {
				return true;
			}
		} else if(canPass(nowLocation.getRow()-1, nowLocation.getColumn(), bullyLocation)) { //上
			if(moveTo(kickedAgent,new Location(nowLocation.getRow()-1,nowLocation.getColumn()),nowLocation)) {
				return true;
			}
		}
		return false; //Blocked at a corder or four bullies.
	}

	private int getAgentIndex(Agent agent) {
		for(int i=0;i<this.agents.size();i++)
			if(agent.equals(this.agents.get(i)))
				return i;
		return -1;
	}
	
	private boolean allAgentArrived() {
		for(Agent agent : this.agents) 
			if(agent.getGoal().equals(agent.getLocation()) == false)
				return false;
		return true;
	}
    
	private void clearMovedThisRound() {
		for(int i=0;i<movedThisRound.length;i++)
			this.movedThisRound[i]=false;
	}

	private Location getNextStep(Agent currentAgent) {
		return distanceMaps.get(this.getAgentIndex(currentAgent)).findNextStep(currentAgent.getLocation());
	}
}

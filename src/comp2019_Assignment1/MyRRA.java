package comp2019_Assignment1;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MyRRA {
	LinkedList<Location> openList = new LinkedList<Location>();
    LinkedList<Location> closedList = new LinkedList<Location>();
    RectangularMap map;
    Location agentGoal; // 'G' in paper
    Location agentInit; // 'O' in paper
    
    public MyRRA(Location agentInit, Location agentGoal, RectangularMap map) {
    	this.map = map;
    	InitialiseRRAStar(agentInit, agentGoal);
    }
    
    private void InitialiseRRAStar(Location O, Location G) {
    	this.agentInit = O;
    	this.agentGoal = G;
    	G.setG(0);
    	G.setH(PathFinder.manhattanDistance(G, O));
    	openList.add(G);
    	ResumeRRAStar(O);
    }

	boolean ResumeRRAStar(Location expandedNode) { // 'N' in paper
		while(!openList.isEmpty()) {
			Location currentLocation = openList.pop();
			closedList.add(currentLocation);
			if(currentLocation.equals(expandedNode))
				return true;
			for(Location newLocation : Successors(currentLocation)) { 
				newLocation.setG(currentLocation.getG() + Cost(currentLocation,newLocation));
				newLocation.setH(PathFinder.manhattanDistance(newLocation, this.agentInit));
				if(!openList.contains(newLocation) && !closedList.contains(newLocation)) {
					openList.add(newLocation);
				}
				if(openList.contains(newLocation)) { 
					try {
						Location sameLocationInOpenList = openList.stream().filter(node -> node.equals(newLocation))
								.collect(Collectors.toList()).get(0);
						if(newLocation.getF() < sameLocationInOpenList.getF())
							sameLocationInOpenList.setF(newLocation.getF());
					} catch (Exception e) {
						System.err.println("Unexpected error occurred!");
						return false;
					}
				}
			}
		}
		return false;
	}

	private boolean canPass(Location location) {
		return (map.getValueAt(location)==0);
	}
	
	private List<Location> Successors(Location location) {
		// TODO Auto-generated method stub
		LinkedList<Location> successors = new LinkedList<Location>();
		for(Location loc : map.getNeighbours(location))
			if(canPass(loc))
				successors.add(loc);
		return successors;
	}

	// 不知道cost=1还是要用PathFinder求cost
	private int Cost(Location location1, Location location2) {
		// TODO Auto-generated method stub
		return new PathFinder(map,location1,location2).findPath().getCost();
		//return PathFinder.manhattanDistance(location1, location2);
	}
    
}

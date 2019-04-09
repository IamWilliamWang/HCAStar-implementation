package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class finds the abstract distance from any location to the agent's goal.
 * This distance is used as the heuristic estimate in the HCA* search. The entry
 * point for your code is in method distance(Location).
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS AND VARIABLES. Otherwise,
 * JUnit tests will fail and you will receive no credit for your code. Of
 * course, you can add additional methods and classes in your implementation.
 *
 */
public class AbstractDistance {

	public static final int INFINITY = Integer.MAX_VALUE;

	RectangularMap map;
	Location agentGoal, agentInitialLoc;
	RRAStar myRRA;

	public AbstractDistance(RectangularMap map, Location agentGoal, Location agentInitialLoc) {
		this.map = map;
		this.agentGoal = agentGoal;
		this.agentInitialLoc = agentInitialLoc;
		myRRA = new RRAStar(agentInitialLoc, agentGoal, map); // RRA*算法
	}

	public int distance(Location loc) {
		// TODO: Question 2:
		// Implement the Reverse Resumable A* to calculate the heuristic distance from
		// this.agentGoal to loc.
		// Return INFINITY if there there is no feasible path fro agentGoal to loc.

		// You may be able to reuse your Question 1 A* implementation here
		if (myRRA.closedList.contains(loc))
			return this.findLocationInOpenClosedMap(loc).getG();
		if (myRRA.ResumeRRAStar(loc))
			return this.findLocationInOpenClosedMap(loc).getG();
		return INFINITY;
	}

	// 以下是找openList或closedList中和neededLocation的行、列相同的Location，名为locationInMap
	private Location findLocationInOpenClosedMap(Location neededLocation) {
		Location locationInMap = null;
		Predicate<Location> predicate = location -> location.equals(neededLocation); // 函数的核心判别表达式
		List<Location> locationsInMap = myRRA.openList.stream().filter(predicate).collect(Collectors.toList());
		if (locationsInMap.isEmpty()) { // open没找到，接着在closed里找
			locationsInMap = myRRA.closedList.stream().filter(predicate).collect(Collectors.toList());
			if (!locationsInMap.isEmpty())
				locationInMap = locationsInMap.get(0);
		} else
			locationInMap = locationsInMap.get(0);
		return locationInMap;
	}

	private Location findRoundLocationByG(Location currentLocation, int g) {
		Location locationInMap = null;
		Location culo = currentLocation; //缩写
		Predicate<Location> predicate = eleLoc -> // 找上下左右四个邻接
		(culo.getColumn() == eleLoc.getColumn() && Math.abs(culo.getRow() - eleLoc.getRow()) == 1)
				|| (culo.getRow() == eleLoc.getRow() && Math.abs(culo.getColumn() - eleLoc.getColumn()) == 1);
		Predicate<Location> predicate2 = eleLoc -> eleLoc.getG() == g; // elementLocation.g要等于g
		if(g==-1) //-1表任意值，取消第二条限制
			throw new RuntimeException("Illegeal g!");
		List<Location> locationsInMap = myRRA.openList.stream().filter(predicate).filter(predicate2)
				.collect(Collectors.toList());
		if (locationsInMap.isEmpty()) { // open没找到，接着在closed里找
			locationsInMap = myRRA.closedList.stream().filter(predicate).filter(predicate2)
					.collect(Collectors.toList());
			if (!locationsInMap.isEmpty())
				locationInMap = locationsInMap.get(0);
		} else
			locationInMap = locationsInMap.get(0);
		return locationInMap;
	}

	public Location findNextStep(Location currentLocation) { // 注意这里的location是Agent的location，fgh有可能为空
//		Location locationInMap = findLocationInOpenClosedMap(currentLocation);
//		int nowG = locationInMap == null ? 0 : locationInMap.getG();
		int nowG = this.distance(currentLocation); //使用distance就不用担心为空的情况
		int nextG = nowG - 1;
		return findRoundLocationByG(currentLocation, nextG);
	}

	private class RRAStar {
		LinkedList<Location> openList = new LinkedList<Location>();
		LinkedList<Location> closedList = new LinkedList<Location>();
		RectangularMap map;
		Location agentGoal; // 'G' in paper
		Location agentInit; // 'O' in paper

		public RRAStar(Location agentInit, Location agentGoal, RectangularMap map) {
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
			while (!openList.isEmpty()) {
				Location currentLocation = openList.pop();
				closedList.add(currentLocation);
				if (currentLocation.equals(expandedNode))
					return true;
				for (Location newLocation : Successors(currentLocation)) {
					newLocation.setG(currentLocation.getG() + Cost(currentLocation, newLocation));
					newLocation.setH(PathFinder.manhattanDistance(newLocation, this.agentInit));
					if (!openList.contains(newLocation) && !closedList.contains(newLocation)) {
						openList.add(newLocation);
					}
					if (openList.contains(newLocation)) {
						try {
							Location sameLocationInOpenList = openList.stream().filter(node -> node.equals(newLocation))
									.collect(Collectors.toList()).get(0);
							if (newLocation.getF() < sameLocationInOpenList.getF())
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
			return (map.getValueAt(location) == 0);
		}

		private List<Location> Successors(Location location) {
			// TODO Auto-generated method stub
			LinkedList<Location> successors = new LinkedList<Location>();
			for (Location loc : map.getNeighbours(location))
				if (canPass(loc))
					successors.add(loc);
			return successors;
		}

		// 不知道cost=1还是要用PathFinder求cost
		private int Cost(Location location1, Location location2) {
			// TODO Auto-generated method stub
			return new PathFinder(map, location1, location2).findPath().getCost();
			// return PathFinder.manhattanDistance(location1, location2);
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			int[][] locationGs = new int[this.map.getRows()][this.map.getColumns()];
			for (Location lo : this.openList)
				locationGs[lo.getRow()][lo.getColumn()] = lo.getG();
			for (Location lo : this.closedList)
				locationGs[lo.getRow()][lo.getColumn()] = lo.getG();
			StringBuilder sb = new StringBuilder();
			for (int row = 0; row < locationGs.length; row++) {
				for (int col = 0; col < locationGs[0].length; col++)
					sb.append(locationGs[row][col] + " ");
				sb.append("\n");
			}
			return sb.toString();
		}
	}
}

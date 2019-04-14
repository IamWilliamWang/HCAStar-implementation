package comp2019_Assignment1;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
		if (myRRA.closedList.contains(loc)) // 如果close列表中有，直接返回g
			return this.findLocationInOpenClosedList(loc).getG();
		if (myRRA.resumeRRAStar(loc)) // 如果没有，就继续执行，直到搜索loc完成
			return this.findLocationInOpenClosedList(loc).getG();
		return INFINITY;
	}

	/**
	 * 找openList或closedList中和neededLocation的行、列相同的Location。
	 * 此函数的由来是因为needLocation完全是由Agent.location而来，只储存了row、col而没有f、g、h的信息
	 * 
	 * @param neededLocation 含有坐标信息的Location
	 * @return 含有f、g、h信息的Location
	 */
	private Location findLocationInOpenClosedList(Location neededLocation) {
		Predicate<Location> predicateEqual = location -> location.equals(neededLocation); // 函数的核心判别表达式
		// 这个List里只可能有1个或者0个元素
		List<Location> locationsInList = myRRA.openList.stream().filter(predicateEqual).collect(Collectors.toList());
		if (locationsInList.isEmpty()) { // openList没找到，接着在closed里找
			locationsInList = myRRA.closedList.stream().filter(predicateEqual).collect(Collectors.toList());
			if (!locationsInList.isEmpty())
				return locationsInList.get(0);
		} else // 找到了就返回该Location
			return locationsInList.get(0);
		return null;
	}

	/**
	 * 在currentLocation周边搜索g为g值的Location
	 * 
	 * @param currentLocation
	 * @param g
	 * @return
	 */
	private Location findRoundLocationByG(Location currentLocation, int g) {
		if(currentLocation==null)
			return null;
		Predicate<Location> predicateSquare = // 找上下左右四个邻接
				element -> (currentLocation.getColumn() == element.getColumn()
						&& Math.abs(currentLocation.getRow() - element.getRow()) == 1)
						|| (currentLocation.getRow() == element.getRow()
								&& Math.abs(currentLocation.getColumn() - element.getColumn()) == 1);
		Predicate<Location> predicateG = element -> element.getG() == g; // elementLocation.g要等于g
		List<Location> locationsInList = myRRA.openList.stream().filter(predicateSquare).filter(predicateG)
				.collect(Collectors.toList());
		if (locationsInList.isEmpty()) { // open没找到，接着在closed里找
			locationsInList = myRRA.closedList.stream().filter(predicateSquare).filter(predicateG)
					.collect(Collectors.toList());
			if (!locationsInList.isEmpty())
				return locationsInList.get(0);
		} else
			return locationsInList.get(0);
		return null;
	}

	/**
	 * 根据当前Location寻找下一步离终点更近的地点
	 * 
	 * @param currentLocation
	 * @return
	 */
	public Location findNextStep(Location currentLocation) { // 注意这里的location是Agent的location，fgh有可能为MAX_VALUE
		int nowG = this.distance(currentLocation); // 使用distance就不用担心为空的情况
		int nextG = nowG - 1;
		return findRoundLocationByG(currentLocation, nextG);
	}

	public List<Location> getSuccessors(Location location) {
		return this.myRRA.getSuccessors(location);
	}

	@Override
	public String toString() {
		return this.myRRA.toString();
	}

	/**
	 * 实现了RRA*算法，论文中伪代码的java实现版本
	 * 
	 * @author William
	 *
	 */
	private class RRAStar {
		LinkedList<Location> openList = new LinkedList<Location>();
		LinkedList<Location> closedList = new LinkedList<Location>();
		RectangularMap map;
		Location agentGoal; // 'G' in paper
		Location agentInit; // 'O' in paper

		public RRAStar(Location agentInit, Location agentGoal, RectangularMap map) {
			this.map = map;
			initialiseRRAStar(agentInit, agentGoal);
		}

		/**
		 * 初始化RRA*
		 * 
		 * @param O Agent初始点
		 * @param G Agent目标点
		 */
		private void initialiseRRAStar(Location O, Location G) {
			this.agentInit = O;
			this.agentGoal = G;
			G.setG(0);
			G.setH(PathFinder.manhattanDistance(G, O));
			openList.add(G);
			resumeRRAStar(O);
		}

		/**
		 * 继续RRA*拓展
		 * 
		 * @param expandedNode 需要拓展到的节点
		 * @return
		 */
		boolean resumeRRAStar(Location expandedNode) { // 'N' in paper
			while (!openList.isEmpty()) { // 不为空就循环
				Location currentLocation = openList.pop();
				closedList.add(currentLocation);
				if (currentLocation.equals(expandedNode)) {
					closedList.remove(currentLocation); // 论文中不直接返回会出bug，这里把目标节点重新加入openList是向让目标节点后边的部分可以在下次调用该函数时也能遍历到
					openList.add(currentLocation);
					return true;
				}
				for (Location newLocation : getSuccessors(currentLocation)) {
					newLocation.setG(currentLocation.getG() + getCost(currentLocation, newLocation));
					newLocation.setH(PathFinder.manhattanDistance(newLocation, this.agentInit));
					if (!openList.contains(newLocation) && !closedList.contains(newLocation)) {
						openList.add(newLocation);
					}
					if (openList.contains(newLocation)) {
						try {
							Location sameLocationInOpenList = openList.stream().filter(node -> node.equals(newLocation))
									.collect(Collectors.toList()).get(0);
							if (newLocation.getF() < sameLocationInOpenList.getF()) {
								sameLocationInOpenList.setG(newLocation.getG());
								sameLocationInOpenList.setH(newLocation.getH());
							}
						} catch (Exception e) {
							System.err.println("Unexpected error occurred!");
							return false;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 判断是否可以通过，因为论文中RRA*算法中忽略其他agent，所以只需要判断这里是不是墙
		 * 
		 * @param location
		 * @return
		 */
		private boolean canPass(Location location) {
			return (map.getValueAt(location) == 0);
		}

		/**
		 * 论文中的SUCCESSORS函数
		 * 
		 * @param location
		 * @return
		 */
		private List<Location> getSuccessors(Location location) {
			// TODO Auto-generated method stub
			LinkedList<Location> successors = new LinkedList<Location>();
			for (Location loc : map.getNeighbours(location))
				if (canPass(loc))
					successors.add(loc);
			return successors;
		}

		/**
		 * 求两地之间的cost
		 * 
		 * @param location1
		 * @param location2
		 * @return
		 */
		private int getCost(Location location1, Location location2) {
			// TODO Auto-generated method stub
			return PathFinder.manhattanDistance(location1, location2); // 调用场景适用于两地点挨着，所以一直会是1
			// return new PathFinder(map, location1, location2).findPath().getCost(); // 过于占用内存，浪费时间。
		}

		/**
		 * 在地图上输出所有已经赋值的g
		 */
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

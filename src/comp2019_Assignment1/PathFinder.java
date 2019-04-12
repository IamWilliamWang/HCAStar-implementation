package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class finds the best path from a start location to the goal location
 * given the map. The entry point for your code is in method findPath().
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS AND VARIABLES. Otherwise,
 * JUnit tests will fail and you will receive no credit for your code. Of
 * course, you can add additional methods and classes in your implementation.
 *
 */
public class PathFinder {

	private Location start; // start location
	private Location goal; // goal location
	private RectangularMap map; // the map
	private ArrayList<Location> openList;
	private ArrayList<Location> closedList;

	public PathFinder(RectangularMap map, Location start, Location goal) {
		this.map = map;
		this.start = start;
		this.goal = goal;
	}

	public RectangularMap getMap() {
		return map;
	}

	public Location getStart() {
		return start;
	}

	public Location getGoal() {
		return goal;
	}

	/* DO NOT CHANGE THE CODE ABOVE */
	/* adding imports and variables is okay. */

	/*
	 * Question 1: add your code below. you can add extra methods.
	 */

	public Path findPath() {
		//
		// TODO Question1
		// Implement A* search that finds the best path from start to goal.
		// Return a Path object if a solution was found; return null otherwise.
		// Refer to the assignment specification for details about the desired path.
		if (this.map.getValueAt(start) == 1 || this.map.getValueAt(goal) == 1) // 如果开始或结果是墙壁
			return null;

		Path shortestPath = new Path(this.start); // 记录最短路径
		Location finalLocation = generateLocationTree(); // 找到最终节点
		if (finalLocation == null) // 节点为空说明没找到
			return null;
		for (Location location : getFullPath(finalLocation)) // 根据目标Location生成完整路径，并遍历
			shortestPath.moveTo(location); // 添加到shortestPath
		return shortestPath;
	}

	/**
	 * 根据目标Location生成完整路径
	 * 
	 * @param finalLocation 目标Location
	 * @return 用List储存的路径
	 */
	private List<Location> getFullPath(Location finalLocation) {
		Location current = finalLocation;
		LinkedList<Location> shortestPathList = new LinkedList<>();
		while (current.getFather() != null) { // 从最后的节点遍历到开始节点。开始节点.getFather()为空
			shortestPathList.addFirst(current); // 插入链表最前端
			current = current.getFather();
		}
		return shortestPathList;
	}

	/**
	 * 生成Location树，并对每个节点F进行赋值。
	 * 
	 * @return 如果找到goal，说明成功则返回goal。否则返回null
	 */
	public Location generateLocationTree() {
		Location endLocation = null;// 终点
		openList = new ArrayList<Location>(); // 待处理的节点
		closedList = new ArrayList<Location>(); // 已处理过的节点
		// 把起点放入open列表中
		this.start.setG(0);
		this.start.setH(manhattanDistance(this.start, this.goal));
		openList.add(this.start);
		while (!openList.isEmpty()) { // 如果openList不为空就一直循环
			Location currentLocation = getMinFLocation(openList); // 获取F值最小的Location
			openList.remove(currentLocation);// 从open列表中移除后
			closedList.add(currentLocation);// 加入到close列表中
			if (currentLocation.equals(this.goal)) { // 当前节点是目标节点
				endLocation = currentLocation; // 记录目标节点
				break;
			} else { // 搜索4个方向并进行处理，添加周围四个方向的新节点到open列表
				// 东
				handleChildNode(currentLocation, currentLocation.getRow(), currentLocation.getColumn() + 1, openList,
						closedList);
				// 南
				handleChildNode(currentLocation, currentLocation.getRow() + 1, currentLocation.getColumn(), openList,
						closedList);
				// 西
				handleChildNode(currentLocation, currentLocation.getRow(), currentLocation.getColumn() - 1, openList,
						closedList);
				// 北
				handleChildNode(currentLocation, currentLocation.getRow() - 1, currentLocation.getColumn(), openList,
						closedList);
			}
		}
		return endLocation;
	}

	/**
	 * 处理每个方向的子节点，
	 */
	private void handleChildNode(Location currentLocation, int childRowIndex, int childColIndex,
			ArrayList<Location> openList, ArrayList<Location> closedList) {
		Location child = null; // 子节点
		if (canPass(childRowIndex, childColIndex)) { // 如果该位置能通过
			child = new Location(childRowIndex, childColIndex); // 生成该位置上的新节点
			child.setG(currentLocation.getG() + 1);
			child.setH(manhattanDistance(this.goal, childRowIndex, childColIndex));
			if (!openList.contains(child) && !closedList.contains(child)) { // 如果是个全新的节点（未在open、closed表中出现）
				child.setFather(currentLocation); // 储存父亲节点
				openList.add(child); // 添加至open列表中
			} else { // 不是全新的节点
				Location conflictLocation = this.findLocationInOpenClosedList(child);
				if (conflictLocation == null)
					throw new RuntimeException("Unexpected error occurred.");
				if (conflictLocation.getF() > child.getF()) {
					conflictLocation.setG(child.getG());
					conflictLocation.setH(child.getH());
					conflictLocation.setFather(currentLocation);
				}
			}
		}
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
		List<Location> locationsInList = openList.stream().filter(predicateEqual).collect(Collectors.toList());
		if (locationsInList.isEmpty()) { // openList没找到，接着在close里找
			locationsInList = closedList.stream().filter(predicateEqual).collect(Collectors.toList());
			if (!locationsInList.isEmpty())
				return locationsInList.get(0);
		} else // 找到了就返回该Location
			return locationsInList.get(0);
		return null;
	}

	/**
	 * 获取F值最小的Location
	 * 
	 * @param openList
	 * @return
	 */
	private Location getMinFLocation(ArrayList<Location> openList) {
		return openList.stream().min((o1, o2) -> o1.getF() - o2.getF()).get();
	}

	/**
	 * 判断该方块是否可通行，只判断数组越界
	 */
	private boolean canPass(int row, int col) {
		int rowCount = map.getRows();
		int colCount = map.getColumns();
		// 边界判断
		if (row >= 0 && row < rowCount && col >= 0 && col < colCount) // row、col在合理的范围内
			return map.getValueAt(row, col) != 1; // 1是墙壁
		return false; // 数组越界
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		int[][] locationGs = new int[this.map.getRows()][this.map.getColumns()];
		for (Location lo : this.openList)
			locationGs[lo.getRow()][lo.getColumn()] = lo.getF();
		for (Location lo : this.closedList)
			locationGs[lo.getRow()][lo.getColumn()] = lo.getF();
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < locationGs.length; row++) {
			for (int col = 0; col < locationGs[0].length; col++)
				sb.append(locationGs[row][col] + " ");
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * 求曼哈顿距离
	 */
	public static int manhattanDistance(Location location1, Location location2) {
		return manhattanDistance(location1.getRow(), location1.getColumn(), location2.getRow(), location2.getColumn());
	}

	public static int manhattanDistance(Location location1, int location2Row, int location2Col) {
		return manhattanDistance(location1.getRow(), location1.getColumn(), location2Row, location2Col);
	}

	public static int manhattanDistance(int location1Row, int location1Col, int location2Row, int location2Col) {
		return Math.abs(location1Row - location2Row) + Math.abs(location1Col - location2Col);
	}
}

package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.DefaultEditorKit.CutAction;

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
		try {
			if (this.map.getValueAt(start) == 1 || this.map.getValueAt(goal) == 1) // 非法入口
				return null;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		Path shortestPath = new Path(this.start); //最短路径
		Location finalLocation = findGoalLocation(); //找到最终节点
		if (finalLocation == null) //节点为空说明没找到
			return null;
		for (Location lo : getFullPath(finalLocation)) //根据目标Location生成完整路径，并遍历
			shortestPath.moveTo(lo); //添加到shortestPath
		return shortestPath;
	}
	
	/**
	 * 根据目标Location生成完整路径
	 * @param finalLocation 目标Location
	 * @return 用List储存的路径
	 */
	private List<Location> getFullPath(Location finalLocation) {
		Location locationPointer = finalLocation; 
		LinkedList<Location> shortestPathList = new LinkedList<>();
		while (locationPointer.getFather() != null) {
			shortestPathList.addFirst(locationPointer);
			locationPointer = locationPointer.getFather();
		}
		return shortestPathList;
	}

	/**
	 * 查找路径
	 * @return 如果找到则返回goal，否则返回null
	 */
	public Location findGoalLocation() {
		Location endNode = null;// 终点
		ArrayList<Location> openList = new ArrayList<Location>(); //开放的Location
		ArrayList<Location> closeList = new ArrayList<Location>(); //闭塞的Location
		// 把起点放入open列表中
		Location startNode = this.start;
		openList.add(startNode);
		int tryCount = 0; //储存循环次数，调试用，无实际用途
		while (openList.size() != 0) {
			tryCount++;
			Location currentMinFNode = getMinFLocation(openList); //获取F值最小的Location
			openList.remove(currentMinFNode);// 从open列表中移除
			closeList.add(currentMinFNode);// 加入到close列表中
			if (currentMinFNode.equals(this.goal)) { // 当前节点是目标节点
				endNode = currentMinFNode;
				break;
			} else {// 搜索4个方向并进行处理
				// 东
				handleChildNode(currentMinFNode, currentMinFNode.getRow(), currentMinFNode.getColumn() + 1, openList, closeList);
				// 南
				handleChildNode(currentMinFNode, currentMinFNode.getRow() + 1, currentMinFNode.getColumn(), openList, closeList);
				// 西
				handleChildNode(currentMinFNode, currentMinFNode.getRow(), currentMinFNode.getColumn() - 1, openList, closeList);
				// 北
				handleChildNode(currentMinFNode, currentMinFNode.getRow() - 1, currentMinFNode.getColumn(), openList, closeList);
			}
		}
		return endNode;
	}

	/* 处理每个方向的子节点 */
	private void handleChildNode(Location currentMinFNode, int rowIndex, int colIndex, ArrayList<Location> openList,
			ArrayList<Location> closeList) {
		Location child = null;
		if (pass(rowIndex, colIndex)) { // 能通过
			child = new Location(rowIndex, colIndex); //生成该位置上的新节点
			if (!openList.contains(child) && !closeList.contains(child)) { //如果是个全新的节点
				child.setF(getCurrentF(rowIndex, colIndex)); //将F值赋值
				child.setFather(currentMinFNode); //储存父亲节点
				openList.add(child); //添加到开放列表中
			}
		}
	}
	
	/**
	 * 获取F值最小的Location
	 * @param openList
	 * @return
	 */
	private Location getMinFLocation(ArrayList<Location> openList) {
		return openList.stream().min((o1, o2) -> o1.getF() - o2.getF()).get();
	}

	/**
	 *  判断该方块是否可通行 
	 */
	private boolean pass(int row, int col) {
		int rowCount = map.getRows();
		int colCount = map.getColumns();
		// 边界判断
		if (row >= 0 && row < rowCount && col >= 0 && col < colCount) // 障碍判断
			return map.getValueAt(row, col) != 1; //1是墙壁
		return false;
	}
	
	/**
	 * 获得指定位置节点f值，g、h都使用曼哈顿距离
	 * @param curRow 行号
	 * @param curCol 列号
	 * @return
	 */
	private int getCurrentF(int curRow, int curCol) {
		// 启发函数表达为f(n)=g(n)+h(n)
		int g = Math.abs(this.start.getRow() - curRow) + Math.abs(this.start.getColumn() - curCol);
		int h = Math.abs(this.goal.getRow() - curRow) + Math.abs(this.goal.getColumn() - curCol);
		return g + h;
		//(5,7)(5,6)(6,6)(6,5)(6,4)(5,4)(4,4)(3,4)(2,4)(2,5)(2,6)(2,7)(1,7)(0,7)(0,6)(0,5)(0,4)(0,3)(0,2)(0,1)(0,0)
	}
	
	/* 另一种启发函数，g使用上一节点的F + 1 */
	private int getCurrentF(int curRow, int curCol, Location curMinFNode) {
		int g = curMinFNode.getF()+1;
		int h = Math.abs(this.goal.getRow() - curRow) + Math.abs(this.goal.getColumn() - curCol);
		return g + h;
		//(5,7)(5,6)(6,6)(6,5)(6,4)(5,4)(4,4)(3,4)(2,4)(2,3)(2,2)(3,2)(4,2)(5,2)(5,1)(5,0)(4,0)(3,0)(2,0)(1,0)(0,0)
	}

}

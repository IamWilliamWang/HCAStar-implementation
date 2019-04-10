package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class finds the best path for multiple agents using the Hierarchical
 * Cooperative A* algorithm. The entry point for your code is in method
 * findPaths().
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS AND VARIABLES. Otherwise,
 * JUnit tests will fail and you will receive no credit for your code. Of
 * course, you can add additional methods and classes in your implementation.
 *
 */
public class HCAStarPathFinder {

	private RectangularMap map; // the map
	private List<Agent> agents; // the list of agents
	private int maxTimeSteps; // max time steps to consider in each path

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

	/*
	 * Question 2 add your code below. you can add extra methods.
	 */

	List<AbstractDistance> distanceMaps; //储存几个agent的距终点距离表
	private boolean[] movedThisRound; //各个agent在该回合是否移动
	ArrayList<Path> agentPaths; //储存agent走的路线

	public List<Path> findPaths() {
		//
		// TODO:
		// Question2
		// Implement HCA* algorithm and use class AbstractDistance as the heuristic
		// distance estimator
		// You will need to implement the RRA* algorithm in AbstractDistance.
		int stepCount = 0;
		distanceMaps = new LinkedList<AbstractDistance>(); //初始化
		int agentCount = this.agents.size();
		movedThisRound = new boolean[agentCount]; //初始化boolean数组
		for (Agent agent : agents) //将agent.size()个距离表插入到distanceMaps
			distanceMaps.add(new AbstractDistance(map, agent.getGoal(), agent.getStart()));
		for (int i = 0; i < this.agents.size(); i++) { //初始化agentPaths
			agentPaths.add(new Path(this.agents.get(i).getStart()));
		}
		this.agents.sort((o1, o2) -> o1.getPriority() - o2.getPriority()); //使用优先级sort agents，注意在此题中priority越小越高

		// 如果有agent没到达终点就继续
		while (!allAgentArrived()) {
			clearMovedThisRound();
			stepCount++;
			if(stepCount>this.maxTimeSteps)
				return null;
			// 每个回合这几个agents都要动一下
			for (int agentI = 0; agentI < this.agents.size(); agentI++) {
				Agent currentAgent = agents.get(agentI);
				if (movedThisRound[agentI]) // 这个回合如果动过就跳过（该agent被挤出去过时适用）
					continue;
				if (currentAgent.getLocation() == currentAgent.getGoal()) //到达终点
					continue;
				Location nextStep = getNextStep(currentAgent); //获得下一步的地点
				if (moveTo(currentAgent, nextStep, currentAgent.getLocation()) == false) //如果向新地点移动失败
					waitHere(currentAgent); //原地踏步一次
			}
		}
		return this.agentPaths;
	}

	/**
	 * 原地踏步一次
	 * @param currentAgent
	 */
	private void waitHere(Agent currentAgent) {
		int agentIndex = this.getAgentIndex(currentAgent);
		this.movedThisRound[agentIndex] = true; //这一回合走过了
		this.agentPaths.get(agentIndex).moveTo(currentAgent.getLocation()); //原地踏了一步
	}

	/**
	 * 获得在该Location上的agent
	 * @param location
	 * @return
	 */
	private Agent getAgentAt(Location location) {
		if(location==null)
			return null;
		List<Agent> foundAgent = this.agents.stream().filter(agent -> agent.getLocation().equals(location))
				.collect(Collectors.toList());
		if (foundAgent.isEmpty())
			return null;
		return foundAgent.get(0);
	}

	/**
	 * 该agent由currentLocation移动到nextStep
	 * @param currentAgent 当前agent
	 * @param nextStep 下一步的地点
	 * @param currentLocation 该agent的当前地点
	 * @return 是否移动成功
	 */
	private boolean moveTo(Agent currentAgent, Location nextStep, Location currentLocation) {
		Agent conflictAgent = getAgentAt(nextStep); //获得nextStep上的冲突agent
		if (conflictAgent != null) { //如果有冲突
			if (conflictAgent.getPriority() < currentAgent.getPriority()) //如果该地点优先级更高
				return false; //移动失败
			if (goAway(conflictAgent, currentAgent) == false) //命令让冲突agent走开
				throw new RuntimeException("Somebody cannot move!");
		}
		currentAgent.setLocation(nextStep); //设定location为nextStep
		this.agentPaths.get(this.getAgentIndex(currentAgent)).moveTo(nextStep); //向储存该agent的agentPath里添加nextStep
		this.movedThisRound[getAgentIndex(currentAgent)] = true; //这回合该agent走过了
		return true; //移动成功
	}

	private boolean moveTo(Agent currentAgent, int newRow,int newCol,Location currentLocation) {
		return moveTo(currentAgent,new Location(newRow, newCol),currentLocation);
	}
	
	/**
	 * 判断该地点能不能通行，包括检测agent
	 * @param row
	 * @param col
	 * @param currentAgent
	 * @return
	 */
	private boolean canPass(int row, int col, Agent currentAgent) {
		Agent conflictAgent = this.getAgentAt(new Location(row, col)); //获得冲突agent
		if (conflictAgent != null && conflictAgent.getPriority() < currentAgent.getPriority()) // 有更高级别的agent在这里
			return false;
		// 以下是检查数组越界和撞墙
		if (row < 0 || row >= this.map.getRows())
			return false;
		if (col < 0 || col >= this.map.getColumns())
			return false;
		if (this.map.getValueAt(row, col) == 1)
			return false;
		return true;
	}

	/**
	 * 该agent被迫被更高等级的agent踢出该位置
	 * @param kickedAgent 被踢的agent
	 * @return 是否被踢成功
	 */
	private boolean goAway(Agent kickedAgent, Agent bullyAgent) {
		Location nowLocation = kickedAgent.getLocation();
		if (canPass(nowLocation.getRow() + 1, nowLocation.getColumn(), kickedAgent)) { // 如果下面可以通过
			if (moveTo(kickedAgent, nowLocation.getRow() + 1, nowLocation.getColumn(), nowLocation)) { //向下方移动一格
				return true;
			}
		} else if (canPass(nowLocation.getRow(), nowLocation.getColumn() + 1, kickedAgent)) { // 右
			if (moveTo(kickedAgent, nowLocation.getRow(), nowLocation.getColumn() + 1, nowLocation)) {
				return true;
			}
		} else if (canPass(nowLocation.getRow(), nowLocation.getColumn() - 1, kickedAgent)) { // 左
			if (moveTo(kickedAgent, nowLocation.getRow(), nowLocation.getColumn() - 1, nowLocation)) {
				return true;
			}
		} else if (canPass(nowLocation.getRow() - 1, nowLocation.getColumn(), kickedAgent)) { // 上
			if (moveTo(kickedAgent, nowLocation.getRow() - 1, nowLocation.getColumn(), nowLocation)) {
				return true;
			}
		}
		return false; // Blocked at a corder or surrounded by bullies.
	}

	/**
	 * 获得agent在数组中的索引号
	 * @param agent
	 * @return
	 */
	private int getAgentIndex(Agent agent) {
		for (int i = 0; i < this.agents.size(); i++)
			if (agent.equals(this.agents.get(i)))
				return i;
		return -1;
	}

	/**
	 * 所有agent都已经到达了终点
	 * @return
	 */
	private boolean allAgentArrived() {
		for (Agent agent : this.agents)
			if (agent.getGoal().equals(agent.getLocation()) == false)
				return false;
		return true;
	}

	/**
	 * 清除movedThisRound
	 */
	private void clearMovedThisRound() {
		for (int i = 0; i < movedThisRound.length; i++)
			this.movedThisRound[i] = false;
	}

	/**
	 * 获得下一步的地点
	 * @param currentAgent
	 * @return
	 */
	private Location getNextStep(Agent currentAgent) {
		return distanceMaps.get(this.getAgentIndex(currentAgent)).findNextStep(currentAgent.getLocation()); //调用该agent所在的AbstractDistance内的findNextStep函数
	}
}

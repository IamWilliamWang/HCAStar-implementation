package comp2019_Assignment1;

import java.util.List;

public class Question2 {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java comp2019_Assignment1.Question2 resources/terrainXX.txt resources/agentsXX.txt max_time_steps");
            System.exit(1);
        }

        RectangularMap map = RectangularMapParser.fromFile(args[0]);
        List<Agent> agentList = AgentListParser.fromFile(args[1]);
		int max_time_steps = Integer.parseInt(args[2]);

		HCAStarPathFinder pathFinder = new HCAStarPathFinder(map, agentList, max_time_steps);
        List<Path> agentPaths = pathFinder.findPaths();
        if (agentPaths != null) {
        	print_agents_paths(agentList, agentPaths);
        } else {
            System.out.println("No solution");
        }
   }

	private static void print_agents_paths(List<Agent> agentList, List<Path> paths) {
		assert agentList.size()==paths.size();
		for(int i=0; i<agentList.size(); i++) {
			Path agentPath = paths.get(i);
			String agentName = agentList.get(i).getName();
			System.out.println("Agent "+agentName+": "+agentPath);
		}	
	}

}

package comp2019_Assignment1;

public class Question1 {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java comp2019_Assignment1.Question1 resources/terrainXX.txt StartRow StartCol GoalRow GoalCol");
            System.exit(1);
        }

        RectangularMap map = RectangularMapParser.fromFile(args[0]);

        Location start = new Location(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        Location goal = new Location(Integer.parseInt(args[3]),Integer.parseInt(args[4]));
	
        PathFinder pathFinder = new PathFinder(map, start, goal);
        Path path = pathFinder.findPath();
        if (path != null) {
            System.out.print("Path: ");
            System.out.println(path.toLocationsString());
            System.out.print("Path cost: ");
            System.out.println(path.getCost());
        } else {
            System.out.println("No solution");
        }
   }

}

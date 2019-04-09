package comp2019_Assignment1;

/**
 * This class represents an Agent.
 * Each agent has a unique priority, a start location, and a goal location.
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS.
 * Otherwise, JUnit tests will fail and you will receive no credit for your code.
 * Of course, you can add additional methods and classes in your implementation.
 *
 */
public class Agent {
    private Location start, goal;
    private Location current;
    private int priority;

    public Agent(int priority, Location start, Location goal) {
        this.priority = priority;
        this.start = start;
        this.goal = goal;
        this.current = this.start;
    }

    public int getPriority() {
        return priority;
    }

    public Location getStart() {
        return start;
    }

    public Location getGoal() {
        return goal;
    }

	public String getName() {
		return "Agent " + priority;
	}
	
	public Location getLocation() {
		return current;
	}

	public void setLocation(Location current) {
		this.current = current;
	}
	
    @Override
    public String toString() {
        return getName() + "[start="+start+" goal="+goal+"]";
    }

}


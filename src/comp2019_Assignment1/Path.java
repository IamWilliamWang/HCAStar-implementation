package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a path on a map.
 * A path consists of a sequence of locations and the costs of moving between adjacent locations.
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS.
 * Otherwise, JUnit tests will fail and you will receive no credit for your code.
 * Of course, you can add additional methods and classes in your implementation.
 *
 */
public class Path {

    private ArrayList<Location> locations = new ArrayList<>();

    public Path(Location initialPosition) {
        locations.add(initialPosition);
    }

    public void moveTo(Location loc) {
        locations.add(loc);
    }

    public void appendLocations(List<Location> list) {
        for(Location loc : list)
            moveTo(loc);
    }

    public void appendLocations(Path path) {
        appendLocations(path.locations);
    }

    public int getLength() {
        return locations.size();
    }

    public int getCost() {
        return getLength()-1;
    }

    @Override
    public String toString() {
        return toLocationsString() + " [path cost= " + getCost() + "]";
    }

    public String toLocationsString() {
        StringBuilder repr = new StringBuilder();
        for(Location loc : locations) {
            repr.append(loc);
        }
        return repr.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((locations == null) ? 0 : locations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Path other = (Path) obj;
        if (locations == null) {
            return other.locations == null;
        } else return locations.equals(other.locations);
    }

    /* for testing; do not use */
    Location getLocation(int index) {
        return locations.get(index);
    }
}

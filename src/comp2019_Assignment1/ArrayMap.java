package comp2019_Assignment1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a rectangular map. Each cell holds an integer value.
 */
public class ArrayMap implements RectangularMap {

    private int[][] cells;

    public ArrayMap(int rows, int columns) {
        if (rows <= 0 || columns <= 0) throw new IllegalArgumentException("Rows and columns must be > 0.");
        this.cells = new int[rows][columns];
    }

    @Override
	public int getRows() {
        return cells.length;
    }

    @Override
	public int getColumns() {
        return cells[0].length;
    }

    @Override
	public int getValueAt(int row, int col) {
        return cells[row][col];
    }

    @Override
	public int getValueAt(Location loc) {
        return getValueAt(loc.getRow(), loc.getColumn());
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        for(int r = 0; r < getRows(); r++) {
            for(int c = 0; c < getColumns(); c++) {
                repr.append(String.format("%03d ", getValueAt(r, c)));
            }
            repr.append("\n");
        }
        return repr.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(cells);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArrayMap other = (ArrayMap) obj;
        return Arrays.deepEquals(cells, other.cells);
    }

    @Override
	public Iterable<Location> getNeighbours(Location loc) {
        List<Location> neighbours = new ArrayList<>();
        if (loc.getColumn() > 0) neighbours.add( new Location(loc.getRow(),loc.getColumn()-1) );
        if (loc.getColumn() < getColumns()-1) neighbours.add( new Location(loc.getRow(),loc.getColumn()+1) );
        if (loc.getRow() > 0) neighbours.add( new Location(loc.getRow()-1,loc.getColumn()) );
        if (loc.getRow() < getRows()-1) neighbours.add( new Location(loc.getRow()+1,loc.getColumn()) );
        return neighbours;
    }

    /* for testing; do not use */
	void setValueAt(int row, int col, int value) {
        cells[row][col] = value;
    }

}

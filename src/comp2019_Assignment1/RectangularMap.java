package comp2019_Assignment1;

public interface RectangularMap {

	int getRows();

	int getColumns();

	int getValueAt(int row, int col);

	int getValueAt(Location loc);

	Iterable<Location> getNeighbours(Location loc);

}
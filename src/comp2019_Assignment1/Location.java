package comp2019_Assignment1;

/**
 * This class represents a location on a rectangular map. A location is a pair
 * (row,column). The top left corner of the map is location (0,0); The top right
 * corner is at (0,w-1), where w is the number of columns in the grid.
 *
 * DO NOT MODIFY THE SIGNATURE OF EXISTING METHODS. Otherwise, JUnit tests will
 * fail and you will receive no credit for your code. Of course, you can add
 * additional methods and classes in your implementation.
 *
 */
public class Location {
	private int row, column;
	// 直接赋值f的时候g和h变成MAX_VALUE，这样只能返回f。或者对g和h进行赋值，f自动变为g+h。两套使用方法不交叉，数据不共存
	private int f = Integer.MAX_VALUE; // 储存该节点的f
	private int g = Integer.MAX_VALUE; 
	private int h = Integer.MAX_VALUE;
	private Location father = null; // 储存父亲节点，仅用于Question1

	public Location(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getF() {
		if (this.g == Integer.MAX_VALUE && this.h == Integer.MAX_VALUE)
			return this.f;
		this.f = this.g + this.h;
		return this.f;
	}

	@Deprecated
	public void setF(int f) {
		this.f = f;
		this.g = Integer.MAX_VALUE;
		this.h = Integer.MAX_VALUE;
	}
	
	public Location getFather() {
		return father;
	}

	public void setFather(Location father) {
		this.father = father;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return this == obj || other.column == column && other.row == row;
	}

	@Override
	public String toString() {
		return "(" + row + "," + column + ")";
	}

}

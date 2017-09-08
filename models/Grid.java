package cz3004MDP.models;

public class Grid {

	private boolean visited;
	private boolean obstacle;
	private boolean clearGrid;
	private int column, row;
	private double gridValue;	
	
	// Default Constructor
	public Grid(){
		this.setVisited(false);
		this.setObstacle(false);
		this.setClearGrid(false);
		
		setGridValue(0);
	}
	
	// Constructor
	public Grid(int column, int row){
		this.setColumn(column);
		this.setRow(row);
		
		this.setVisited(false);
		this.setObstacle(false);
		this.setClearGrid(false);
		
		setGridValue(0);
	}

	
	public boolean isVisited() {
		return visited;
	}

	public boolean isObstacle() {
		return obstacle;
	}

	public boolean isClearGrid() {
		return clearGrid;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
	
	public double getGridValue() {
		return gridValue;
	}

	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
		if (obstacle == true){
			setVisited(true);
			setClearGrid(false);
		}
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
		if (obstacle == true){
			setVisited(true);
			setClearGrid(false);
		}
	}

	public void setClearGrid(boolean clearGrid) {
		this.clearGrid = clearGrid;
		if (obstacle == true){
			setVisited(true);
			setObstacle(false);
		}
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setRow(int row) {
		this.row = row;
	}

	

	public void setGridValue(double gridValue) {
		this.gridValue = gridValue;
	}

	public void resetGrid(){
		this.visited = false;
		this.obstacle = false;
		this.clearGrid = false;
		this.gridValue = 0;
	}
}

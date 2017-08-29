package cz3004MDP.models;

public class Grid {

	private boolean explored;
	private boolean obstacle;
	private boolean clearGrid;
	private int x, y;
	private double gridValue;	
	
	// Default Constructor
	public Grid(){
		this.setExplored(false);
		this.setObstacle(false);
		this.setClearGrid(false);
		
		setGridValue(0);
	}
	
	// Constructor
	public Grid(int x, int y){
		this.setX(x);
		this.setY(y);
		
		this.setExplored(false);
		this.setObstacle(false);
		this.setClearGrid(false);
		
		setGridValue(0);
	}

	
	public boolean isExplored() {
		return explored;
	}

	public boolean isObstacle() {
		return obstacle;
	}

	public boolean isClearGrid() {
		return clearGrid;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public double getGridValue() {
		return gridValue;
	}

	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
		if (obstacle == true){
			setExplored(true);
			setClearGrid(false);
		}
	}
	
	public void setExplored(boolean explored) {
		this.explored = explored;
		if (obstacle == true){
			setExplored(true);
			setClearGrid(false);
		}
	}

	public void setClearGrid(boolean clearGrid) {
		this.clearGrid = clearGrid;
		if (obstacle == true){
			setExplored(true);
			setObstacle(false);
		}
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	

	public void setGridValue(double gridValue) {
		this.gridValue = gridValue;
	}

	public void resetGrid(){
		this.explored = false;
		this.obstacle = false;
		this.clearGrid = false;
		this.gridValue = 0;
	}
}

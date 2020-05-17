package cz3004MDP.models;

import java.util.ArrayList;


public class Grid implements Comparable<Grid>, ArenaRobot{

	private boolean obstacle;
	private boolean visited;
	private boolean clearGrid;
	private int column, row;
	private int gridValue;	
	public static final int GRID_MAXCOST  = 99999;
	
	// Default Constructor
	public Grid(){
		this.setVisited(false);
		this.setObstacle(false);
		this.setClearGrid(false);
		
		setGridValue(0);
	}
	
	// Constructor 1
	public Grid(int column, int row){
		this.setColumn(column);
		this.setRow(row);
		
		this.setVisited(false);
		this.setObstacle(false);
		this.setClearGrid(false);
		
		setGridValue(0);
	}
	
	// Constructor 2
	public Grid(int column, int row, int gridValue){
		this.setColumn(column);
		this.setRow(row);
		this.setGridValue(gridValue);
		
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
	
	public int getGridValue() {
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
	}

	public void setClearGrid(boolean clearGrid) {
		this.clearGrid = clearGrid;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setGridValue(int gridValue) {
		this.gridValue = gridValue;
	}

	public void resetGrid(){
		this.visited = false;
		this.obstacle = false;
		this.clearGrid = false;
		this.gridValue = 0;
	}
	
	public int[] getPosition(){
		return new int[]{this.row, this.column};
	}
	
	public int compareGrid(Grid o){
		if(this.gridValue == o.getGridValue())
			return 0;
		else if(this.gridValue > o.getGridValue())
			return 1;
		else
			return -1;
	}
	
	public boolean equalGrid(Grid o){
		if (this.row == o.getRow()){
			if (this.column == o.getColumn())
				return true;
			else
				return false;
		}
		return false;
			
	}
	
	// MODIFY THIS AGAIN
	public boolean gridIsWall(int[] pos){
		return (pos[0] == -1 || pos[0] == ArenaRobot.ROW || pos[1] == -1 || pos[1] == ArenaRobot.COLUMN);
	}
	
	public boolean withinMapArea(int row, int column){
		if (row >=0 && row <= ArenaRobot.ROW-1){
			if (column >=0 && column<=ArenaRobot.COLUMN-1)
				return true;
			else
				return false;
		}
		return false;
	}

	@Override
	public int compareTo(Grid o) {
		// TODO Auto-generated method stub
		if(this.gridValue == o.gridValue)
			return 0;
		else if(this.gridValue > o.gridValue)
			return 1;
		else
			return -1;
	}
	
	public boolean equals(Grid o){
		if(this.row == o.getRow() && this.column == o.getColumn())
			return true;
		else
			return false;
	}

	public static ArrayList<int []> getUnexplorableWalkableNeighbour(int[] curPos, Grid[][] grid) {
		// TODO Auto-generated method stub
		ArrayList<int[]> neighbour = new ArrayList<int[]>();
		if(Robot.isMovable(curPos[0],curPos[1], grid, Robot.NORTH))	//up
			neighbour.add(new int[]{curPos[0]-1, curPos[1]});
		if(Robot.isMovable(curPos[0],curPos[1], grid, Robot.SOUTH))	//bottom
			neighbour.add(new int[]{curPos[0]+1, curPos[1]});
		if(Robot.isMovable(curPos[0],curPos[1], grid, Robot.WEST))	//left
			neighbour.add(new int[]{curPos[0], curPos[1]-1});
		if(Robot.isMovable(curPos[0],curPos[1], grid, Robot.EAST))	//right
			neighbour.add(new int[]{curPos[0], curPos[1]+1});
		return neighbour;	
	}

	public static int[] oneDPosToTwoD(int current) {
		// TODO Auto-generated method stub
		int [] position = new int[2];
		position[0] = current / COLUMN;
		position[0] = current % COLUMN;
		return position;
	}
	
/* CHECK OUT THE LOGIC IF NEEDED
//TODO: Edit Robot.canmovethrough() to the function written by xuhui
		public static ArrayList<int[]> getWalkableNeighbour(int[] curPos, GridCell[][] gridcell){
			ArrayList<int[]> neighbour = new ArrayList<int[]>();
//				int[][] grid = curMap.getGrid();
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.NORTH))	//up
				neighbour.add(new int[]{curPos[0]-1, curPos[1]});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.SOUTH))	//bottom
				neighbour.add(new int[]{curPos[0]+1, curPos[1]});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.WEST))	//left
				neighbour.add(new int[]{curPos[0], curPos[1]-1});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.EAST))	//right
				neighbour.add(new int[]{curPos[0], curPos[1]+1});
			return neighbour;
		}
		


	 */
}
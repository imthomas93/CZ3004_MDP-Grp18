package Model;

import java.util.ArrayList;
import javax.swing.JLabel;


public class Grid extends JLabel implements Comparable<Grid>, RobotArenaProtocol{
	
	private int row, column, cost;
	
	private int[] position = null;
	private int[] gridStatus = new int[2];
	
	// Default Constructor
	public Grid(int row, int column)
	{
		this.row = row;
		this.column = column;
		this.setGridStatus(NOT_VISITED, NO_OBSTACLE);
	}
	
	// Constructor to use when building visited/nonvisited set set
	public Grid(int row, int column, int cost)
	{
		this.cost = cost;
		this.row = row;
		this.column = column;
		this.setGridStatus(NOT_VISITED, NO_OBSTACLE);
	}

	public int[] getGridStatus() {
		return gridStatus;
	}

	public int getRow() {
		return row;
	}
		
	public int getColumn() {
		return column;
	}

	public void setGridStatus(int visitedOrNot, int emptyOrNot) {
		this.gridStatus[0] = visitedOrNot;
		this.gridStatus[1] = emptyOrNot;
	}
	
	public boolean equals(Grid o){
		if(this.row == o.getRow() && this.column == o.getColumn())
			return true;
		else return false;
	}

	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}

	public static ArrayList<int[]> getWalkableNeighbour(int[] curPos, Grid[][] gridcell){
		ArrayList<int[]> neighbour = new ArrayList<int[]>();
		
		// above
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.NORTH))
			neighbour.add(new int[]{curPos[0]-1, curPos[1]});
		
		// below
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.SOUTH))
			neighbour.add(new int[]{curPos[0]+1, curPos[1]});
		
		// left
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.WEST))
			neighbour.add(new int[]{curPos[0], curPos[1]-1});
		
		// right
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.EAST))
			neighbour.add(new int[]{curPos[0], curPos[1]+1});
		
		return neighbour;
	}	
	
	public static ArrayList<int[]> getUnexploreWalkableNeighbour(int[] curPos, Grid[][] gridcell){
		ArrayList<int[]> neighbour = new ArrayList<int[]>();
		// above
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.NORTH))
			neighbour.add(new int[]{curPos[0]-1, curPos[1]});
		
		//below
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.SOUTH))
			neighbour.add(new int[]{curPos[0]+1, curPos[1]});
		
		// left
		if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.WEST))
			neighbour.add(new int[]{curPos[0], curPos[1]-1});
		
		// right
			if(Robot.isMovableFP(curPos[0],curPos[1], gridcell, Robot.EAST))
				neighbour.add(new int[]{curPos[0], curPos[1]+1});
			return neighbour;
		}
	
	public static int[] convert1DPositionTo2DPositon(int index){
		int[] result = new int[2];
		
		result[0] = index / RobotArenaProtocol.COLUMN;
		result[1] = index % RobotArenaProtocol.COLUMN;
		return result;
	}

	public int compareTo(Grid o) {
		if(this.cost == o.cost)
			return 0;
		else if(this.cost > o.cost)
			return 1; 
		else return -1;
	}

}

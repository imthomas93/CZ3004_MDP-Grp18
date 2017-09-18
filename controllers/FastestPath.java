package cz3004MDP.controllers;

import java.util.ArrayList;

import cz3004MDP.models.ArenaRobot;
import cz3004MDP.models.Grid;

public class FastestPath implements ArenaRobot {
	
	private Grid[][] grid;
	private int turningCost;
	private int[] startPosition;
	private int[] goalPosition;
	
	private int movementCost;
	private int narrowCost;
	
	public FastestPath(Grid[][] grid, int [] startPositon, int [] goalPosition){
		this.setGrid(grid);
		this.setStartPosition(startPositon);
		this.setGoalPosition(goalPosition);
	}

	public Grid[][] getGrid() {
		return grid;
	}

	public void setGrid(Grid[][] grid) {
		this.grid = grid;
	}

	public int getTurningCost() {
		return turningCost;
	}

	public void setTurningCost(int turningCost) {
		this.turningCost = turningCost;
	}

	public int[] getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int[] startPosition) {
		this.startPosition = startPosition;
	}

	public int[] getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(int[] goalPosition) {
		this.goalPosition = goalPosition;
	}

	
	public int getMovementCost() {
		return movementCost;
	}

	
	public void setMovementCost(int movementCost) {
		this.movementCost = movementCost;
	}

	public int getNarrowCost() {
		return narrowCost;
	}

	public void setNarrowCost(int narrowCost) {
		this.narrowCost = narrowCost;
	}

	// get fastest path from start to goal zone
	public int [] execute(){
		narrowCost = 1;
		turningCost = 2;
		int[] path = dijkstra(startPosition);
		return path;
	}
	
	// invert a neg int to pos int
	private int abs(int index){
		if (index > 0)
			return index;
		else
			return -index;
	}

	//if 2 cells are diagonal to each other. Used to detect a TURN movement
	private boolean isDiagonal(int[] p1, int[] p2){
		return (abs(p1[0]-p2[0]) == 1 && abs(p1[1]-p2[1])==1);
	}
 	
	private int[] dijkstra(int[] curPosition) {
		// TODO Auto-generated method stub
		int[][] pi = new int[ROW][COLUMN];
		BinaryHeap<Grid> open = new BinaryHeap<Grid>();
		ArrayList<Grid> expMap = new ArrayList<Grid>();
		int[][] visited = new int[ROW][COLUMN];
		int [] npPos = new int[2];
		movementCost = 0;
	
		// initilise map grid
		for(int i =0; i < ROW; i++){
			for (int j = 0; j < COLUMN; j++){
				visited[i][j] = 0;
				expMap.add(new Grid(i,j, Grid.GRID_MAXCOST));
			}
		}
		
		Grid cur;
		Grid start = expMap.get(curPosition[0]*COLUMN + curPosition[1]);
		Grid goal = expMap.get(goalPosition[0]*COLUMN + goalPosition[1]);
		start.setGridValue(0);
		open.add(start);
		
		pi[start.getRow()][start.getColumn()] = 0;
		
		while(open.peek() != goal){
			// remove current from heap
			cur = open.remove();
			
			visited[cur.getRow()][cur.getColumn()] = 1;
			ArrayList<int[]> neighbours = new ArrayList<int[]>();
			
			neighbours = Grid.getUnexplorableWalkableNeighbour(new int[]{cur.getRow(), cur.getColumn()}, grid);
			int[] curPos = new int[]{cur.getRow(), cur.getColumn()};
			
			for(int i = 0; i < neighbours.size(); i++){
				npPos = neighbours.get(i);
				Grid nb = expMap.get(npPos[0]*COLUMN + npPos[1]);
				movementCost = cur.getGridValue() + calculateMovementCost(curPos, npPos, pi);
				
				if (visited[nb.getRow()][nb.getColumn()] != 1 && nb.getGridValue() > movementCost){
					if(open.contains(nb))
						open.remove(nb);
					nb.setGridValue(movementCost);
					pi[nb.getRow()][nb.getColumn()] = cur.getRow() * COLUMN + cur.getColumn();
					open.add(nb);
				}
			}
			if (open.isEmpty()){
				System.out.println("path to grid not available");
				return null;
			}
		}
		int [] ipath = new int[ROW*COLUMN];
		cur = goal;
		int j = 0;
		
		while(!cur.equals(start)){
			ipath[j++] = cur.getRow()*COLUMN + cur.getColumn();
			cur = expMap.get(pi[cur.getRow()][cur.getColumn()]);
		}
		ipath[j] = start.getRow()*COLUMN + start.getColumn();
		
		int ps = j+1;
		int [] rpath = new int[ps];
		
		// map to return path and return result
		for(int i = 0; i < ps; i++)
			rpath[i] = ipath[ps-i-1];
		
		return rpath;
		
	}

	private int calculateMovementCost(int[] curPos, int[] nbPos, int[][] pi) {
		// TODO Auto-generated method stub
		int cost = 1;
		int pre;
		int [] prePos = new int[2];
		
		if((pre = pi[curPos[0]][curPos[1]]) == 0) 
			return cost;
		
		prePos[0] = pre/ COLUMN; 
		prePos[1] = pre % COLUMN;
		
		// check if its a turn
		if(isDiagonal(prePos, nbPos))
			cost = turningCost;
		else{
			//get direction and check grid
			if(nbPos[0] == curPos[0])
			{
				if(nbPos[1] > curPos[1] && curPos[0] > 1 && curPos[0] < 18)	//EAST
				{
					if((grid[curPos[0]-2][curPos[1]+1].isObstacle() || curPos[0] == 1) &&
							(grid[curPos[0]+2][curPos[1]+1].isObstacle() || curPos[0] == 18))
						cost = narrowCost;
							
				}
				else if(nbPos[1] < curPos[1] && curPos[0] > 1 && curPos[0] < 18)	//WEST
				{
					if((grid[curPos[0]-2][curPos[1]-1].isObstacle() || curPos[0] == 1) &&
							(grid[curPos[0]+2][curPos[1]-1].isObstacle() || curPos[0] == 18))
						cost = narrowCost;
				}
			}
			else if(nbPos[1] == curPos[1])
			{
				if(nbPos[0] < curPos[0] && curPos[1] > 1 && curPos[1] < 13)	//NORTH
				{
					if((grid[curPos[0]-1][curPos[1]-2].isObstacle() || curPos[1] == 1) &&
							grid[curPos[0]-1][curPos[1]+2].isObstacle() || curPos[1] == 13)
						cost = narrowCost;
				}
				else if(nbPos[0] > curPos[0] && curPos[1] > 1 && curPos[1] < 13)	//SOUTH
				{
					if((grid[curPos[0]+1][curPos[1]-2].isObstacle() || curPos[1] == 1) &&
							(grid[curPos[0]+1][curPos[1]+2].isObstacle() || curPos[1] ==13))
						cost = narrowCost;
				}
			}			
		}
		return cost;
	}
	
	//This is for cleanup exploration
	public int[][] executeCost(){
		narrowCost = 1;
		turningCost = 2;
		int[] path1 = dijkstra(startPosition);
		return new int[][]{path1, new int[]{movementCost}};
	}
}

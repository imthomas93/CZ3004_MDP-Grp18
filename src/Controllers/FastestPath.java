package Controllers;

import java.util.ArrayList;

import Model.RobotArenaProtocol;
import Model.Grid;
import Model.Robot;

public class FastestPath {
	private Grid[][] grid;
	private int turnCost;
	private int[] startPos;
	private int[] goalPos;
	private int movementCost;
	private int narrowCost;
	
	public FastestPath(Grid[][] grid, int[] startPos, int[] goalPos){
		this.grid = grid;
		this.startPos = startPos;
		this.goalPos = goalPos;
	}

	//this is for fastest path from start to goal
	public int[] execute(){
		narrowCost = 2;
		turnCost = 2;
		int[] path = dijkstra(startPos);
		return path;
	}
	
	//This is for cleanup exploration
	public int[][] executeCost(){
		narrowCost = 1;
		turnCost = 2;
		int[] path1 = dijkstra(startPos);
		return new int[][]{path1, new int[]{movementCost}};
	}
	
	private int abs(int k){
		if(k>=0 ) return k;
		else	return -k;
	}
	
	private int[] dijkstra(int[] curStart){
		int[][] pi = new int[RobotArenaProtocol.ROW][RobotArenaProtocol.COLUMN];
		BinaryHeap<Grid> open = new BinaryHeap<Grid>();
		ArrayList<Grid> expMap = new ArrayList<Grid>();
		int[][] visited = new int[RobotArenaProtocol.ROW][RobotArenaProtocol.COLUMN];
		int[] nbPos = new int[2];
		movementCost = 0;
		
		//initialize map grid
		for(int i=0; i<RobotArenaProtocol.ROW; i++){
			for(int j=0; j<RobotArenaProtocol.COLUMN; j++){
				visited[i][j] = 0; 
				expMap.add(new Grid(i, j, RobotArenaProtocol.MAX_COST ));
			}
		}
				
		Grid current;
		Grid start  = expMap.get(curStart[0]*RobotArenaProtocol.COLUMN + curStart[1]);
		Grid goal  = expMap.get(goalPos[0]*RobotArenaProtocol.COLUMN + goalPos[1]);
		start.setCost(0);
		open.add(start);
		pi[start.getRow()][start.getColumn()] = 0;
		while(open.peek() != goal){	
			current = open.remove();
			visited[current.getRow()][current.getColumn()] = 1;
			ArrayList<int[]> neighbours = new ArrayList<int[]>();
			neighbours = Grid.getUnexploreWalkableNeighbour(new int[]{current.getRow(), current.getColumn()}, grid);
			int[] curPos = new int[]{current.getRow(), current.getColumn()};
			for(int i=0; i<neighbours.size(); i++){
				nbPos = neighbours.get(i);	//not obstacle
				Grid nb = expMap.get(nbPos[0]*RobotArenaProtocol.COLUMN + nbPos[1]);
				movementCost = current.getCost() + calculateMovementCost(curPos, nbPos, pi);
				if(visited[nb.getRow()][nb.getColumn()] != 1 && nb.getCost() > movementCost){
					if(open.contains(nb))	open.remove(nb);
					nb.setCost(movementCost);
					pi[nb.getRow()][nb.getColumn()] = current.getRow() * RobotArenaProtocol.COLUMN + current.getColumn();
					open.add(nb);
				}  
			}
			if(open.isEmpty()==true){
				System.out.println("Path to gridcell not avaiable");
				return null;
			}
		}
		
		int[] ipath = new int[RobotArenaProtocol.ROW*RobotArenaProtocol.COLUMN];
		current = goal;
		int j=0;
		while(!current.equals(start)){
			ipath[j++] = current.getRow()*RobotArenaProtocol.COLUMN + current.getColumn();
			current = expMap.get(pi[current.getRow()][current.getColumn()]);
		}
		ipath[j] = start.getRow()*RobotArenaProtocol.COLUMN + start.getColumn();
		
		//reverse the path (goal -> start to start -> goal
		int ps = j+1;
		int[] rpath = new int[ps];
		for(int i=0; i<ps; i++)
			rpath[i] = ipath[ps-i-1];
		
		return rpath;
	}
	
	//check if 2 cells are diagonal to each other. Used to detect a TURN movement
	private boolean isDiagonal(int[] p1, int[] p2){
		return (abs(p1[0]-p2[0]) == 1 && abs(p1[1]-p2[1])==1);
	}
	
	//calculate movement cost depending on what the movement is (go straight, turn, or go straight through a narrow path)
	private int calculateMovementCost(int[] curPos, int[] nbPos, int[][] pi){
		int cost = 1;
		int pre;
		int[] prePos = new int[2];
		
		if((pre = pi[curPos[0]][curPos[1]]) == 0) return cost;
		prePos[0] = pre/RobotArenaProtocol.COLUMN; prePos[1] = pre%RobotArenaProtocol.COLUMN;
		
		if(isDiagonal(prePos, nbPos))	//check if it is a turn
			cost = turnCost;
		else						//check if it is a narrow
		{
			//get direction and check grid
			if(nbPos[0] == curPos[0])
			{
				if(nbPos[1] > curPos[1] && curPos[0] > 1 && curPos[0] < 18)	//EAST
				{
					if((grid[curPos[0]-2][curPos[1]+1].getGridStatus()[1] == Grid.OBSTACLE || curPos[0] == 1) &&
							(grid[curPos[0]+2][curPos[1]+1].getGridStatus()[1] == Grid.OBSTACLE || curPos[0] == 18))
						cost = narrowCost;
							
				}
				else if(nbPos[1] < curPos[1] && curPos[0] > 1 && curPos[0] < 18)	//WEST
				{
					if((grid[curPos[0]-2][curPos[1]-1].getGridStatus()[1] == Grid.OBSTACLE || curPos[0] == 1) &&
							(grid[curPos[0]+2][curPos[1]-1].getGridStatus()[1] == Grid.OBSTACLE || curPos[0] == 18))
						cost = narrowCost;
				}
			}
			else if(nbPos[1] == curPos[1])
			{
				if(nbPos[0] < curPos[0] && curPos[1] > 1 && curPos[1] < 13)	//NORTH
				{
					if((grid[curPos[0]-1][curPos[1]-2].getGridStatus()[1] == Grid.OBSTACLE || curPos[1] == 1) &&
							grid[curPos[0]-1][curPos[1]+2].getGridStatus()[1] == Grid.OBSTACLE || curPos[1] == 13)
						cost = narrowCost;
				}
				else if(nbPos[0] > curPos[0] && curPos[1] > 1 && curPos[1] < 13)	//SOUTH
				{
					if((grid[curPos[0]+1][curPos[1]-2].getGridStatus()[1] == Grid.OBSTACLE || curPos[1] == 1) &&
							(grid[curPos[0]+1][curPos[1]+2].getGridStatus()[1] == Grid.OBSTACLE || curPos[1] ==13))
						cost = narrowCost;
				}
			}
		}
		return cost;
	}	
}

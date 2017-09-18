package cz3004MDP.controllers;

import java.util.ArrayList;

import cz3004MDP.models.Arena;
import cz3004MDP.models.Grid;
import cz3004MDP.models.Robot;
import cz3004MDP.services.RpiCommProtocol;
import cz3004MDP.services.Utilities;
import cz3004MDP.models.ArenaRobot;

public class ExplorationManager implements ArenaRobot {

	private Robot robot;
	private Arena arena;
	private Grid[][] grid;
	private Utilities utility;
	
	private boolean isAtGoal = false;
	private boolean isTimetoGoBack = false;
	
	// robot speed
	// CHANGE SPEED BY GETTING FROM VIEW
	private int speed = 1;
	
	// arena exploration percentage
	private int coveragePercentage;
	
	// CHANGE VARIABLE NAME AFT CFM LOGIC
	private boolean timesUp = false;
	private boolean enableCoverageTerminal = false;
	private boolean enableTimerTerminal = false;
	
	public ExplorationManager(Robot robot, Arena arena){
		this.robot = robot;
		this.arena = arena;
		this.grid = arena.getGrid();
		utility = new Utilities();
	}
	
	public void explore(){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				startExploration();
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	private void startExploration() {
		// TODO Auto-generated method stub
		boolean goStraight = false;
		
		do{
			robot.getSensorData(grid);
			arena.updateRobotPos();
			
			int row = robot.getCurrentPos()[0];
			int column = robot.getCurrentPos()[1];
			
			// need to implement logic here
			/*
			 * Logic:
			 * 1) Always move forward
			 * 2) hug left first
			 * 3) if left is block > check forward
			 * 4) if both left and front is blocked, hug right
			 * 5) if all are block, return by 1 position and prevent bot to go go front first
			 */
			if(goStraight){
				robot.goStraight();
				goStraight = false;
			}
			else if(!this.isLeftBlocked()){
				robot.leftTurn();
				goStraight = true;
			}
			else if(!this.isFrontBlocked()){
				robot.goStraight();
				goStraight = false;
			}
			else if (!this.isRightBlocked()){
				robot.rightTurn();
				goStraight = true;
			}
			else{
				// return 1 position
				robot.goBack();
				goStraight = false;
			}
			// display to check
			System.out.println("current row: " + row + "\tcurrent col: "+ column);
			
			// NEED TO CHECK THE PLACE OF GOAL
			// Reached goal zone
			if(robot.getCurrentPos()[0] == 1 && robot.getCurrentPos()[1] == 13){
				isAtGoal = true;
			}
			
			try{
				Thread.sleep(1000/speed);
			}
			catch (InterruptedException ie){
				ie.printStackTrace();
			}
			// UPDATE ROBOT ARENA
			arena.updateRobotPos();
			
			// TODO: update coverage and time here
			
			if(enableCoverageTerminal){
				if (arena.calculateExplorationCoverage() >= coveragePercentage){
					System.out.println("exploration coverage: " + arena.calculateExplorationCoverage());
					System.out.println("coverage percentage: " + coveragePercentage);
					System.out.println("go back");
					timesUp = true;
				}
			}
			
		}
		// stop when robot is back to start zone from goal zone
		while((!(robot.getCurrentPos()[0] == 18 && robot.getCurrentPos()[1] == 1) || isAtGoal!=true) 
				&& isTimetoGoBack ==false && timesUp == false);
		
		if(isTimetoGoBack || timesUp)
			turnBackAndGoBack();
		
		if(isAtGoal){
			cleanExplorationThread();
			robot.calibrateToNorth();
			arena.updateRobotPos();
			System.out.println("exploration done");
			ArrayList<String> result = new ArrayList<String>();
	
			utility.saveArenaToFile(FILENAME2, result);
			
			// TODO: stop timer here?
		}
	}

	private void cleanExplorationThread() {
		// TODO Auto-generated method stub
		boolean result = true;
		
		do{
			ArrayList<int[]> points = new ArrayList<int[]>();
			for(int i = 0; i <ROW;i++){
				for (int j = 0;j<COLUMN;j++){
					if(grid[ROW][COLUMN].isVisited()){
						int[][] temp = pointAccessible(new int[]{ROW, COLUMN});
						for(int k = 0; k < temp.length; k++){
							if(temp[k][0] != 0 && temp[k][1] != 0)
								points.add(temp[k]);
						}
					}
				}
			}
			
			if (points.size() >0)
				// find fastestpath to cell and direction, return lowest cost path
				cleanupExploration(grid, points);
			else{
				System.out.println("fully explored / no path to unexplored area");
				
				if (robot.getCurrentPos()[0] != 18 || robot.getCurrentPos()[1] !=1){
					points.add(new int[]{18,1});
					cleanupExploration(grid,points);
				}
				result = false;
			}
		} while(result);
	}

	private int[][] pointAccessible(int[] points) {
		// TODO Auto-generated method stub
		int counter  = 0;
		int [][] tbExploredPts = new int[24][3];
		int [][] accessiblePts = new int[24][3];
		
		for(int i = -1; i <2;i++){
			// North Point
			if(points[0] > 0){
				if(!grid[points[0]-1][points[1]].isObstacle()){
					tbExploredPts[1+i][0] = points[0]-2;
					tbExploredPts[1+i][1] = points[1]+i;
					tbExploredPts[1+i][2] = Robot.SOUTH;

					tbExploredPts[4+i][0] = points[0]-3;
					tbExploredPts[4+i][1] = points[1]+i;
					tbExploredPts[4+i][2] = Robot.SOUTH;

				}
			}
			// SOUTH POINT
			if(points[0] < 19){
				if(!grid[points[0]-1][points[1]].isObstacle()){
					tbExploredPts[13+i][0] = points[0]+2;
					tbExploredPts[13+i][1] = points[1]+i;
					tbExploredPts[13+i][2] = Robot.NORTH;
					
					tbExploredPts[16+i][0] = points[0]+3;
					tbExploredPts[16+i][1] = points[1]+i;
					tbExploredPts[16+i][2] = Robot.NORTH;

				}
			}
			
			// EAST POINT
			if(points[1] < 14){
				if(!grid[points[0]+1][points[1]].isObstacle()){
					tbExploredPts[7+i][0] = points[0]+i;
					tbExploredPts[7+i][1] = points[1]+2;
					tbExploredPts[7+i][2] = Robot.WEST;

					tbExploredPts[10+i][0] = points[0]-3;
					tbExploredPts[10+i][1] = points[1]+i;
					tbExploredPts[10+i][2] = Robot.WEST;

				}
			}
			
			// WEST POINT
			if(points[1]> 0){
				if(!grid[points[0]][points[1]-1].isObstacle()){
					tbExploredPts[19+i][0] = points[0]+i;
					tbExploredPts[19+i][1] = points[1]-2;
					tbExploredPts[19+i][2] = Robot.EAST;
					
					tbExploredPts[22+i][0] = points[0]+i;
					tbExploredPts[22+i][1] = points[1]-3;
					tbExploredPts[22+i][2] = Robot.EAST;

				}
			}
		}
		
		for(int i = 0; i <tbExploredPts.length;i++){
			boolean notAccessible = false;
			
			if(insideArena(tbExploredPts[i])){
				for (int j=-1;j<2;j++){
					for (int k=-1;k<2;k++){
						if(!(grid[tbExploredPts[i][0]+j][tbExploredPts[i][1]+k].isVisited())
								|| (grid[tbExploredPts[i][0]+j][tbExploredPts[i][1+k]].isObstacle())){
							notAccessible = true;
							break;
						}
						if(j == 1 && k ==1 && !notAccessible){
							accessiblePts[counter][0] = tbExploredPts[i][0];
							accessiblePts[counter][1] = tbExploredPts[i][1];
							accessiblePts[counter][2] = tbExploredPts[i][2];
							counter++;
							i = i + (6 - i%6);
						}
					}
					if(notAccessible)
						break;
				}
			}
				
		}
		return accessiblePts;
	}

	private boolean insideArena(int[]point) {
		// TODO Auto-generated method stub
		int row = point[0];
		int col = point[1];
		
		if(row >0 && row < 19){
			if(col > 0 && col < 19)
				return true;
		}
		return false;
	}

	private String cleanupExploration(Grid[][] grid, ArrayList<int[]> points) {
		// TODO Auto-generated method stub
		int[] currentPos = robot.getCurrentPos();
		int[][] pathCost;
		int[] path = null;
		int cost=99999;
		
		// implement here
		
		for(int i = 0; i < points.size();i++){
			FastestPath fp = new FastestPath(grid, currentPos, points.get(i));
			pathCost = fp.executeCost();
			
			if(pathCost[1][0] < cost){
				path = pathCost[0];
				cost = pathCost[1][0];
			}
		}
		String robotPath = "";
		
		int[] tempPos = Grid.oneDPosToTwoD(path[0]);
		int[] tempnextPos = Grid.oneDPosToTwoD(path[1]); 
		
		int curDeg = 0;
		
		if(tempnextPos[0] == tempPos[0])
		{
			if(tempnextPos[1] > tempPos[1])
				curDeg = Robot.EAST;
			else if(tempnextPos[1] < tempPos[1])
				curDeg = Robot.WEST;
		}
		else if(tempnextPos[1] == tempPos[1])
		{
			if(tempnextPos[0] > tempPos[0])
				curDeg = Robot.NORTH;
			if(tempnextPos[0] > tempPos[0])
				curDeg = Robot.SOUTH;
		}
		robotPath = robotPath + robot.turnReqDirection(robot.getDirection(), curDeg);

		for(int j = 0; j <path.length-1; j++){
			int [] curPos = Grid.oneDPosToTwoD(path[j]);
			int [] nextPos = Grid.oneDPosToTwoD(path[j+1]);
			
			String curTurn = robot.turnString(curPos, nextPos, curDeg);
			
			if (curTurn.equals(RpiCommProtocol.RIGHTTURN))
				curDeg = robot.getDirAftRightTurn(curDeg);
			else if (curTurn.equals(RpiCommProtocol.LEFTTURN))
				curDeg = robot.getDirAftLeftTurn(curDeg);
			
			robotPath = robotPath + curTurn;
			robotPath = RpiCommProtocol.MOVESTRAIGHT;
		}
		
		for(int j = 0; j < robotPath.length();  j++){
			switch(robotPath.charAt(j)){
			case 'R':
				robot.rightTurn();
				break;
			case 'L':
				robot.leftTurn();
				break;
			case 'M':
				robot.goStraight();
				break;
			case 'B':
				robot.goBack();
				break;
			}
			
			System.out.println(robot.getCurrentPos()[0] +", " + robot.getCurrentPos()[1]);

			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			arena.updateRobotPos();
			
			if(enableCoverageTerminal)
			{
				if(arena.calculateExplorationCoverage() >= coveragePercentage){
					System.out.println(arena.calculateExplorationCoverage());
					System.out.println(coveragePercentage);
					System.out.println("need to go back");
					isTimetoGoBack = true;
				}
			}
		}
		return robotPath;
	}

	public void cleanUpExploration(){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				cleanExplorationThread();
			}
		});
	    thread.setPriority(Thread.NORM_PRIORITY);  
	    thread.start();	
		
	}
	
	private void turnBackAndGoBack() {
		// TODO Auto-generated method stub
		robot.goBack();
		int index = 0;
		boolean goStraight = true;
		
		do{
			index++;
			robot.getSensorData(grid);
			
			if(goStraight){
				if(!isFrontBlocked())
					robot.goStraight();
				goStraight = false;
			}
			else if(!this.isRightBlocked()){
				robot.rightTurn();
				goStraight = true;
			}
			else if(!isFrontBlocked()){
				robot.goStraight();
				goStraight = false;
			}
			else if (!this.isLeftBlocked()){
				robot.leftTurn();
				goStraight = true;
			}
			else{
				robot.goBack();
				goStraight = false;
			}
			
			if (robot.getCurrentPos()[0] ==1 && robot.getCurrentPos()[1] == 13){
				isAtGoal = true;
			}
			
			try{
				Thread.sleep(1000/speed);
			}
			catch (InterruptedException ie){
				ie.printStackTrace();
			}

			// UPDATE ROBOT ARENA
			arena.updateRobotPos();
			
			// TODO: update coverage and time here
		
			
		} while(!(robot.getCurrentPos()[0] == 18 && robot.getCurrentPos()[1] == 1));
		
		arena.updateRobotPos();
		// prepare for fastest path 
		
		robot.calibrateToNorth();
		arena.updateRobotPos();	
	}

	private boolean isLeftBlocked() {
		// TODO Auto-generated method stub
		int row = robot.getCurrentPos()[0];
		int column = robot.getCurrentPos()[1];
		int direction = robot.getDirection();
		
		switch(direction){
		case NORTH:
			if(!robot.isMovable(row, column, grid, WEST)){
				return true;
			}
		case SOUTH:
			if(!robot.isMovable(row, column, grid, EAST)){
				return true;
			}
		case EAST:
			if(!robot.isMovable(row, column, grid, NORTH)){
				return true;
			}
		case WEST:
			if(!robot.isMovable(row, column, grid, SOUTH)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isFrontBlocked() {
		// TODO Auto-generated method stub
		int row = robot.getCurrentPos()[0];
		int column = robot.getCurrentPos()[1];
		int direction = robot.getDirection();
		
		switch(direction){
		case NORTH:
			if(!robot.isMovable(row, column, grid, NORTH)){
				return true;
			}
		case SOUTH:
			if(!robot.isMovable(row, column, grid, SOUTH)){
				return true;
			}
		case EAST:
			if(!robot.isMovable(row, column, grid, EAST)){
				return true;
			}
		case WEST:
			if(!robot.isMovable(row, column, grid, WEST)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isRightBlocked() {
		// TODO Auto-generated method stub
		int row = robot.getCurrentPos()[0];
		int column = robot.getCurrentPos()[1];
		int direction = robot.getDirection();
		
		switch(direction){
		case NORTH:
			if(!robot.isMovable(row, column, grid, EAST)){
				return true;
			}
		case SOUTH:
			if(!robot.isMovable(row, column, grid, WEST)){
				return true;
			}
		case EAST:
			if(!robot.isMovable(row, column, grid, SOUTH)){
				return true;
			}
		case WEST:
			if(!robot.isMovable(row, column, grid, NORTH)){
				return true;
			}
		}
		
		return false;
	}


	// NEED TO CHECK GOT USE ANOT
	public void setCoveragePercentage(int percentage){
		this.coveragePercentage = percentage;
	}
	// NEED TO CHECK GOT USE ANOT
	public boolean getCoverageTerminal(){
		return enableCoverageTerminal;
	}
	// NEED TO CHECK GOT USE ANOT
	public void switchCoverageTerminal(){
		if(enableCoverageTerminal){
			enableCoverageTerminal = false;
			isTimetoGoBack =false;
		}
		else{
			enableCoverageTerminal = true;

		}
	}
	// NEED TO CHECK GOT USE ANOT
	public void switchTimerTerminal()
	{
		if(enableTimerTerminal)
			enableTimerTerminal = false;
		else 
			enableTimerTerminal = true;
	}

	public void setSpeed(int newSpeed){
		this.speed = newSpeed;
	}
	
	public int getSpeed(){
		return this.speed;
	}
	
	public void setTimesUp(boolean result){
		this.timesUp = result;
	}
	
	public boolean isTimesUp(){
		return this.timesUp;
	}

	
}

package cz3004MDP.controllers;

import java.util.ArrayList;

import cz3004MDP.models.Arena;
import cz3004MDP.models.Grid;
import cz3004MDP.models.Robot;
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

	private int[][] pointAccessible(int[] is) {
		// TODO Auto-generated method stub
		return null;
	}

	private void cleanupExploration(Grid[][] grid2, ArrayList<int[]> points) {
		// TODO Auto-generated method stub
		int[] currentPos = robot.getCurrentPos();
		int[][] pathCost;
		int[] path = null;
		int cost=99999;
		
		// implement here
		
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

package cz3004MDP.controllers;

import cz3004MDP.models.Arena;
import cz3004MDP.models.Grid;
import cz3004MDP.models.Robot;

public class ExplorationManager {

	private Robot robot;
	private Arena arena;
	private Grid[][] grid;
	
	private boolean isAtGoal = false;
	private boolean isTimetoGoBack = false;
	
	// robot speed
	private int speed;
	
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
	}
	
	public void explore(){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				startExploration();
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
					
				}
				// stop when robot is back to start zone from goal zone
				while((!(robot.getCurrentPos()[0] == 18 && robot.getCurrentPos()[1] == 1) || isAtGoal!=true) 
						&& isTimetoGoBack ==false && timesUp == false);
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
}

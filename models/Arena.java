package cz3004MDP.models;

import cz3004MDP.controllers.ExplorationManager;
import cz3004MDP.services.RpiCommProtocol;


public class Arena implements ArenaRobot{
	
	Grid[][] arenaSimulate = new Grid[ROW][COLUMN];
	Grid[][] arenaReal = new Grid[ROW][COLUMN];

	private Robot robot;
	private Arena arena;
	private ExplorationManager exploreAlgo;
	
	// Default Constructor
	public Arena(Robot robot){
		this.robot = robot;
		arena = this;
		exploreAlgo = new ExplorationManager(robot, arena);
		this.updateRobotPos();
	}

	public Grid[][] getGrid(){
		return arenaSimulate;
	}
	
	public void resetArena(){
		for (int i = 0; i < ROW;  i++){
			for(int j = 0; j < COLUMN; j++){
				arenaSimulate[i][j].setClearGrid(false);
				arenaSimulate[i][j].setVisited(false);
			}
		}
	}
	
	public void updateRobotPos() {
		// update explored arena before robot pos
		updateArenaAftMove();
		
		/*
		//change color from green to white
		updateArena(gridCell2);
		 */
		
		int[] curPos = new int[2];
		curPos[0] = robot.getCurrentPos()[0];
		curPos[1] = robot.getCurrentPos()[1];
		
		/* SET ROBOT COLOR HERE
		for(int i=-1; i < 2; i++){
			for(int j=-1; j < 2; j++){
				gridCell2[currentPosition[0]+i][currentPosition[1]+j].setBackground(ROBOT_COLOR);
			}
		}
		 */
		
		switch(robot.getDirection()){
		case NORTH:
			curPos[0] = curPos[0] - 1;
			break;
		case SOUTH:
			curPos[0] = curPos[0] + 1;
			break;
		case EAST:
			curPos[1] = curPos[1] + 1;
			break;
		case WEST:
			curPos[1] = curPos[1] - 1;
			break;
		}
		/*
		 gridCell2[currentPosition[0]][currentPosition[1]].setBackground(ROBOTDIRECTION_COLOR);
		 */
	}

	private void updateArenaAftMove() {
		// TODO Auto-generated method stub
		int[] pos = robot.getPrevPos();
		for(int i =-1; i<2;i++){
			for(int j=-1; j < 2; j++){
				if (!arenaSimulate[pos[0]+i][pos[1]+j].isVisited())
					arenaSimulate[pos[0]+i][pos[1]+j].setVisited(true);
			}
		}
		
		// get sensor data
		robot.getSensorData(arenaSimulate);
	}

	public double calculateExplorationCoverage(){
		int counter = 0;
		double size = ROW*COLUMN;
		
		for (int i = 0; i < ROW; i++){
			for(int j = 0; j < COLUMN; j++){
				if (arenaSimulate[i][j].isVisited())
					counter++;
			}
		}
		return (counter/size) *100;
	}


}

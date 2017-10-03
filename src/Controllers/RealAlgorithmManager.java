package Controllers;

import java.util.ArrayList;

import Model.Arena;
import Model.Grid;
import Model.Robot;
import Model.RobotArenaProtocol;
import Services.Utilities;

public class RealAlgorithmManager implements RobotArenaProtocol{

	private Robot robot;
	private Arena arena;
	private Grid[][] grid;
	private int[] wayPoint;
	private Utilities utility = new Utilities();
	private boolean timeToGoBack = false;
	private boolean reachedGoal = false;
	private boolean isAtWayPoint  = false;
	private int speed;
	private int coveredPercentage;
	private boolean timesUp = false;
	private boolean enableCoverageTerminal = false;
	private boolean enableTimerTerminal = false;
	private RpiManager rpiMgr = null;
	private int moveCountCleanup = 0;
	
	public RealAlgorithmManager(Robot robot, Arena arena, int[] wayPoint, RpiManager rpiMgr){
		this.robot = robot;
		this.arena = arena;
		this.rpiMgr = rpiMgr;
		this.wayPoint = wayPoint;
		this.grid = arena.getRealArena();
	}
	
	public void realGo(){
		 Thread thread = new Thread(new Runnable() {  
		        public void run() {  
		            startExploration();
		        }
		    }  );
		    thread.setPriority(Thread.NORM_PRIORITY);  
		    thread.start();
	}
	
	public void startExploration()
	{
		boolean goStraight = false;
		int moveCount = 0;
		
		Arena.appendMessage("Starting Real Exploration..");
		// loop while robot is not yet at end goal
		do{
			// send starting arena and robot info to android for mapping
			rpiMgr.sendInstruction(TABLET +"explore:\"" + arenaInforToStringVisited() + "\"");
			rpiMgr.sendInstruction(TABLET + "grid:\"" + arenaInforToStringObstacle()+ "\"");		
			rpiMgr.sendInstruction(TABLET + robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));

			
			int row = robot.getCurrentPosition()[0];
			int column = robot.getCurrentPosition()[1];
		
			if(goStraight){
				// move forward
				goStraight = false;

				robot.goStraight();
				rpiMgr.sendInstruction(AUDUINO + STRAIGHTMOVE1);
				//rpiMgr.sendInstruction(TABLET + STRAIGHTMOVE1);

				moveCount++;

				
			} else if(!this.leftIsBlocked()){
				// TURN LEFT
				// Calibrate before turning
				if(robot.canFrontCalibrate(row, column, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					moveCount = 0;
				}
				if(robot.canRightCalibrate(row, column, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
					moveCount = 0;
				}
				
				robot.turnLeft();
				rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
				goStraight = true;

			} else if(!this.frontIsBlocked()){
				// move forward
				robot.goStraight();
				goStraight = false;
				rpiMgr.sendInstruction(AUDUINO + STRAIGHTMOVE1);
				//rpiMgr.sendInstruction(TABLET + STRAIGHTMOVE1);
				moveCount++;


			}  else if(!this.rightIsBlocked()){
				// turn right
				// Calibrate before turning
				if(robot.canFrontCalibrate(row, column, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					moveCount = 0;
				}
				if(robot.canLeftCalibrate(row, column, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
					moveCount = 0;
				}
				robot.turnRight();
				rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
				goStraight = true;
			}
			else {
				// go back
				// Calibrate before turning
				if(robot.canFrontCalibrate(row, column, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					moveCount = 0;
				}
				if(robot.canLeftCalibrate(row, column, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);					
					moveCount = 0;
				}
				else{
					rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
					rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
					rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
					moveCount = 0;
				}
				robot.turnBack();
				rpiMgr.sendInstruction(AUDUINO + TURNBACK);
				robot.turnBack();
				goStraight = false;
			}
			
			// do a calibration for every 7 moves
			if(moveCount>3 && !this.frontIsBlocked()){
				moveCount = 0;
				calibrateRobot();
			}
			// Output Moves position
			Arena.appendMessage("Current Position: " + robot.getCurrentPosition()[0] + " ; " + robot.getCurrentPosition()[1] + "\tCounter: " + moveCount);
			
			// Update on Android
			rpiMgr.sendInstruction(TABLET +"explore:\"" + arenaInforToStringVisited() + "\"");
			rpiMgr.sendInstruction(TABLET + "grid:\"" + arenaInforToStringObstacle()+ "\"");		
			rpiMgr.sendInstruction(TABLET + robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));

			if(robot.getCurrentPosition()[0] == GOALPOSITION[0] 
					&& robot.getCurrentPosition()[1] == GOALPOSITION[1])
				reachedGoal=true;
	
			arena.updateRobotPosition();
			//arena.updateCoverageAndTime();
				
		}while(!(robot.getCurrentPosition()[0] ==18 
				&& robot.getCurrentPosition()[1] == 1) 
				|| reachedGoal!=true);

		// explore the rest using dijkstra algo
		cleanupExplorationThread();
		
		// do a final calibration at start position
		calibrateRobot();
		

		if(reachedGoal){
		 	switch(robot.getRobotHead()){
			case NORTH:
				break;
			case SOUTH:
				rpiMgr.sendInstruction(AUDUINO + TURNBACK);
				break;
			case EAST:
				rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
				break;
			case WEST:
				rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
				break;
			}			
			Arena.appendMessage("Exploration Completed!");
			
			// save file
			ArrayList<String> result = new ArrayList<String>();
			result = utility.exportMap(grid);
			utility.saveArenaToFile(FILENAME2,result);
			if(enableTimerTerminal)
				arena.stopTimer();
			
			
			rpiMgr.sendInstruction(TABLET +"explore:\"" + arenaInforToStringVisited() + "\"");
			rpiMgr.sendInstruction(TABLET + "grid:\"" + arenaInforToStringObstacle()+ "\"");		
			rpiMgr.sendInstruction(TABLET + robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
		}
		robot.turnToNorth();
		arena.updateRobotPosition();
		fpgo(grid);
	
	}
	
	private void cleanupExplorationThread() {
		// TODO Auto-generated method stub
		boolean explorable = true;
		do
		{
			ArrayList<int[]> points = new ArrayList<int[]>();
			for(int i=0; i< COLUMN; i++)
			{
				for(int j=0; j< ROW; j++)
				{
					if(grid[j][i].getGridStatus()[0] == NOT_VISITED)
					{
						int[][] temp = getAccessibleGrids(new int[]{j, i});
						for(int k=0; k<temp.length;k++)
							if(temp[k][0] != 0 && temp[k][1] != 0)
							points.add(temp[k]);
					}
				}
			}
			if(points.size() > 0)
			{
				//find fastest path to the cells+direction, return lowest cost path STRING
				//travel to cell using path string
				cleanupExploration(grid, points);
			}
			else{
				Arena.appendMessage("Fully explored or no path to unexplored areas");

				if(robot.getCurrentPosition()[0] != 18  || robot.getCurrentPosition()[1] != 1)
				{
					points.add(STARTPOSITION);
					cleanupExploration(grid, points);
				}
				explorable = false;
			}
		}while(explorable);
	}
	
	private String cleanupExploration(Grid[][] grid, ArrayList<int[]> points) {
		// TODO Auto-generated method stub

		int[] currentPos = robot.getCurrentPosition();
		int[][] pathCost;
		int[] path = null;
		int cost=99999;
		int curDeg = 0;

		int moveCount = 0; 
		for(int i=0; i<points.size(); i++)
		{
			FastestPath fp = new FastestPath(grid, currentPos, points.get(i));
			pathCost = fp.executeCost();
			
			if(pathCost[1][0] < cost){
				path = pathCost[0];
				cost = pathCost[1][0];
			}
		}
		
		String robotPath = "";
		int[] tempPos = Grid.convert1DPositionTo2DPositon(path[0]);
		int[] tempnextPos = Grid.convert1DPositionTo2DPositon(path[1]);
		
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
		robotPath += robot.turnToReqDirection(robot.getRobotHead(), curDeg);
		
		for(int j = 0; j<path.length-1;j++){
			int[] curPos = Grid.convert1DPositionTo2DPositon(path[j]);
			int[] nxtPos = Grid.convert1DPositionTo2DPositon(path[j+1]);
	
			String curTurn = robot.turnString(curPos, nxtPos, curDeg);
			if(curTurn.equals(RobotArenaProtocol.TURNRIGHT))
				curDeg = robot.getDirAfterRightTurn(curDeg);
			else if(curTurn.equals(RobotArenaProtocol.TURNLEFT))
				curDeg = robot.getDirAfterLeftTurn(curDeg);
			
			robotPath += curTurn;
			robotPath += RobotArenaProtocol.STRAIGHTMOVE1;
		}
		
		for(int j = 0; j < robotPath.length(); j++){
			int row = robot.getCurrentPosition()[0];
			int column = robot.getCurrentPosition()[1];
			
			Arena.appendMessage("Current Movecount: " + moveCount);
			
			if(moveCount>7)
				calibrateRobot();
		
			switch (robotPath.charAt(j)){
			case 'D':
				robot.turnRight();
				rpiMgr.sendInstruction(AUDUINO+TURNRIGHT);
				break;
			case 'A':
				robot.turnLeft();
				rpiMgr.sendInstruction(AUDUINO+TURNLEFT);
				break;
			case 'W':
				robot.goStraight();
				rpiMgr.sendInstruction(AUDUINO+STRAIGHTMOVE1);
				break;
			case 'B':
				robot.turnBack();
				rpiMgr.sendInstruction(AUDUINO+TURNBACK);
				break;
				default:
		
			}
			Arena.appendMessage("Current Pos: " + robot.getCurrentPosition()[0] + ";" + robot.getCurrentPosition()[1]);

			rpiMgr.sendInstruction(TABLET +"explore:\"" + arenaInforToStringVisited() + "\"");
			rpiMgr.sendInstruction(TABLET + "grid:\"" + arenaInforToStringObstacle()+ "\"");		
			rpiMgr.sendInstruction(TABLET + robotLocationToString(row, column, robot.getRobotHead()));
			
			
			arena.updateRobotPosition();
			//arena.updateCoverageAndTime();
		}
		return robotPath;
	}

	public void fpgo(final Grid[][] grid){
		 Thread thread = new Thread(new Runnable() {  
		        public void run() {  
		        	// get shortest path to waypoint
		        	Arena.appendMessage("Executing Fastest Path to waypoint!");
		        	isAtWayPoint = false;
		        	getFastestPath(grid);
		        	 
		        	Arena.appendMessage("Reached waypoint!");
		        	Arena.appendMessage("Executing Fastest Path to GOAL!");
		        	isAtWayPoint = true;
		        	// get shortest path to GOAL
		        	getFastestPath(grid);
		        }
		    }  );
		    thread.setPriority(Thread.NORM_PRIORITY);  
		    thread.start();
	}
	
	public String getFastestPath(Grid[][] grid)
	{
		FastestPath fp;
		if(!isAtWayPoint)
			fp = new FastestPath(grid, STARTPOSITION, wayPoint);
		else
			fp = new FastestPath(grid, wayPoint, GOALPOSITION);
		 
		int[] path = fp.execute();
		
		Arena.appendMessage("Getting fastest path road");
		String robotPath = "";
		int curDeg = (Grid.convert1DPositionTo2DPositon(path[0]))[1] < (Grid.convert1DPositionTo2DPositon(path[1]))[1] ? Robot.EAST : Robot.NORTH;
		robotPath += robot.turnToReqDirection(robot.getRobotHead(), curDeg);

		for(int j = 0; j<path.length-1;j++){
			int[] curPos = Grid.convert1DPositionTo2DPositon(path[j]);
			int[] nxtPos = Grid.convert1DPositionTo2DPositon(path[j+1]);
			
			String curTurn = robot.turnString(curPos, nxtPos, curDeg);
			if(curTurn.equals(TURNRIGHT)){
				curDeg = robot.getDirAfterRightTurn(curDeg);
			}
			else if(curTurn.equals(TURNLEFT)){
				curDeg = robot.getDirAfterLeftTurn(curDeg);
			}
			robotPath += curTurn;
			robotPath += STRAIGHTMOVE1;
		}
		
		String newPath = "";
		for(int j = 0; j < robotPath.length(); j++){
			switch (robotPath.charAt(j)){
			case 'D':
				robot.turnRight();
				newPath += TURNRIGHT;
				rpiMgr.sendInstruction(TABLET+robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
				break;
			case 'A':
				robot.turnLeft();
				newPath += TURNLEFT;
				rpiMgr.sendInstruction(TABLET+robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
				break;
			case 'W':
				int counter = 1;
				for(int a = j+1; a <robotPath.length();a++){
					if(robotPath.charAt(a) == 'W'){
						robot.goStraight();
						arena.updateRobotPosition();
						rpiMgr.sendInstruction(TABLET+robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
						counter++;
						j++;
						if(counter>=7)
							break;
					}
					else
						break;
				}
				robot.goStraight();
				rpiMgr.sendInstruction(TABLET+robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
				newPath += STRAIGHTMOVE1 + counter;
			case 'B':
				robot.turnBack();
				rpiMgr.sendInstruction(TABLET+robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
				break;
				default:
			}
			
			Arena.appendMessage(robot.getCurrentPosition()[0] +", " + robot.getCurrentPosition()[1]);
			
		}
		rpiMgr.sendInstruction(AUDUINO+newPath);
		rpiMgr.sendInstruction(TABLET+robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()));
		return robotPath;
	}
	
	//receive a single point, and return accessible points from NSEW, return value: int[index][point 0=row, 1=col][direction]
	public int[][] getAccessibleGrids(int[] points){
		int count =0;
		int[][] toBeExploredPoints = new int[24][3];
		int[][] accessiblePoints = new int[24][3];
	
		for(int i=-1; i<2; i++){

			//get NORTH point
			if(points[0]>0)
				if(grid[points[0]-1][points[1]].getGridStatus()[1] != OBSTACLE)
				{
					toBeExploredPoints[1+i][0] = points[0]-2;
					toBeExploredPoints[1+i][1] = points[1]+i;
					toBeExploredPoints[1+i][2] = Robot.SOUTH;
					
					toBeExploredPoints[4+i][0] = points[0]-3;
					toBeExploredPoints[4+i][1] = points[1]+i;
					toBeExploredPoints[4+i][2] = Robot.SOUTH;
				}
			//get SOUTH point
			if(points[0]<19)
				if(grid[points[0]+1][points[1]].getGridStatus()[1] != OBSTACLE)
				{
					toBeExploredPoints[13+i][0] = points[0]+2;
					toBeExploredPoints[13+i][1] = points[1]+i;
					toBeExploredPoints[13+i][2] = Robot.NORTH;
					
					toBeExploredPoints[16+i][0] = points[0]+3;
					toBeExploredPoints[16+i][1] = points[1]+i;
					toBeExploredPoints[16+i][2] = Robot.NORTH;
				}
			//get EAST point
			if(points[1]<14)
				if(grid[points[0]][points[1]+1].getGridStatus()[1] != OBSTACLE)
				{
					toBeExploredPoints[7+i][0] = points[0]+i;
					toBeExploredPoints[7+i][1] = points[1]+2;
					toBeExploredPoints[7+i][2] = Robot.WEST;
					
					toBeExploredPoints[10+i][0] = points[0]+i;
					toBeExploredPoints[10+i][1] = points[1]+3;
					toBeExploredPoints[10+i][2] = Robot.WEST;
				}			
			//get WEST point
			if(points[1]>0)
				if(grid[points[0]][points[1]-1].getGridStatus()[1] != OBSTACLE)
				{
					toBeExploredPoints[19+i][0] = points[0]+i;
					toBeExploredPoints[19+i][1] = points[1]-2;
					toBeExploredPoints[19+i][2] = Robot.EAST;
					
					toBeExploredPoints[22+i][0] = points[0]+i;
					toBeExploredPoints[22+i][1] = points[1]-3;
					toBeExploredPoints[22+i][2] = Robot.EAST;
				}
			
		}
		
		for(int i = 0; i<toBeExploredPoints.length; i++){
			boolean unaccessible = false;

			if(insideArena(toBeExploredPoints[i]))
			{
				for(int j=-1; j<2; j++){
					for(int k=-1; k<2; k++){
						if(grid[toBeExploredPoints[i][0]+j][toBeExploredPoints[i][1]+k].getGridStatus()[0] == NOT_VISITED 
								|| grid[toBeExploredPoints[i][0]+j][toBeExploredPoints[i][1]+k].getGridStatus()[1] == OBSTACLE){
							unaccessible = true;
							break;
						}
						if(j==1 && k==1 && !unaccessible){
							accessiblePoints[count][0] = toBeExploredPoints[i][0];
							accessiblePoints[count][1] = toBeExploredPoints[i][1];
							accessiblePoints[count][2] = toBeExploredPoints[i][2];
							count++;
							i = i + (6  - i%6);
						}
					}
					if(unaccessible == true)
						break;
				}
			}
		}
		
		return accessiblePoints;
	}
	
	private boolean insideArena(int[] point){
		int row = point[0];
		int col = point[1];
		
		if(row>0 && row <19 && col>0 && col<14)
			return true;
		else
			return false;
	}

	/*
	 * CALIBRATION AND SENSOR CHECKER
	 */
	private void calibrateRobot() {
		// TODO Auto-generated method stub
		int row = robot.getCurrentPosition()[0];
		int column = robot.getCurrentPosition()[1];
		
		if(robot.canLeftCalibrate(row, column, grid, robot.getRobotHead())){
			rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
			rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
			rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
		}
		else if (robot.canRightCalibrate(row, column, grid, robot.getRobotHead())){
			rpiMgr.sendInstruction(AUDUINO + TURNRIGHT);
			rpiMgr.sendInstruction(AUDUINO + CALIBRATE);
			rpiMgr.sendInstruction(AUDUINO + TURNLEFT);
		}
	}

	public boolean frontIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getRobotHead();
		if(direction == NORTH)
		{
			if(!robot.isMovable(x, y, grid, NORTH))
			{
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.isMovable(x, y, grid, SOUTH))
			{
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.isMovable(x, y, grid, EAST))
			{
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.isMovable(x, y, grid, WEST))
			{
				return true;
			}
		}
		return false;	
	}
	
	public boolean leftIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getRobotHead();
		if(direction == NORTH)
		{
			if(!robot.isMovable(x, y, grid, WEST))
			{
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.isMovable(x, y, grid, EAST))
			{
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.isMovable(x, y, grid, NORTH))
			{
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.isMovable(x, y, grid, SOUTH))
			{
				return true;
			}
		}
		return false;	
	}
	
	public boolean rightIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getRobotHead();
		if(direction == NORTH)
		{
			if(!robot.isMovable(x, y, grid, EAST))
			{
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.isMovable(x, y, grid, WEST))
			{
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.isMovable(x, y, grid, SOUTH))
			{
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.isMovable(x, y, grid, NORTH))
			{
				return true;
			}
		}
		return false;	
	}
	
	
	/*
	 * STRING CONVERTER
	 * 
	 */
	private String getFinalString() {
		// TODO Auto-generated method stub
		ArrayList<String> output = new ArrayList<String>();
		output = utility.exportMap(grid);
		String result = "{\"Map String\" : [" + output.get(1) + "]}";
		return result;
	}
	
	private String robotLocationToString(int row, int column, int robotHead) {
		String output = "rPos:" + row + "," + column + "," + robotHead;
		return output;
	}

	private String arenaInforToStringObstacle() {
		// TODO Auto-generated method stub
		String hexResult = "";
		String binResult = "";
		
		for(int i =0; i < ROW; i++){
			for(int j =0; j < COLUMN; j++){
				binResult = binResult + grid[i][j].getGridStatus()[1];
			}
		}
		hexResult = utility.binToHex(binResult);
		return hexResult;
	}
	
	private String arenaInforToStringVisited() {
		// TODO Auto-generated method stub
		String hexResult = "";
		String binResult = "";
		
		for(int i =0; i < ROW; i++){
			for(int j =0; j < COLUMN; j++){
				binResult = binResult + grid[i][j].getGridStatus()[0];
			}
		}
		hexResult = utility.binToHex(binResult);
		return hexResult;
	}
}

package Controllers;


import java.util.ArrayList;

import Model.Arena;
import Model.RobotArenaProtocol;
import Model.Grid;
import Model.Robot;
import Services.Utilities;

public class SimAlgorithmManager implements RobotArenaProtocol{

	private Robot robot;
	private Arena arena;
	private Grid[][] grid;
	private int[] wayPoint;
	private Utilities utility = new Utilities();
	private boolean timeToGoBack = false;
	private boolean reachedGoal = false;
	private boolean isAtWayPoint = false;
	private int speed;
	private int coveredPercentage;
	private boolean timesUp = false;
	private boolean enableCoverageTerminal = false;
	private boolean enableTimerTerminal = false;
	
	public SimAlgorithmManager(Robot robot, Arena arena, int[] wayPoint)
	{
		this.robot = robot;
		this.arena = arena;
		this.grid = arena.getRealArena();
		this.wayPoint = wayPoint;
	}
	
	public void resetAlgo(){
		timesUp = false;
		enableCoverageTerminal = false;
		enableTimerTerminal = false;
		isAtWayPoint = false;
		reachedGoal = false;
		timeToGoBack = false;
	}
	
	public void simGo(){
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

			// loop while robot is not yet at end goal
			do{
				robot.getSensorsData(grid);
				arena.updateRobotPosition();
				
				int x = robot.getCurrentPosition()[0];
				int y = robot.getCurrentPosition()[1];
			
				if(goStraight){
					robot.goStraight();
					goStraight = false;
				} else if(!this.leftIsBlocked()){
					robot.turnLeft();
					goStraight = true;
				} else if(!this.frontIsBlocked()){
					robot.goStraight();
					goStraight = false;
				}  else if(!this.rightIsBlocked()){
					robot.turnRight();
					goStraight = true;
				}
				else {
					robot.turnBack();
					goStraight = false;
				}
				
				if(robot.getCurrentPosition()[0]==1 && robot.getCurrentPosition()[1]==13){
					reachedGoal=true;
				}
				try {
					Thread.sleep(1000/speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				arena.updateCoverageAndTime();
				
			   if(enableTimerTerminal){
				   if(timesUp == true){
					   reachedGoal = true;
						Arena.appendMessage("Time up going back");
				   }
			   }
		
				if(enableCoverageTerminal)
				{
					if(arena.calculateExploredPercentage() >= coveredPercentage){
						reachedGoal = true;
						timeToGoBack = true;
						Arena.appendMessage(String.valueOf(arena.calculateExploredPercentage()));
						Arena.appendMessage(String.valueOf(coveredPercentage));
						Arena.appendMessage("need to go back");
						arena.stopTimer();
					}
				}
				
				
				
				Arena.appendMessage("Current Pos: " + robot.getCurrentPosition()[0] + ";" + robot.getCurrentPosition()[1]);
					
			}while((!(robot.getCurrentPosition()[0] == 18 && robot.getCurrentPosition()[1] == 1)
					|| reachedGoal!=true) && timeToGoBack==false && timesUp == false);
			
			arena.updateRobotPosition();
			if(timeToGoBack || timesUp)
				turnBackAndGoBack();
			
			
			if(reachedGoal){
				
				if(!enableCoverageTerminal){
					cleanupExplorationThread();
					robot.turnToNorth();
					arena.updateRobotPosition();					
				} else if (arena.calculateExploredPercentage() < coveredPercentage && timesUp == false) {
	
					cleanupExplorationThread();
					robot.turnToNorth();
					arena.updateRobotPosition();	
					timeToGoBack = false;
					
				}
				else
					Arena.appendMessage("Exploration has completed the required coverage percentage");
				
				
				arena.allowFastestPath(true);
				utility.playExploreSuccessSound();
				// save file
				ArrayList<String> result = new ArrayList<String>();
				result = utility.exportMap(grid);
				utility.saveArenaToFile(FILENAME2,result);
				if(enableTimerTerminal)
					arena.stopTimer();
			}	
		}
		
	public void turnBackAndGoBack(){
		robot.turnBack();
		
		boolean goStraight = true;
		do{
			robot.getSensorsData(grid);
		
			if(goStraight){
				if(!this.frontIsBlocked())
				{
				robot.goStraight();
				}
				goStraight = false;
			} 
			else if(!this.rightIsBlocked()){
				robot.turnRight();
				goStraight = true;
			}
			 else if(!this.frontIsBlocked()){
				robot.goStraight();
				goStraight = false;
			}  else if(!this.leftIsBlocked()){
				robot.turnLeft();
				goStraight = true;
			}
			else {
				robot.turnBack();
				goStraight = false;
			}
			
			if(robot.getCurrentPosition()[0]==1 && robot.getCurrentPosition()[1]==13){
				reachedGoal=true;
			}
			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arena.updateRobotPosition();
			arena.updateCoverageAndTime();

		}while(!(robot.getCurrentPosition()[0] ==18 && robot.getCurrentPosition()[1] == 1));
		arena.updateRobotPosition();
		robot.turnToNorth();
		arena.updateRobotPosition();
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

	public String getFastestPath(Grid[][] grid)
	{
		FastestPath fp;
		if(!isAtWayPoint)
			fp = new FastestPath(grid, STARTPOSITION, wayPoint);
		else
			fp = new FastestPath(grid, wayPoint, GOALPOSITION);
		
		int[] path = fp.execute();
		String robotPath = "";
		int curDeg = (Grid.convert1DPositionTo2DPositon(path[0]))[1] < (Grid.convert1DPositionTo2DPositon(path[1]))[1] ? Robot.EAST : Robot.NORTH;
		robotPath += robot.turnToReqDirection(robot.getRobotHead(), curDeg);

		for(int j = 0; j<path.length-1;j++){
			int[] curPos = Grid.convert1DPositionTo2DPositon(path[j]);
			int[] nxtPos = Grid.convert1DPositionTo2DPositon(path[j+1]);
			String curTurn = robot.turnString(curPos, nxtPos, curDeg);
			if(curTurn.equals(RobotArenaProtocol.TURNRIGHT)){
				curDeg = robot.getDirAfterRightTurn(curDeg);
			}
			else if(curTurn.equals(RobotArenaProtocol.TURNLEFT)){
				curDeg = robot.getDirAfterLeftTurn(curDeg);
			}
			robotPath += curTurn;
			robotPath += RobotArenaProtocol.STRAIGHTMOVE;

		}	
		
		String newPath = "";
		for(int j = 0; j < robotPath.length(); j++){
			switch (robotPath.charAt(j)){
			case 'D':
				robot.turnRight();
				newPath += TURNRIGHT;
				break;
			case 'A':
				robot.turnLeft();
				newPath += TURNLEFT;
				break;
			case 'W':
				int counter = 1;
				for(int a = j+1; a <robotPath.length();a++){
					if(robotPath.charAt(a) == 'W'){
						robot.goStraight();
						arena.updateRobotPosition();
						counter++;
						j++;
						if(counter>=7)
							break;
					}
					else
						break;
				}
				robot.goStraight();
				newPath += STRAIGHTMOVE + counter;
				break;
			case 'B':
				robot.turnBack();
				break;
				default:
					
			}
			Arena.appendMessage("Current Pos: " + robot.getCurrentPosition()[0] + ";" + robot.getCurrentPosition()[1]);

			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arena.updateRobotPosition();
		}
		Arena.appendMessage(newPath);
		return newPath;
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
			
	public void cleanupExplorationThread()
	{
		boolean explorable = true;
		do
		{
			ArrayList<int[]> points = new ArrayList<int[]>();
			for(int i=0; i<RobotArenaProtocol.COLUMN; i++)
			{
				for(int j=0; j<RobotArenaProtocol.ROW; j++)
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
				cleanupExploration(grid, points);
				//travel to cell using path string
			}
			else{
				Arena.appendMessage("Fully explored or no path to unexplored areas");

				if(robot.getCurrentPosition()[0] != 18  || robot.getCurrentPosition()[1] != 1)
				{
					points.add(new int[]{18,1});
					cleanupExploration(grid, points);
				}
				explorable = false;
			}
		}while(explorable);
	}
	
	public String cleanupExploration(Grid[][] grid, ArrayList<int[]> points)
	{

		int[] currentPos = robot.getCurrentPosition();
		int[][] pathCost;
		int[] path = null;
		int cost=99999;

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
		robotPath += robot.turnToReqDirection(robot.getRobotHead(), curDeg);
		for(int j = 0; j<path.length-1;j++){
			int[] curPos = Grid.convert1DPositionTo2DPositon(path[j]);
			int[] nxtPos = Grid.convert1DPositionTo2DPositon(path[j+1]);
	
			String curTurn = robot.turnString(curPos, nxtPos, curDeg);
			if(curTurn.equals(RobotArenaProtocol.TURNRIGHT)){
				curDeg = robot.getDirAfterRightTurn(curDeg);
				
			}
			else if(curTurn.equals(RobotArenaProtocol.TURNLEFT)){
				curDeg = robot.getDirAfterLeftTurn(curDeg);
			}
			robotPath += curTurn;
			robotPath += RobotArenaProtocol.STRAIGHTMOVE;

			
		}
		for(int j = 0; j < robotPath.length(); j++){
			switch (robotPath.charAt(j)){
			case 'D':
				robot.turnRight();
				break;
			case 'A':
				robot.turnLeft();
				break;
			case 'W':
				robot.goStraight();
				break;
			case 'S':
				robot.turnBack();
				break;
				default:
		
			}
			Arena.appendMessage("Current Pos: " + robot.getCurrentPosition()[0] + ";" + robot.getCurrentPosition()[1]);
			
			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				Arena.appendMessage(ie.getMessage());
			}
			arena.updateRobotPosition();
			arena.updateCoverageAndTime();
			
			if(enableCoverageTerminal)
			{
				if(arena.calculateExploredPercentage() >= coveredPercentage){
					Arena.appendMessage(String.valueOf(arena.calculateExploredPercentage()));
					Arena.appendMessage(String.valueOf(coveredPercentage));
					Arena.appendMessage("need to go back");
					timeToGoBack = true;
				}
			}
		}
		return robotPath;

	}
	
	//receive a single point, and return accessible points from NSEW, return value: int[index][point 0=row, 1=col][direction]
	private int[][] getAccessibleGrids(int[] points){
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

	public void setCoveredPercentage(int coveredPercentage)
	{
		this.coveredPercentage = coveredPercentage;
	}
	
	public boolean getCoveredTerminal()
	{
		return enableCoverageTerminal;
	}
	
	public void switchCoveredTerminal()
	{
		if(enableCoverageTerminal){
			enableCoverageTerminal = false;
			timeToGoBack = false;
		}
		else
			enableCoverageTerminal = true;
	}

	public void switchTimerTerminal()
	{
		if(enableTimerTerminal)
			enableTimerTerminal = false;
		else
			enableTimerTerminal = true;
	}
	
	public boolean getTimerTerminal()
	{
		return enableTimerTerminal;
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public void setTimesUp(boolean timesUp)
	{
		this.timesUp = timesUp;
	}
		
}

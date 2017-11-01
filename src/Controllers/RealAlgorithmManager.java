package Controllers;

import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
	private boolean reachedGoal = false;
	private boolean isAtWayPoint  = false;
	private RpiManager rpiMgr = null;
	public static String sensorData;
	public static String fastestString = "";
	private int moveCount = 0;
	private String sentIns = "";		

	
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
				
		// loop while robot is not yet at GOAL ZONE
		do{		
			int row = robot.getCurrentPosition()[0];
			int col = robot.getCurrentPosition()[1];
			
			if(goStraight && !(this.frontIsBlocked())){
				// move forward
				rpiMgr.sendInstruction2(AUDUINO + FORWARD1 + "#" +  TABLET  + generateMsgToTablet());
				goStraight = false;
				sentIns = "W1";
				robot.goStraight();				
				moveCount++;				
			}else if(goStraight && this.frontIsBlocked()){
				rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT + "#" +  TABLET  + generateMsgToTablet());
				sentIns = "D";
				robot.turnRight();
				goStraight = true;				
				moveCount++;		
			}else if(!this.leftIsBlocked()){
				// TURN LEFT
				rpiMgr.sendInstruction2(AUDUINO + TURNLEFT + "#" +  TABLET  + generateMsgToTablet());
				sentIns = "A";
				robot.turnLeft();
				goStraight = true;
				moveCount++;
			} else if(!this.frontIsBlocked()){					
				rpiMgr.sendInstruction2(AUDUINO + FORWARD1 + "#" +  TABLET  + generateMsgToTablet());
				robot.goStraight();
				sentIns = "W1";
				moveCount++;				
			}  else if(!this.rightIsBlocked()){
				// TURN RIGHT
				rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT + "#" +  TABLET  + generateMsgToTablet());
				sentIns = "D";
				robot.turnRight();
				goStraight = true;
				moveCount++;
			}
			else {	
				// UTURN
				if(robot.canRightCalibrate(row, col, grid, robot.getRobotHead())){
					rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT + "#" +  TABLET  + generateMsgToTablet());
					robot.turnRight();
					arena.updateRobotPosition();
					rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT + "#" +  TABLET  + generateMsgToTablet());
					sentIns = "B";
					robot.turnRight();
					goStraight = false;

				}else{
					rpiMgr.sendInstruction2(AUDUINO + TURNLEFT + "#" +  TABLET  + generateMsgToTablet());
					robot.turnLeft();
					arena.updateRobotPosition();
					rpiMgr.sendInstruction2(AUDUINO + TURNLEFT + "#" +  TABLET  + generateMsgToTablet());
					sentIns = "B";
					robot.turnLeft();
					goStraight = false;
				}	
				moveCount++;
			}
			// Run turbo boost	
			executeTurboBoost();


			// Output Moves position
			Arena.appendMessage("Current Pos(R/C/Deg): " + robot.getCurrentPosition()[0] + " ; " + robot.getCurrentPosition()[1]+ " ; " + robot.getRobotHead()+
					"; Counter: " + (moveCount+1)  + " ; Sent: " +  sentIns);
		
			if(robot.getCurrentPosition()[0] == 1 && robot.getCurrentPosition()[1] == 13){
				reachedGoal=true;
			}
			// update sensor readings and robot positon on arena UI
			arena.updateRobotPosition();

		}while(!(robot.getCurrentPosition()[0] == 18 && robot.getCurrentPosition()[1] == 1) || reachedGoal == false);
		
		// explore the rest using dijkstra algo
		//cleanupExplorationThread();
		arena.updateRobotPosition();
		rpiMgr.sendInstruction(TABLET + generateMsgToTablet());

		utility.playExploreSuccessSound();
		Arena.appendMessage("Exploration Completed!");
		Arena.isExplorationDone = true;
		
		arena.allowFastestPath(true);
		
		// save file
		ArrayList<String> result = new ArrayList<String>();
		result = utility.exportMap(grid);
		utility.saveArenaToFile(FILENAME2, result);
		
		rpiMgr.sendInstruction(TABLET + generateMsgToTablet2(result));
		
		// delay before attemtping to calibrate
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// make robot to face north AKA TURNTONORTH
		calibrateRobot();
	 	switch(robot.getRobotHead()){
		case NORTH:
			break;
		case SOUTH:
			rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT);
			robot.setRobotHead(WEST);
			rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT);
			robot.setRobotHead(WEST);
			break;
		case EAST:
			robot.setRobotHead(EAST);
			rpiMgr.sendInstruction2(AUDUINO + TURNLEFT);
			break;
		case WEST:
			robot.setRobotHead(WEST);
			rpiMgr.sendInstruction2(AUDUINO + TURNRIGHT);
			break;
	 	}			
		robot.turnToNorth();
		arena.updateRobotPosition();
		
		fpgo(grid);
		while(true){
			String input = rpiMgr.getInstructionFromAndroid();
			if(input.equals("FP")){
				Arena.appendMessage("Starting Fastest Path(via WayPoint)...");
	        	// Execute Fastest String WITH NO ACKNOWLEDGEMENT
	        	rpiMgr.sendInstruction(AUDUINO + fastestString);
			}
			else if(input.equals("NWP")){
				this.wayPoint = rpiMgr.getWayPointromAndroid();
				Arena.appendMessage("New WP Cord: " + this.wayPoint[0] + ";" + this.wayPoint[1]);
				Arena.appendMessage("Generating new fastest path...");
				fpgo(grid);

			}
		}
	}
	
	private void executeTurboBoost() {
		int row = robot.getCurrentPosition()[0];
		int col = robot.getCurrentPosition()[1];
		
		//int count = runHowManyGrid(row, col, sentIns);
		int count = runHowManyGrid2(row, col, sentIns);

		if (count != 0){
			Arena.appendMessage("HELLOOOOOOOOO COUNTER IS " + count);
			for(int i = 1; i <=count; i++){
				robot.goStraight();
			}
			rpiMgr.sendInstruction2(AUDUINO + "W" + count + "#" +  TABLET  + generateMsgToTablet());
		}
	}

	private void cleanupExplorationThread() {
		boolean explorable = true;
		do{
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
				//find fastest path to the cells+direction
				// returning lowest cost path STRING
				cleanupExploration(grid, points);
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
	
	private String cleanupExploration(Grid[][] grid, ArrayList<int[]> points) {
		// TODO Auto-generated method stub

		String robotPath = "";
		int[] path = null;
		int[] currentPos = robot.getCurrentPosition();
		int[] tempPos, tempnextPos;
		int[][] pathCost;
		int cost=99999;
		int curDeg = 0;

		for(int i=0; i<points.size(); i++)
		{
			FastestPath fp = new FastestPath(grid, currentPos, points.get(i));
			pathCost = fp.executeCost();
			
			if(pathCost[1][0] < cost){
				path = pathCost[0];
				cost = pathCost[1][0];
			}
		}
		
		tempPos = Grid.convert1DPositionTo2DPositon(path[0]);
		tempnextPos = Grid.convert1DPositionTo2DPositon(path[1]);
		
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
			if(curTurn.equals(TURNRIGHT))
				curDeg = robot.getDirAfterRightTurn(curDeg);
			
			else if(curTurn.equals(TURNLEFT))
				curDeg = robot.getDirAfterLeftTurn(curDeg);
			
			robotPath += curTurn;
			robotPath += FORWARD;
		}
		Arena.appendMessage("Cur path: "+ robotPath);
		

		for(int j = 0; j < robotPath.length(); j++){
			if(moveCount >3){
				boolean result = calibrateRobot();
				if(result){
					moveCount = 0;
				}
			}
			switch (robotPath.charAt(j)){
			case 'W':
				int counter = 1;
				String newFoward = FORWARD + counter;
				rpiMgr.sendInstruction2(AUDUINO + newFoward  + "#" +  TABLET  + generateMsgToTablet());
				robot.goStraight();
				moveCount++;
				break;
				
			case 'A':
				rpiMgr.sendInstruction2(AUDUINO+TURNLEFT  + "#" +  TABLET  + generateMsgToTablet());
				robot.turnLeft();
				moveCount++;
				break;
			case 'D':
				rpiMgr.sendInstruction2(AUDUINO+TURNRIGHT + "#" +  TABLET  + generateMsgToTablet());
				robot.turnRight();
				moveCount++;
				break;			
			case 'B':
				rpiMgr.sendInstruction2(AUDUINO + TURNBACK + "#" +  TABLET  + generateMsgToTablet());
				robot.turnBack();
				moveCount++;
				break;
			}
	
			Arena.appendMessage("Current Pos(R/C/Deg/Counter): " + robot.getCurrentPosition()[0] + ";" + robot.getCurrentPosition()[1] + "; " + robot.getRobotHead() + " ; "  + moveCount);
			arena.updateRobotPosition();
		}
		return robotPath;
	}
	
	public void fpgo(final Grid[][] grid){
		 Thread thread = new Thread(new Runnable() {  
		        public void run() {  
		        	String line2;
		        	// Disable arudino calibration
		        	//rpiMgr.sendInstruction(AUDUINO + FUCKINGFAST);
		        	fastestString = "";
		        	// get shortest path to waypoint
		        	Arena.appendMessage("Executing Fastest Path to waypoint!");
		        	isAtWayPoint = false;
		        	fastestString = getFastestPath(grid);
		        	Arena.appendMessage("Reached waypoint!");
		        	
		   		    // get shortest path to GOAL
		        	Arena.appendMessage("Executing Fastest Path to GOAL!");
		        	isAtWayPoint = true;
		        	line2 = getFastestPath(grid);
		        	
		        	Arena.appendMessage("String 1: " + fastestString);
		        	Arena.appendMessage("String 2: " + line2);

		        	/*
		        	if (line2.charAt(0) != 'W'){
		        		fastestString += line2;
		        	}
		        	else{
		        		int second = line2.charAt(1);
		        		line2.substring(2, line2.length());
		        		int first = fastestString.charAt(fastestString.length()-1);
		        		
		        		String patch = "W" + second + first;
		        		fastestString.substring(0, fastestString.length()-2);
		        		fastestString = fastestString + patch + line2;
		        	}*/
	        		fastestString += line2; 
		        	Arena.appendMessage("Fastest Path road: " + fastestString);
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
			robotPath += FORWARD;
		}
		
		String newPath = "";
		for(int j = 0; j < robotPath.length(); j++){
			switch (robotPath.charAt(j)){
			case 'A':
				robot.turnLeft();
				newPath += TURNLEFT;
				break;
			case 'D':
				robot.turnRight();
				newPath += TURNRIGHT;
				break;
			case 'W':
				int counter = 1;
				for(int a = j+1; a <robotPath.length();a++){
					if(robotPath.charAt(a) == 'W'){
						robot.goStraight();
						counter++;
						j++;
						if(counter>=12)
							break;
					}
					else
						break;
				}
				robot.goStraight();
				newPath += FORWARD + counter;
				break;
			case 'B':				
				newPath += TURNBACK;
				robot.turnBack();
				break;
				default:
			}
		}
		return newPath;
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
	
	public void setWayPoint(int[] newWP){
		this.wayPoint = newWP;
	}
	
	/*
	 * CALIBRATION AND SENSOR CHECKER
	 */
	private boolean calibrateRobot() {
		// TODO Auto-generated method stub
		int row = robot.getCurrentPosition()[0];
		int column = robot.getCurrentPosition()[1];
		
		/* NO NEED THIS, ROBOT CAN DO FRONT CALIBRATE
		if(robot.canFrontCalibrate(row, column, grid, robot.getRobotHead()))
		{	
			rpiMgr.sendInstruction3(AUDUINO + CALIBRATE);
			return true;
		}*/
		
		if(robot.canLeftCalibrate(row, column, grid, robot.getRobotHead())){
			rpiMgr.sendInstruction3(AUDUINO + TURNLEFT);
			//rpiMgr.sendInstruction3(AUDUINO + CALIBRATE);
			rpiMgr.sendInstruction3(AUDUINO + TURNRIGHT);
			return true;
		}
		else if (robot.canRightCalibrate(row, column, grid, robot.getRobotHead())){
			rpiMgr.sendInstruction3(AUDUINO + TURNRIGHT);	
			//rpiMgr.sendInstruction3(AUDUINO + CALIBRATE);	
			rpiMgr.sendInstruction3(AUDUINO + TURNLEFT);
			return true;
		}
		return false;
	}

	public boolean frontIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getRobotHead();
		if(direction == NORTH)
		{
			if(!robot.isMovable(x, y, grid, NORTH)){
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.isMovable(x, y, grid, SOUTH)){
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.isMovable(x, y, grid, EAST)){
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.isMovable(x, y, grid, WEST)){
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
	
	public boolean positionInsideArena(int row, int column){
		if(row >= 0 && row <= ROW-1 && column >= 0 && column <= COLUMN-1)
			return true;
		else
			return false;
	}
	
	private int runHowManyGrid2(int row, int col, String dir) {
		// TODO Auto-generated method stub
		int result = 0;
		int front = 0,left = 0,right = 0;
		int deg = robot.getRobotHead();
		boolean flag = false;
		switch(deg){
		case NORTH:
			for(result = 0; result < 6; result++){
				// check front first
				for(int i = -1; i <=1; i++){
					if(positionInsideArena((row-result), (col + i))){
						if(grid[row - result][col + i].getGridStatus()[0] == VISITED &&
								 grid[row - result][col + i].getGridStatus()[1] == OBSTACLE){
							front=-1;
							flag = true;
							break;
						}
						if(grid[row - result][col + i].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left	
				for(int i = -2;  i >-4;i--){
					if(positionInsideArena((row-1-result), (col + i))){
						if(grid[row - result][col + i].getGridStatus()[0] == VISITED &&
								grid[row - result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if (grid[row - 1 - result][col + i].getGridStatus()[0] == VISITED 
								&& grid[row - result][col + i].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if (grid[row - result][col + i].getGridStatus()[0] != VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				// check right
				int k = LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = 2; i < k;i++){

					if(positionInsideArena((row-1-result), (col + i))){
						if(grid[row - result][col + i].getGridStatus()[0] == VISITED &&
								grid[row - result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						
						if (grid[row - result][col + i].getGridStatus()[0] == VISITED){
							right-=1;
							flag = true;
							break;
						}
						if (grid[row - result][col + i].getGridStatus()[0] != VISITED){
							right-=1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}
				front++;
				left++;
				right++;
			}
			break;
		
		case SOUTH:
			for(result = 0; result < 6; result++){
				// check front first
				for(int i = -1; i <=1; i++){
					if(positionInsideArena((row+result), (col + i))){
						if (grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] == OBSTACLE){
							front=-1;
							flag = true;
							break;
						}
						if (grid[row+result][col + i].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left
				for(int i = 2;  i < 4;i++){
					if(positionInsideArena((row+result), (col + i))){
						if(grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if (grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if(grid[row+result][col + i].getGridStatus()[0] != VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				// check right
				int k = 0 - LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = -2; i > (k) ;i--){
					if(positionInsideArena((row+result), (col + i))){
						if(grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if (grid[row+result][col + i].getGridStatus()[0] != VISITED){
							right-=1;
							flag = true;
							break;
						}
						if(grid[row+result][col + i].getGridStatus()[0] == VISITED &&
							grid[row + result][col + i].getGridStatus()[1] != OBSTACLE){
							
							right-=1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}
				front++;
				left++;
				right++;
			}

			break;
		case EAST:
			for(result = 0; result < 6; result++){
				// check front
				for(int i = -1; i <=1; i++){
					if(positionInsideArena((row+i), (col+ result))){
						if (grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] == OBSTACLE){
							front=-1;
							flag = true;
							break;
						}
						if(grid[row+i][col+ result].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left
				for(int i = -2;  i > -4;i--){
					if(positionInsideArena((row+i), (col + result))){
						if(grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if(grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if(grid[row+i][col + result].getGridStatus()[0] !=VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				
				// check right
				int k = LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = 2; i < (k);i++){
					if(positionInsideArena((row+i), (col + result))){
						if(grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] == OBSTACLE){
							right-=1;
							flag = true;
							break;
						}
						if(grid[row+i][col + result].getGridStatus()[0] !=VISITED){
							right = right -1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}	
				left++;
				right++;
				front++;
			}
			break;
		
		case WEST:
			for(result = 0; result < 6; result++){
				// check front
				for(int i = -1; i <= 1; i++){
					if(positionInsideArena((row+i), (col - result))){
						if (grid[row+i][col - result].getGridStatus()[0] == VISITED &&
								grid[row+i][col - result].getGridStatus()[1] == OBSTACLE){
							front= -1;
							flag = true;
							break;
						}
						if(grid[row+i][col - result].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left
				for(int i = 2;  i < 4;i++){
					if(positionInsideArena((row+i), (col - result))){
						if(grid[row+i][col -result].getGridStatus()[0] == VISITED &&
								grid[row+i][col -result].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if(grid[row+i][col - result].getGridStatus()[0] == VISITED &&
								grid[row+i][col - result].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if(grid[row+i][col - result].getGridStatus()[0] !=VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				
				// check right
				int k = 0 - LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = -2; i > (k);i--){
					if(positionInsideArena((row+i), (col - result))){
						if(grid[row+i][col -result].getGridStatus()[0] == VISITED &&
								grid[row+i][col -result].getGridStatus()[1] == OBSTACLE){
							break;
						}
		
						if(grid[row+i][col - result].getGridStatus()[0] !=VISITED){
							right-=1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}
				right++;
				left++;
				front++;
			}
			break;
		}
		Arena.appendMessage("Left:" + left + " ; Front: " + front + " ; Right: " + right);
		int k = Math.min(front, left);
		k = Math.min(k, right);
		
		while(speedUpWillCrash(k)){
			k=k-1;
			if(k == 1){
				return 0;
			}
		}
		
		if(dir == "B"){
			k = decreaseIfNoObsatcle(k);
		}
		else{
			k = k-2;
		}
		

		if(k < 0)
			k = 0;
		
		return k;
	}
	
	private int runHowManyGrid(int row, int col, String dir) {
		// TODO Auto-generated method stub
		int result = 0;
		int front = 0,left = 0,right = 0;
		int deg = robot.getRobotHead();
		boolean flag = false;
		switch(deg){
		case NORTH:
			for(result = 0; result < 6; result++){
				// check front first
				for(int i = -1; i <=1; i++){
					if(positionInsideArena((row-result), (col + i))){
						if(grid[row - result][col + i].getGridStatus()[0] == VISITED &&
								 grid[row - result][col + i].getGridStatus()[1] == OBSTACLE){
							front=-1;
							flag = true;
							break;
						}
						if(grid[row - result][col + i].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left	
				for(int i = -2;  i >-4;i--){
					if(positionInsideArena((row-1-result), (col + i))){
						if(grid[row - result][col + i].getGridStatus()[0] == VISITED &&
								grid[row - result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if (grid[row - 1 - result][col + i].getGridStatus()[0] == VISITED 
								&& grid[row - result][col + i].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if (grid[row - result][col + i].getGridStatus()[0] != VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				// check right
				int k = LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = 2; i < (2+k);i++){

					if(positionInsideArena((row-1-result), (col + i))){
						if(grid[row - result][col + i].getGridStatus()[0] == VISITED &&
								grid[row - result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						
						if (grid[row - result][col + i].getGridStatus()[0] == VISITED){
							right-=1;
							flag = true;
							break;
						}
						if (grid[row - result][col + i].getGridStatus()[0] != VISITED){
							right-=1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}
				front++;
				left++;
				right++;
			}
			break;
		
		case SOUTH:
			for(result = 0; result < 6; result++){
				// check front first
				for(int i = -1; i <=1; i++){
					if(positionInsideArena((row+result), (col + i))){
						if (grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] == OBSTACLE){
							front=-1;
							flag = true;
							break;
						}
						if (grid[row+result][col + i].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left
				for(int i = 2;  i < 4;i++){
					if(positionInsideArena((row+result), (col + i))){
						if(grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if (grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if(grid[row+result][col + i].getGridStatus()[0] != VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				// check right
				int k = 0 - LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = -2; i > (-2+k) ;i--){
					if(positionInsideArena((row+result), (col + i))){
						if(grid[row+result][col + i].getGridStatus()[0] == VISITED &&
								grid[row+result][col + i].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if (grid[row+result][col + i].getGridStatus()[0] != VISITED){
							right-=1;
							flag = true;
							break;
						}
						if(grid[row+result][col + i].getGridStatus()[0] == VISITED &&
							grid[row + result][col + i].getGridStatus()[1] != OBSTACLE){
							
							right-=1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}
				front++;
				left++;
				right++;
			}

			break;
		case EAST:
			for(result = 0; result < 6; result++){
				// check front
				for(int i = -1; i <=1; i++){
					if(positionInsideArena((row+i), (col+ result))){
						if (grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] == OBSTACLE){
							front=-1;
							flag = true;
							break;
						}
						if(grid[row+i][col+ result].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left
				for(int i = -2;  i > -4;i--){
					if(positionInsideArena((row+i), (col + result))){
						if(grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if(grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if(grid[row+i][col + result].getGridStatus()[0] !=VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				
				// check right
				int k = LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = 2; i < (2+k);i++){
					if(positionInsideArena((row+i), (col + result))){
						if(grid[row+i][col + result].getGridStatus()[0] == VISITED &&
								grid[row+i][col + result].getGridStatus()[1] == OBSTACLE){
							right-=1;
							flag = true;
							break;
						}
						if(grid[row+i][col + result].getGridStatus()[0] !=VISITED){
							right = right -1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}	
				left++;
				right++;
				front++;
			}
			break;
		
		case WEST:
			for(result = 0; result < 6; result++){
				// check front
				for(int i = -1; i <= 1; i++){
					if(positionInsideArena((row+i), (col - result))){
						if (grid[row+i][col - result].getGridStatus()[0] == VISITED &&
								grid[row+i][col - result].getGridStatus()[1] == OBSTACLE){
							front= -1;
							flag = true;
							break;
						}
						if(grid[row+i][col - result].getGridStatus()[0] != VISITED){
							front=-1;
							flag = true;
							break;
						}
					}
					else{
						front=-1;
						flag = true;
						break;
					}
				}
				// check left
				for(int i = 2;  i < 4;i++){
					if(positionInsideArena((row+i), (col - result))){
						if(grid[row+i][col -result].getGridStatus()[0] == VISITED &&
								grid[row+i][col -result].getGridStatus()[1] == OBSTACLE){
							break;
						}
						
						if(grid[row+i][col - result].getGridStatus()[0] == VISITED &&
								grid[row+i][col - result].getGridStatus()[1] != OBSTACLE){
							left-=1;
							flag = true;
							break;
						}
						if(grid[row+i][col - result].getGridStatus()[0] !=VISITED){
							left-=1;
							flag = true;
							break;
						}
					}
				}
				
				// check right
				int k = 0 - LONGRANGE_SENSOR_MAXIMUM_DISTANCE;
				for(int i = -2; i > (-2+k);i--){
					if(positionInsideArena((row+i), (col - result))){
						if(grid[row+i][col -result].getGridStatus()[0] == VISITED &&
								grid[row+i][col -result].getGridStatus()[1] == OBSTACLE){
							break;
						}
		
						if(grid[row+i][col - result].getGridStatus()[0] !=VISITED){
							right-=1;
							flag = true;
							break;
						}
					}
				}
				if(flag){
					flag = false;
					break;
				}
				right++;
				left++;
				front++;
			}
			break;
		}
		Arena.appendMessage("Left:" + left + " ; Front: " + front + " ; Right: " + right);
		int k = Math.min(front, left);
		k = Math.min(k, right);
		
		while(speedUpWillCrash(k)){
			k=k-1;
			if(k == 1){
				return 0;
			}
		}
		
		if(dir == "B"){
			k = decreaseIfNoObsatcle(k);
		}
		else{
			k = k-2;
		}
		

		if(k < 0)
			k = 0;
		
		return k;
	}
	
	private int decreaseIfNoObsatcle(int k) {
		// TODO Auto-generated method stub
		int dir = robot.getRobotHead();
		int row = robot.getCurrentPosition()[0];
		int col = robot.getCurrentPosition()[1];
		
		switch(dir){
		case NORTH:
			if(positionInsideArena((row), (col - 2))){
				if(grid[row][col-2].getGridStatus()[0] == VISITED &&
						grid[row][col-2].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			if (positionInsideArena((row), (col + 2))){
				if(grid[row][col+2].getGridStatus()[0] == VISITED &&
						grid[row][col +2].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			break;
		case SOUTH:
			if(positionInsideArena((row), (col - 2))){
				if(grid[row][col-2].getGridStatus()[0] == VISITED &&
						grid[row][col-2].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			if (positionInsideArena((row), (col + 2))){
				if(grid[row][col+2].getGridStatus()[0] == VISITED &&
						grid[row][col +2].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			break;
		case EAST:
			if(positionInsideArena((row-2), (col))){
				if(grid[row-2][col].getGridStatus()[0] == VISITED &&
						grid[row-2][col].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			if(positionInsideArena((row+2), (col))){
				if(grid[row+2][col].getGridStatus()[0] == VISITED &&
						grid[row][col].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			break;
		case WEST:
			if(positionInsideArena((row-2), (col))){
				if(grid[row-2][col].getGridStatus()[0] == VISITED &&
						grid[row-2][col].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			if(positionInsideArena((row+2), (col))){
				if(grid[row+2][col].getGridStatus()[0] == VISITED &&
						grid[row][col].getGridStatus()[1] != OBSTACLE){
					return k-1;
				}
			}
			break;
		}
		return k + 2;
	}

	private boolean speedUpWillCrash(int k){
		int dir = robot.getRobotHead();
		int row = robot.getCurrentPosition()[0];
		int col = robot.getCurrentPosition()[1];

		switch(dir){
		case NORTH:
			row= row - k;
			if (positionInsideArena(row, col)){
				return false;
			}
			else return true;
			
		case SOUTH:
			row = row + k;
			if (positionInsideArena(row, col)){
				return false;
			}
			else return true;
		case EAST:
			col = col + k;
			if (positionInsideArena(row, col)){
				return false;
			}
			else return true;
		case WEST:
			col = col -k;
			if (positionInsideArena(row, col)){
				return false;
			}
			else return true;
		}
		return true;
	}

	/*
	 * STRING CONVERTER 
	 */	
	private String generateMsgToTablet() {
		String output = "";
		output = "explore:\"" + arenaInforToStringVisited() + "\"!";
		output += "grid:\"" + arenaInforToStringObstacle()+ "\"!";
		output += robotLocationToString(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getRobotHead()) + "!";
		return output;
	}

	private String generateMsgToTablet2(ArrayList<String> result) {		
		String output =  "@" + result.get(0) + "@" + result.get(1);
		return output;
	}
	
	private String robotLocationToString(int row, int column, int robotHead) {
		String output = "rPos:" + row + "," + column + "," + robotHead;
		return output;
	}

	private String arenaInforToStringObstacle() {
		// TODO Auto-generated method stub
		String hexResult = "";
		String binResult = "";
		
		for(int i = ROW-1; i >= 0; i--){
			for(int j = 0; j < COLUMN; j++){
				binResult = binResult + grid[i][j].getGridStatus()[1];
			}
		}
		//binResult = "11" + binResult + "11";
		hexResult = utility.binToHex(binResult);
		return hexResult;
	}
	
	private String arenaInforToStringVisited() {
		// TODO Auto-generated method stub
		String hexResult = "";
		String binResult = "";
		
		for(int i = ROW-1; i >= 0; i--){
			for(int j =0; j < COLUMN; j++){
				binResult = binResult + grid[i][j].getGridStatus()[0];
			}
		}
		
		//binResult = "11" + binResult + "11";
		hexResult = utility.binToHex(binResult);

		return hexResult;
	}
}

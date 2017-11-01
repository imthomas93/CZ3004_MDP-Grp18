package Model;

import java.util.concurrent.ExecutionException;

import Controllers.RealAlgorithmManager;

public class Robot implements RobotArenaProtocol{

	private int[] curPosition = new int[2];
	private int[] prevPosition = new int[2];
	private int robotHead;
	public static int[][] sensorDataRecord = new int[ROW][COLUMN];

	public Robot(int[] startPosition, int robotHead)
	{
		this.curPosition = startPosition;
		this.prevPosition[0] = startPosition[0];
		this.prevPosition[1] = startPosition[1];
		this.robotHead = robotHead;

		/* init sensor data
		 * Value 10 = no obstacle or NO_OBSTACLE record. 
		 * 1 is obstacle
		 * 0 is NO_OBSTACLE
		 * 5 means dont update anymore)
		 */
		for(int i = 0; i < ROW; i++)
			for(int j = 0; j< COLUMN; j++)
				sensorDataRecord[i][j] = 10;
	}

	public int[] getCurrentPosition(){
		return this.curPosition;
	}

	public void setCurrentPosition(int[] newPosition){
		this.curPosition[0] = newPosition[0];
		this.curPosition[1] = newPosition[1];
	}

	public int[] getPreviousPosition(){
		return this.prevPosition;
	}

	public void setPreviousPosition(int[] prevPosition){
		this.prevPosition[0] = prevPosition[0];
		this.prevPosition[1] = prevPosition[1];	
	}

	public int getRobotHead(){
		return robotHead;
	}

	public void setRobotHead(int robotHead){
		this.robotHead = robotHead;
	}

	public void goStraight(){
		prevPosition[0] = curPosition[0];
		prevPosition[1] = curPosition[1];
		switch (robotHead){
		case NORTH:
			curPosition[0] = curPosition[0] - 1;
			break;
		case SOUTH:
			curPosition[0] = curPosition[0] + 1;
			break;
		case EAST:
			curPosition[1] = curPosition[1] + 1;
			break;
		case WEST:
			curPosition[1] = curPosition[1] - 1;
			break;
		}
	}

	public void turnLeft()
	{
		switch (robotHead) {
		case NORTH:
			robotHead = WEST;
			break;
		case SOUTH:
			robotHead = EAST;
			break;
		case EAST:
			robotHead = NORTH;
			break;
		case WEST:
			robotHead = SOUTH;
			break;
		}
	}

	public void turnRight()
	{
		switch (robotHead) {
		case NORTH:
			robotHead = EAST;
			break;
		case SOUTH:
			robotHead = WEST;
			break;
		case EAST:
			robotHead = SOUTH;
			break;
		case WEST:
			robotHead = NORTH;
			break;
		}
	}

	public void turnBack()
	{
		switch (robotHead) {
		case NORTH:
			this.robotHead = SOUTH;
			break;
		case SOUTH:
			this.robotHead = NORTH;
			break;
		case EAST:
			this.robotHead = WEST;
			break;
		case WEST:
			this.robotHead = EAST;
			break;
		}
	}
	

	private static boolean notInsideArena(int row, int column) {
		if(row<= 0 || row>= ROW-1 || column <= 0 || column >= COLUMN-1)
			return true;
		else
			return false;
	}

	public boolean positionInsideArena(int row, int column)
	{
		if(row >= 0 && row <= ROW-1 && column >= 0 && column <= COLUMN-1)
			return true;
		else
			return false;
	}

	public static boolean isMovable(int row, int column, Grid[][] grid, int robotHead)
	{	
		switch (robotHead) {
		case NORTH:
			row--;
			break;
		case SOUTH:
			row++;
			break;
		case EAST:
			column++;
			break;
		case WEST:
			column--;
			break;
		}

		if(notInsideArena(row, column))
			return false;
		else{
			for(int i= -1; i < 2; i++)
				for(int j= -1; j < 2; j++)
					if(grid[row+i][column+j].getGridStatus()[1] == 1)
						return false;
		}
		return true;
	}

	public static boolean isMovableFP(int row, int column, Grid[][] grid, int robotHead)
	{	
		switch (robotHead) {
		case NORTH:
			row--;
			break;
		case SOUTH:
			row++;
			break;
		case EAST:
			column++;
			break;
		case WEST:
			column--;
			break;
		}

		if(notInsideArena(row, column))
			return false;
		else{
			for(int i=-1; i<2; i++)
				for(int j=-1; j<2; j++)
					if(grid[row+i][column+j].getGridStatus()[1] == 1 || grid[row+i][column+j].getGridStatus()[0] == 0)
						return false;
		}
		return true;
	}

	public boolean canFrontCalibrate(int row, int column, Grid[][] grid, int robotHead)
	{
		switch (robotHead) {

		case NORTH:
			row--;
			break;
		case SOUTH:
			row++;
			break;
		case EAST:
			column++;
			break;
		case WEST:
			column--;
			break;
		}
		if(notInsideArena(row, column))
			return true;
		else{
			switch (robotHead) {
			case NORTH:
				if(grid[row-1][column-1].getGridStatus()[1] == 1 && grid[row-1][column+1].getGridStatus()[1] == 1)
					return true; 
				break;
			case SOUTH:
				if(grid[row+1][column-1].getGridStatus()[1] == 1 && grid[row+1][column+1].getGridStatus()[1] == 1)
					return true; 
				break;
			case EAST:
				if(grid[row-1][column+1].getGridStatus()[1] == 1 && grid[row+1][column+1].getGridStatus()[1] == 1)
					return true; 
				break;
			case WEST:
				if(grid[row-1][column-1].getGridStatus()[1] == 1 && grid[row+1][column-1].getGridStatus()[1] == 1)
					return true; 
				break;
			}
		}
		return false;
	}

	public boolean canRightCalibrate(int row, int column, Grid[][] grid, int robotHead) 
	{
		switch (robotHead) {
		case NORTH:
			column++;
			break;
		case SOUTH:
			column--;
			break;
		case EAST:
			row++;
			break;
		case WEST:
			row--;
			break;
		}

		if(notInsideArena(row, column))
			return true;
		else
		{
			switch (robotHead) {
			case NORTH:
				if(grid[row+1][column+1].getGridStatus()[1] == 1 
				&& grid[row-1][column+1].getGridStatus()[1] == 1)
					return true;
				break;
			case SOUTH:
				if(grid[row+1][column-1].getGridStatus()[1] == 1 
				&& grid[row-1][column-1].getGridStatus()[1] == 1)
					return true; 		
				break;
			case EAST:
				if(grid[row+1][column+1].getGridStatus()[1] == 1 
				&& grid[row+1][column-1].getGridStatus()[1] == 1)
					return true; 
				break;
			case WEST:
				if(grid[row-1][column-1].getGridStatus()[1] == 1 && grid[row-1][column+1].getGridStatus()[1] == 1)
					return true;
				break;
			}
		}
		return false;
	}

	public boolean canLeftCalibrate(int row, int column, Grid[][] grid, int robotHead)
	{
		switch (robotHead){
		case NORTH:
			column--;
			break;
		case SOUTH:
			column++;
			break;
		case EAST:
			row--;
			break;	
		case WEST:
			row++;
			break;
		}
		if(notInsideArena(row, column))
			return true;
		else{
			switch (robotHead) {
			case NORTH:
				if(grid[row+1][column-1].getGridStatus()[1] == 1 && grid[row-1][column-1].getGridStatus()[1] == 1)
					return true; 
				break;
			case SOUTH:
				if(grid[row+1][column+1].getGridStatus()[1] == 1 && grid[row-1][column+1].getGridStatus()[1] == 1)
					return true; 
				break;
			case EAST:
				if(grid[row-1][column+1].getGridStatus()[1] == 1 && grid[row-1][column-1].getGridStatus()[1] == 1)
					return true; 
				break;
			case WEST:
				if(grid[row+1][column-1].getGridStatus()[1] == 1 && grid[row+1][column+1].getGridStatus()[1] == 1)
					return true; 
				break;
			}
		}
		return false;
	}

	/*
	 * Soft Simulation
	 */
	public void getSensorsData(Grid[][] grid)
	{
		this.getFrontSensor1Data(grid);
		this.getFrontSensor2Data(grid);
		this.getFrontSensor3Data(grid);
		this.getLeftSensorData(grid);
		this.getRightSensorData(grid);
	}

	private void getFrontSensor1Data(Grid[][] grid) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]-1)){
					if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]+1)){
					if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i)){
					if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i)){
					if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getFrontSensor2Data(Grid[][] grid) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1])){
					if(grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1])){
					if(grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0],curPosition[1]+2+i)){
					if(grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0],curPosition[1]-2-i)){
					if(grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getFrontSensor3Data(Grid[][] grid) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]+1)){
					if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]-1)){
					if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]+2+i)){
					if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			case WEST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]-2-i)){
					if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getLeftSensorData(Grid[][] grid) {
		boolean obstacleDetected = false;
		for (int i = 0; i < LONGRANGE_SENSOR_MAXIMUM_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]-2-i)){
					if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]+2+i)){
					if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]+1)){
					if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]-1)){
					if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getRightSensorData(Grid[][] grid) {
		boolean obstacleDetected = false;
		for (int i = 0; i < LONGRANGE_SENSOR_MAXIMUM_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i)){
					if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i)){
					if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]+1)){
					if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]-1)){
					if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	public void turnToNorth()
	{
		switch (this.robotHead) {
		case NORTH:
			break;
		case SOUTH:
			turnBack();
			break;
		case EAST:
			turnLeft();
			break;
		case WEST:
			turnRight();
			break;
		}
	}

	public String turnToReqDirection(int curo, int reqo){
		switch(curo){
		case Robot.EAST:
			switch(reqo){
			case Robot.EAST:
				return "";
			case Robot.WEST:
				return RobotArenaProtocol.TURNBACK;
			case Robot.SOUTH:
				return RobotArenaProtocol.TURNRIGHT;
			case Robot.NORTH:
				return RobotArenaProtocol.TURNLEFT;
			default:
				System.out.println("error: turntoreqDirection");
			}
			break;
		case Robot.WEST:
			switch(reqo){
			case Robot.EAST:
				return RobotArenaProtocol.TURNBACK;
			case Robot.WEST:
				return "";
			case Robot.SOUTH:
				return RobotArenaProtocol.TURNLEFT;
			case Robot.NORTH:
				return RobotArenaProtocol.TURNRIGHT;
			default:
				System.out.println("error: turntoreqDirection");
			}
			break;
		case Robot.SOUTH:
			switch(reqo){
			case Robot.EAST:
				return RobotArenaProtocol.TURNLEFT;
			case Robot.WEST:
				return RobotArenaProtocol.TURNRIGHT;
			case Robot.SOUTH:
				return "";
			case Robot.NORTH:
				return RobotArenaProtocol.TURNBACK;
			default:
				System.out.println("error: turntoreqDirection");
			}
			break;
		case Robot.NORTH:
			switch(reqo){
			case Robot.EAST:
				return RobotArenaProtocol.TURNRIGHT;
			case Robot.WEST:
				return RobotArenaProtocol.TURNLEFT;
			case Robot.SOUTH:
				return RobotArenaProtocol.TURNBACK;
			case Robot.NORTH:
				return "";
			default:
				System.out.println("error: turntoreqDirection");
			}
			break;
		default:
			System.out.println("error: turntoreqDirection");
		}
		return null;
	}

	public String turnString(int[] prevPos, int[] curPos, int curO){
		switch(curO){
		case Robot.EAST:
			if(prevPos[0] != curPos[0] && prevPos[1] == curPos[1]){
				if(prevPos[0] < curPos[0])	return RobotArenaProtocol.TURNRIGHT;
				else return RobotArenaProtocol.TURNLEFT;
			}
			return "";
		case Robot.WEST:
			if(prevPos[0] != curPos[0] && prevPos[1] == curPos[1]){
				if(prevPos[0] < curPos[0])	return RobotArenaProtocol.TURNLEFT;
				else return RobotArenaProtocol.TURNRIGHT;
			}
			return "";
		case Robot.SOUTH:
			if(prevPos[1] != curPos[1] && prevPos[0] == curPos[0]){
				if(prevPos[1] < curPos[1])	return RobotArenaProtocol.TURNLEFT;
				else return RobotArenaProtocol.TURNRIGHT;
			}
			return "";
		case Robot.NORTH:
			if(prevPos[1] != curPos[1] && prevPos[0] == curPos[0]){
				if(prevPos[1] < curPos[1])	return RobotArenaProtocol.TURNRIGHT;
				else return RobotArenaProtocol.TURNLEFT;
			}
			return "";
		default:
			System.out.println("error: turnString");
		}
		return "";
	}

	public int getDirAfterRightTurn(int cur0)
	{
		switch (cur0) {
		case EAST:
			return SOUTH;
		case SOUTH:
			return WEST;
		case WEST:
			return NORTH;
		case NORTH:
			return EAST;
		}
		return 0;
	}

	public int getDirAfterLeftTurn(int cur0)
	{
		switch (cur0) {
		case EAST:
			return NORTH;
		case SOUTH:
			return EAST;
		case WEST:
			return SOUTH;
		case NORTH:
			return WEST;
		}
		return 0;
	}

	//For real run without update grid cell 
	public void getSensorsData(Grid[][] grid, int[] distance)
	{
		// front
		// left sensor
		this.getLeftSensorData(grid, distance[0]);
		// left, mid, right
		this.getFrontSensor1Data(grid, distance[1]);
		this.getFrontSensor2Data(grid, distance[2]);
		this.getFrontSensor3Data(grid, distance[3]);
		// right sensor
		this.getRightSensorData(grid, distance[4], distance[5]);

	}

	private void getFrontLeftWithUpdate(Grid[][] grid, int obstacleDistance){
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				// check if the position is inside the arena
				if (positionInsideArena(curPosition[0]-2-i, curPosition[1]-1)){
				//	if((grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == VISITED && grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1] == OBSTACLE)
					//		|| grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED ){
						
						if(i == obstacleDistance){
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]-2-i, curPosition[1]-1)){
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
				//}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]+1)){
					//if((grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1] == OBSTACLE && grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[0] == VISITED) 
						//	|| grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED ){
						
						if(i == obstacleDistance){
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]+2+i, curPosition[1]+1)){
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
			//	}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i)){
				//	if((grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[0] == VISITED && grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1] == OBSTACLE)
					//		|| grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED ){

						if(i == obstacleDistance){
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]-1, curPosition[1]+2+i)){
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					//}
				}
				break;
				
			case WEST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i)){
					//if((grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[0] == VISITED && grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1] == OBSTACLE)
						//	|| grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED ){
						
						if(i == obstacleDistance){
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]+1, curPosition[1]-2-i)){
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}

				//}
				break;
			
			}
			if(obstacleDetected){
				break;
			}
		}
	}

	private boolean inStartOrGoal(int row, int col) {
		// TODO Auto-generated method stub
		if ((row == 0 || row == 1 || row == 2) && (col == 12 || col == 13 || col == 14)){
			return true;
		}
		
		if ((row == 17 || row == 18 || row == 19) && (col == 0 || col == 1 || col == 2)){
			return true;
		}
		return false;

	}

	private void getFrontSensor1Data(Grid[][] grid, int obstacleDistance) {
		boolean obstacleDetected = false;

		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]-1)){
					if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED){
						if(i == obstacleDistance){
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else{
						if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE){
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]+1)){
					if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance){
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else{
						if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE){
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i)){
					if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance){
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i)){
					if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						

					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getFrontMidWithUpdate(Grid[][] grid, int obstacleDistance){
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1])){
					//if((grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[0] == VISITED && grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[1] == OBSTACLE)
						//	|| grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[0] == NOT_VISITED ){
					
	
						if(i == obstacleDistance){
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]-2-i, curPosition[1])){
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}

				//}	
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1])){
					//if((grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[0] == VISITED && grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[1] == OBSTACLE)
						//	|| grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[0] == NOT_VISITED ){
						
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]+2+i, curPosition[1])){
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
						
				//}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0],curPosition[1]+2+i)){
					//if((grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[0] == VISITED && grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[1] == OBSTACLE)
						//	|| grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED ){
					
					
						if(i == obstacleDistance){
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0], curPosition[1]+2+i)){
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
						
				//}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0],curPosition[1]-2-i)){
					//if((grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[0] == VISITED && grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[1] == OBSTACLE)
						//	|| grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED ){
						
						if(i == obstacleDistance){
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0], curPosition[1]-2-i)){
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
				//}
				break;
		}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor2Data(Grid[][] grid, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1])){
					if(grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1])){
					if(grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0],curPosition[1]+2+i)){
					if(grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0],curPosition[1]-2-i)){
					if(grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getFrontRightWithUpdate(Grid [][] grid,  int obstacleDistance){
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]+1)){
					
					//if((grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[0] == VISITED && grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1] == OBSTACLE)
						//|| grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED ){
						
						if(i == obstacleDistance){
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
						if (inStartOrGoal(curPosition[0]-2-i, curPosition[1]+1)){
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
				//}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]-1)){
					//if((grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[0] == VISITED && grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1] == OBSTACLE)
						//|| grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED ){
					
						if(i == obstacleDistance){
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						if (inStartOrGoal(curPosition[0]+2+i, curPosition[1]-1)){
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
	
				//}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]+2+i)){
					//if((grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[0] == VISITED && grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1] == OBSTACLE)
						//|| grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED ){
						if(i == obstacleDistance){
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						if (inStartOrGoal(curPosition[0]+1, curPosition[1]+2+i)){
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}

			//}
				break;
				
			case WEST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]-2-i)){
					//if((grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[0] == VISITED && grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1] == OBSTACLE)
						//	|| grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED ){
					
						if(i == obstacleDistance){
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						if (inStartOrGoal(curPosition[0]-1, curPosition[1]-2-i)){
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
					}
				//}
				break;
			}
			if(obstacleDetected){
				break;
			}
		}		
	}
	
	private void getFrontSensor3Data(Grid[][] grid, int obstacleDistance) {

		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]+1)){
					if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]-1)){
					if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]+2+i)){
					if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			case WEST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]-2-i)){
					if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getLeftSensorData(Grid[][] grid, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]-2-i)){
					if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]+2+i)){
					if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]+1)){
					if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]-1)){
					if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}

	private void getRightSensorData(Grid[][] grid, int obstacleDistance, int obstcaleDistance2) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i)){
					if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-1][curPosition[1]+2+i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case SOUTH:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i)){
					if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+1][curPosition[1]-2-i].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case EAST:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]+1)){
					if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]+2+i][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;

			case WEST:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]-1)){
					if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
						}
						
					}
					else
					{
						if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected){
				break;
			}
		}
		
	
		if(!obstacleDetected){
			
			// overwrite second grid from long range if slip thru
			if (obstcaleDistance2 == 1){
				switch(this.robotHead){
				case NORTH:
					if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+1)){
						grid[curPosition[0]-1][curPosition[1]+2+1].setGridStatus(VISITED, OBSTACLE);
					}
					break;
				case SOUTH:
					if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-1)){
						grid[curPosition[0]+1][curPosition[1]-2-1].setGridStatus(VISITED, OBSTACLE);
					}
					break;
				case EAST:
					if(positionInsideArena(curPosition[0]+2+1,curPosition[1]+1)){
						grid[curPosition[0]+2+1][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
					}
					break;
				case WEST:
					if(positionInsideArena(curPosition[0]-2-1,curPosition[1]-1)){
						grid[curPosition[0]-2-1][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
					}
					break;
				}
				obstacleDetected = true;
			}
	
			for (int i1 = 2; i1 < LONGRANGE_SENSOR_MAXIMUM_DISTANCE; i1++) {	
				if (obstacleDetected){
					break;
				}
				switch (this.robotHead) {
				case NORTH:
					if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i1)){
						if(grid[curPosition[0]-1][curPosition[1]+2+i1].getGridStatus()[0] == NOT_VISITED)
						{
							if(i1 == obstcaleDistance2)
							{
								grid[curPosition[0]-1][curPosition[1]+2+i1].setGridStatus(VISITED, OBSTACLE);
								obstacleDetected = true;
							}
							else
							{
								grid[curPosition[0]-1][curPosition[1]+2+i1].setGridStatus(VISITED, NO_OBSTACLE);
							}
							
						}
						else
						{
							if(grid[curPosition[0]-1][curPosition[1]+2+i1].getGridStatus()[1]== OBSTACLE)
							{
								obstacleDetected = true;
							}
						}
					}
					break;

				case SOUTH:
					if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i1)){
						if(grid[curPosition[0]+1][curPosition[1]-2-i1].getGridStatus()[0] == NOT_VISITED)
						{
							if(i1 == obstcaleDistance2)
							{
								grid[curPosition[0]+1][curPosition[1]-2-i1].setGridStatus(VISITED, OBSTACLE);
								obstacleDetected = true;
							}
							else
							{
								grid[curPosition[0]+1][curPosition[1]-2-i1].setGridStatus(VISITED, NO_OBSTACLE);
							}
							
						}
						else
						{
							if(grid[curPosition[0]+1][curPosition[1]-2-i1].getGridStatus()[1]== OBSTACLE)
							{
								obstacleDetected = true;
							}
						}
					}
					break;

				case EAST:
					if(positionInsideArena(curPosition[0]+2+i1,curPosition[1]+1)){
						if(grid[curPosition[0]+2+i1][curPosition[1]+1].getGridStatus()[0] == NOT_VISITED)
						{
							if(i1 == obstcaleDistance2)
							{
								grid[curPosition[0]+2+i1][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
								obstacleDetected = true;
							}
							else
							{
								grid[curPosition[0]+2+i1][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
							}
							
						}
						else
						{
							if(grid[curPosition[0]+2+i1][curPosition[1]+1].getGridStatus()[1]== OBSTACLE)
							{
								obstacleDetected = true;
							}
						}
					}
					break;

				case WEST:
					if(positionInsideArena(curPosition[0]-2-i1,curPosition[1]-1)){
						if(grid[curPosition[0]-2-i1][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
						{
							if(i1 == obstcaleDistance2)
							{
								grid[curPosition[0]-2-i1][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
								obstacleDetected = true;
							}
							else
							{
								grid[curPosition[0]-2-i1][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
							}
							
						}
						else
						{
							if(grid[curPosition[0]-2-i1][curPosition[1]-1].getGridStatus()[1]== OBSTACLE)
							{
								obstacleDetected = true;
							}
						}
					}
					break;
				}
			}
		}

	}

	//For real run with update grid cell. Only use when update is required
	public void getSensorsDataWithUpdate(Grid[][] grid, int[] distance){
		//this.getLeftSensorDataWithUpdate(grid, distance[0]);
		this.getLeftSensorData(grid, distance[0]);
		this.getFrontLeftWithUpdate(grid, distance[1]);
		this.getFrontMidWithUpdate(grid, distance[2]);
		this.getFrontRightWithUpdate(grid, distance[3]);		
		this.getRightSensorData(grid, distance[4], distance[5]);
	}

}

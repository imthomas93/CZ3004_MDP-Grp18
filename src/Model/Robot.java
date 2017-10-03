package Model;

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
			robotHead = SOUTH;
			break;
		case SOUTH:
			robotHead = NORTH;
			break;
		case EAST:
			robotHead = WEST;
			break;
		case WEST:
			robotHead = EAST;
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
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
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
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
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
	
	public String getDirectionString() {
		switch (this.robotHead) {
		case NORTH:
			return "north";
		case SOUTH:
			return "south";
		case EAST:
			return "east";
		case WEST:
			return "west";
		default:
			return null;
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
		this.getRightSensorData(grid, distance[4]);
		
	}
	
	private void getFrontSensor1Data(Grid[][] grid, int obstacleDistance) {
		boolean obstacleDetected = false;
		
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]-1)){
					if(grid[curPosition[0]-2-i][curPosition[1]-1].getGridStatus()[0] == NOT_VISITED)
					{
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
		for (int i = 0; i < 3; i++) {
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
		
	private void getRightSensorData(Grid[][] grid, int obstacleDistance) {
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
			if(obstacleDetected)
				break;

		}
	}
	
	//For real run with update grid cell. Only use when update is required
	public void getSensorsDataWithUpdate(Grid[][] grid, int[] distance)
	{
		this.getFrontSensor1DataWithUpdate(grid, distance[2]);
		this.getFrontSensor2DataWithUpdate(grid, distance[1]);
		this.getFrontSensor3DataWithUpdate(grid, distance[0]);		
		this.getLeftSensorData(grid, distance[3]);
		this.getRightSensorData(grid, distance[4]);
	}
	
	private void getFrontSensor1DataWithUpdate(Grid[][] grid, int obstacleDistance) {
		boolean obstacleDetected = false;
		
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]-1)){
					if(sensorDataRecord[curPosition[0]-2-i][curPosition[1]-1] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]-1] = 1;//update sensor data record;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]-1] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]-2-i][curPosition[1]-1] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]-1] = 0;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]+1)){
					if(sensorDataRecord[curPosition[0]+2+i][curPosition[1]+1] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]+1] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]+1] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]+2+i][curPosition[1]+1] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]+1] = 0;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]+2+i)){
					if(sensorDataRecord[curPosition[0]-1][curPosition[1]+2+i] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]-1][curPosition[1]+2+i] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-1][curPosition[1]+2+i] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]-1][curPosition[1]+2+i] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-1][curPosition[1]+2+i] = 0;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]-2-i)){
					if(sensorDataRecord[curPosition[0]+1][curPosition[1]-2-i] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]+1][curPosition[1]-2-i] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+1][curPosition[1]-2-i] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]+1][curPosition[1]-2-i] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+1][curPosition[1]-2-i] = 0;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor2DataWithUpdate(Grid[][] grid, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1])){
					if(sensorDataRecord[curPosition[0]-2-i][curPosition[1]] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]] = 1;//update sensor data record;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]-2-i][curPosition[1]] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]] = 0;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1])){
					if(sensorDataRecord[curPosition[0]+2+i][curPosition[1]] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]+2+i][curPosition[1]] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]] = 0;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(curPosition[0],curPosition[1]+2+i)){
					if(sensorDataRecord[curPosition[0]][curPosition[1]+2+i] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]][curPosition[1]+2+i] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]][curPosition[1]+2+i] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]][curPosition[1]+2+i] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]][curPosition[1]+2+i] = 0;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(curPosition[0],curPosition[1]-2-i)){
					if(sensorDataRecord[curPosition[0]][curPosition[1]-2-i] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]][curPosition[1]-2-i] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]][curPosition[1]-2-i] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]][curPosition[1]-2-i] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]][curPosition[1]-2-i] = 0;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor3DataWithUpdate(Grid[][] grid, int obstacleDistance) {

		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.robotHead) {
			case NORTH:
				if(positionInsideArena(curPosition[0]-2-i,curPosition[1]+1)){
					if(sensorDataRecord[curPosition[0]-2-i][curPosition[1]+1] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]+1] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]+1] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]-2-i][curPosition[1]+1] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]-2-i][curPosition[1]+1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-2-i][curPosition[1]+1] = 0;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(curPosition[0]+2+i,curPosition[1]-1)){
					if(sensorDataRecord[curPosition[0]+2+i][curPosition[1]-1] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]-1] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]-1] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]+2+i][curPosition[1]-1] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]+2+i][curPosition[1]-1].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+2+i][curPosition[1]-1] = 0;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(curPosition[0]+1,curPosition[1]+2+i)){
					if(sensorDataRecord[curPosition[0]+1][curPosition[1]+2+i] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]+1][curPosition[1]+2+i] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+1][curPosition[1]+2+i] = 0;
						}
					}
					else if (sensorDataRecord[curPosition[0]+1][curPosition[1]+2+i] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]+1][curPosition[1]+2+i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]+1][curPosition[1]+2+i] = 0;
						}
					}
				}
				break;
			case WEST:
				if(positionInsideArena(curPosition[0]-1,curPosition[1]-2-i)){
					if(sensorDataRecord[curPosition[0]-1][curPosition[1]-2-i] == 10)
					{
						if(i == obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, OBSTACLE);
							sensorDataRecord[curPosition[0]-1][curPosition[1]-2-i] = 1;
							obstacleDetected = true;
						}
						else
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-1][curPosition[1]-2-i] = 0;
						}
					}
					else if(sensorDataRecord[curPosition[0]-1][curPosition[1]-2-i] == 1)
					{
						if(i != obstacleDistance)
						{
							grid[curPosition[0]-1][curPosition[1]-2-i].setGridStatus(VISITED, NO_OBSTACLE);
							sensorDataRecord[curPosition[0]-1][curPosition[1]-2-i] = 0;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
		
}

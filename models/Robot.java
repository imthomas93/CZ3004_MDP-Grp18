package cz3004MDP.models;

import cz3004MDP.services.RpiCommProtocol;

public class Robot implements ArenaRobot{
	
	private int[] currentPos = new int [2];
	private int[] prevPos = new int[2];
	
	private int direction;
	
	private static int [][] sensorDB = new int[ROW][COLUMN];
	
	public Robot(int[] startZone, int direction){
		this.currentPos = startZone;
		this.prevPos[0] = startZone[0];
		this.prevPos[1] = startZone[1];
		this.direction = direction;
		
		for(int i = 0; i < ROW; i++){
			for (int j =0; j < COLUMN; j++)
				// NEED TO FIND OUT WHAT IS 10
				sensorDB[i][j] = 10;
		}
	}
	
	public int[] getCurrentPos(){
		return this.currentPos;
	}
	
	public int[] getPrevPos(){
		return this.prevPos;
	}
	
	public int getDirection(){
		return this.direction;
	}
	
	public void setCurrentPos(int[] newCurrentPostion){
		// LOOK UP AT THIS LOGIC AGAIN. NEED TO BE TESTED
		setPrevPos(this.currentPos);
		this.currentPos = newCurrentPostion;
	}
	
	public void setPrevPos(int[] currentPosition){
		this.prevPos = currentPosition;
	}
	
	public void setDirection(int newDirection){
		this.direction = newDirection;
	}
	
	// turning at certain direction
	public void goStraight(){
		// move current position to previous position
		this.prevPos[0] = this.currentPos[0];
		this.prevPos[1] = this.currentPos[1];
		
		/*
		 * If the 4 cells are (a,b), 1 (a,b+1), (a+1, b) and 23 (a+1,b+1) the center is 4
		 * ( (a+1)x10, (b+1)x10 )
		 */
		switch(direction){
		case EAST:
			currentPos[1] += 1;
			break;
		case SOUTH:
			currentPos[0] += 1;
			break;
		case WEST:
			currentPos[1] -= 1;
			break;
		case NORTH:
			currentPos[0] -= 1;
			break;
		}
	}

	public void leftTurn(){
		switch(direction){
		case EAST:
			direction = NORTH;
			break;
		case SOUTH:
			direction = EAST;
			break;
		case WEST:
			direction = SOUTH;
			break;
		case NORTH:
			direction = WEST;
			break;
		}
	}
	
	public void rightTurn(){
		switch(direction){
		case EAST:
			direction = SOUTH;
			break;
		case SOUTH:
			direction = WEST;
			break;
		case WEST:
			direction = NORTH;
			break;
		case NORTH:
			direction = EAST;
			break;
		}
	}
	
	public void goBack(){
		switch(direction){
		case EAST:
			direction = WEST;
			break;
		case SOUTH:
			direction = NORTH;
			break;
		case WEST:
			direction = EAST;
			break;
		case NORTH:
			direction = SOUTH;
			break;
		}
	}
		
	// this method is to convert the direction to string line
	public String getStringDirection(){
		switch(direction){
		case EAST:
			return "east";
		case SOUTH:
			return "south";
		case WEST:
			return "west";
		case NORTH:
			return "north";
		default:
			return null;
		}
	}
	
	// callibrate robot to face north
	public void calibrateToNorth(){
		switch(this.direction){
		case SOUTH:
			goBack();
		case EAST:
			leftTurn();
		case WEST:
			rightTurn();
		default:
			// already at north
			break;
		}
	}
	
	public boolean isInsideArena(int row, int column){
		// within row between 0 - 19
		if (row >=0 && row <= ArenaRobot.ROW-1){
			// within column between 0 - 19
			if (column >=0 && column<=ArenaRobot.COLUMN-1)
				return true;
			else
				return false;
		}
		return false;
	}
	
	public boolean isMovable(int row, int column, Grid[][] grid, int direction){
		switch(direction){
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
		if(!isInsideArena(row, column)){
			return false;
		}
		else{
			for(int i=-1;i<2;i++){
				for(int j=-1;j<2;j++){
					if(grid[row+i][column+j].isClearGrid() || !(grid[row+i][column+j].isVisited()))
						return false;
				}
			}
		}
		return true;
	}
	
	public boolean isRightCalibrated(int row, int column, Grid[][] grid, int direction){
		switch(direction){
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
		if(!isInsideArena(row, column)){
			return false;
		}
		else{
			switch(direction){
			case NORTH:
				if(grid[row+1][column+1].isClearGrid() && grid[row-1][column+1].isClearGrid())
					return true;
				break;
			case SOUTH:
				if(grid[row+1][column-1].isClearGrid() && grid[row-1][column-1].isClearGrid())
					return true;
				break;
			case EAST:
				if(grid[row+1][column+1].isClearGrid() && grid[row+1][column-1].isClearGrid())
					return true;
				break;
			case WEST:
				if(grid[row-1][column-1].isClearGrid() && grid[row-1][column+1].isClearGrid())
					return true;
				break;
			}
		}
		return false;
	}
	
	public boolean isLeftCalibrated(int row, int column, Grid[][] grid, int direction){
		switch(direction){
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
		if(!isInsideArena(row, column)){
			return false;
		}
		else{
			switch(direction){
			case NORTH:
				if(grid[row+1][column-1].isClearGrid() && grid[row-1][column-1].isClearGrid())
					return true;
				break;
			case SOUTH:
				if(grid[row+1][column+1].isClearGrid() && grid[row-1][column+1].isClearGrid())
					return true;
				break;
			case EAST:
				if(grid[row-1][column+1].isClearGrid() && grid[row-1][column-1].isClearGrid())
					return true;
				break;
			case WEST:
				if(grid[row+1][column-1].isClearGrid() && grid[row+1][column+1].isClearGrid())
					return true;
				break;
			}
		}
		return false;
	}
	
	public boolean isFrontCalibrated(int row, int column, Grid[][] grid, int direction){
		switch(direction){
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
		if(!isInsideArena(row, column)){
			return false;
		}
		else{
			switch(direction){
			case NORTH:
				if(grid[row-1][column-1].isClearGrid() && grid[row-1][column+1].isClearGrid())
					return true;
				break;
			case SOUTH:
				if(grid[row+1][column-1].isClearGrid() && grid[row+1][column+1].isClearGrid())
					return true;
				break;
			case EAST:
				if(grid[row-1][column+1].isClearGrid() && grid[row+1][column+1].isClearGrid())
					return true;
				break;
			case WEST:
				if(grid[row-1][column-1].isClearGrid() && grid[row+1][column-1].isClearGrid())
					return true;
				break;
			}
		}
		return false;
	}

	// This area is for simulation
	public void getSensorData(Grid[][] grid){
		this.getFrontSensor1(grid);
		this.getFrontSensor2(grid);
		this.getFrontSensor3(grid);
		this.getLeftSensor(grid);
		this.getRightSensor(grid);
	}
	
	// front left
	private void getFrontSensor1(Grid[][] grid) {
		// TODO Auto-generated method stub
		boolean isObstacle = false;
		for(int i =0; i < SHORT_RANGE; i++){
			switch(this.direction){
			case NORTH:
				if(isInsideArena(currentPos[0]-2-i, currentPos[1]-1)){
					if(!grid[currentPos[0]-2-i][currentPos[1]-1].isVisited()){
						// if not visited
						if(grid[currentPos[0]-2-i][currentPos[1]-1].isObstacle()){
							grid[currentPos[0]-2-i][currentPos[1]-1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-2-i][currentPos[1]-1].setVisited(true);
							grid[currentPos[0]-2-i][currentPos[1]-1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-2-i][currentPos[1]-1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case SOUTH:
				if(isInsideArena(currentPos[0]+2+i, currentPos[1]+1)){
					if(!grid[currentPos[0]+2+i][currentPos[1]+1].isVisited()){
						// if not visited
						if(grid[currentPos[0]+2+i][currentPos[1]+1].isObstacle()){
							grid[currentPos[0]+2+i][currentPos[1]+1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+2+i][currentPos[1]+1].setVisited(true);
							grid[currentPos[0]+2+i][currentPos[1]+1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+2+i][currentPos[1]+1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case EAST:
				if(isInsideArena(currentPos[0]-1, currentPos[1]+2+i)){
					if(!grid[currentPos[0]-1][currentPos[1]+2+i].isVisited()){
						// if not visited
						if(grid[currentPos[0]-1][currentPos[1]+2+i].isObstacle()){
							grid[currentPos[0]-1][currentPos[1]+2+i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-1][currentPos[1]+2+i].setVisited(true);
							grid[currentPos[0]-1][currentPos[1]+2+i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-1][currentPos[1]+2+i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case WEST:
				if(isInsideArena(currentPos[0]+1, currentPos[1]-2-i)){
					if(!grid[currentPos[0]+1][currentPos[1]-2-i].isVisited()){
						// if not visited
						if(grid[currentPos[0]+1][currentPos[1]-2-i].isObstacle()){
							grid[currentPos[0]+1][currentPos[1]-2-i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+1][currentPos[1]-2-i].setVisited(true);
							grid[currentPos[0]+1][currentPos[1]-2-i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+1][currentPos[1]-2-i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;			
			}
			if(isObstacle)
				break;
		}
	}

	// front mid
	private void getFrontSensor2(Grid[][] grid) {
		// TODO Auto-generated method stub
		boolean isObstacle = false;
		for(int i =0; i < SHORT_RANGE; i++){
			switch(this.direction){
			case NORTH:
				if(isInsideArena(currentPos[0]-2-i, currentPos[1])){
					if(!grid[currentPos[0]-2-i][currentPos[1]].isVisited()){
						// if not visited
						if(grid[currentPos[0]-2-i][currentPos[1]].isObstacle()){
							grid[currentPos[0]-2-i][currentPos[1]].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-2-i][currentPos[1]].setVisited(true);
							grid[currentPos[0]-2-i][currentPos[1]].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-2-i][currentPos[1]].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case SOUTH:
				if(isInsideArena(currentPos[0]+2+i, currentPos[1])){
					if(!grid[currentPos[0]+2+i][currentPos[1]].isVisited()){
						// if not visited
						if(grid[currentPos[0]+2+i][currentPos[1]].isObstacle()){
							grid[currentPos[0]+2+i][currentPos[1]].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+2+i][currentPos[1]].setVisited(true);
							grid[currentPos[0]+2+i][currentPos[1]].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+2+i][currentPos[1]].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case EAST:
				if(isInsideArena(currentPos[0], currentPos[1]+2+i)){
					if(!grid[currentPos[0]][currentPos[1]+2+i].isVisited()){
						// if not visited
						if(grid[currentPos[0]][currentPos[1]+2+i].isObstacle()){
							grid[currentPos[0]][currentPos[1]+2+i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]][currentPos[1]+2+i].setVisited(true);
							grid[currentPos[0]][currentPos[1]+2+i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]][currentPos[1]+2+i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case WEST:
				if(isInsideArena(currentPos[0], currentPos[1]-2-i)){
					if(!grid[currentPos[0]][currentPos[1]-2-i].isVisited()){
						// if not visited
						if(grid[currentPos[0]][currentPos[1]-2-i].isObstacle()){
							grid[currentPos[0]][currentPos[1]-2-i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]][currentPos[1]-2-i].setVisited(true);
							grid[currentPos[0]][currentPos[1]-2-i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]][currentPos[1]-2-i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;			
			}
			if(isObstacle)
				break;
		}
	}

	// front right
	private void getFrontSensor3(Grid[][] grid) {
		// TODO Auto-generated method stub
		boolean isObstacle = false;
		for(int i =0; i < SHORT_RANGE; i++){
			switch(this.direction){
			case NORTH:
				if(isInsideArena(currentPos[0]-2-i, currentPos[1]+1)){
					if(!grid[currentPos[0]-2-i][currentPos[1]+1].isVisited()){
						// if not visited
						if(grid[currentPos[0]-2-i][currentPos[1]+1].isObstacle()){
							grid[currentPos[0]-2-i][currentPos[1]+1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-2-i][currentPos[1]+1].setVisited(true);
							grid[currentPos[0]-2-i][currentPos[1]+1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-2-i][currentPos[1]+1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case SOUTH:
				if(isInsideArena(currentPos[0]+2+i, currentPos[1]-1)){
					if(!grid[currentPos[0]+2+i][currentPos[1]-1].isVisited()){
						// if not visited
						if(grid[currentPos[0]+2+i][currentPos[1]-1].isObstacle()){
							grid[currentPos[0]+2+i][currentPos[1]-1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+2+i][currentPos[1]-1].setVisited(true);
							grid[currentPos[0]+2+i][currentPos[1]-1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+2+i][currentPos[1]-1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case EAST:
				if(isInsideArena(currentPos[0]+1, currentPos[1]+2+i)){
					if(!grid[currentPos[0]+1][currentPos[1]+2+i].isVisited()){
						// if not visited
						if(grid[currentPos[0]+1][currentPos[1]+2+i].isObstacle()){
							grid[currentPos[0]+1][currentPos[1]+2+i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+1][currentPos[1]+2+i].setVisited(true);
							grid[currentPos[0]+1][currentPos[1]+2+i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+1][currentPos[1]+2+i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case WEST:
				if(isInsideArena(currentPos[0]-1, currentPos[1]-2-i)){
					if(!grid[currentPos[0]-1][currentPos[1]-2-i].isVisited()){
						// if not visited
						if(grid[currentPos[0]-1][currentPos[1]-2-i].isObstacle()){
							grid[currentPos[0]-1][currentPos[1]-2-i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-1][currentPos[1]-2-i].setVisited(true);
							grid[currentPos[0]-1][currentPos[1]-2-i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-1][currentPos[1]-2-i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;		
			}
			if(isObstacle)
				break;
		}
	}

	private void getRightSensor(Grid[][] grid) {
		// TODO Auto-generated method stub
		boolean isObstacle = false;
		for(int i =0; i < SHORT_RANGE; i++){
			switch(this.direction){
			case NORTH:
				if(isInsideArena(currentPos[0]-1, currentPos[1]+2-i)){
					if(!grid[currentPos[0]-1][currentPos[1]+2-i].isVisited()){
						// if not visited
						if(grid[currentPos[0]-1][currentPos[1]+2-i].isObstacle()){
							grid[currentPos[0]-1][currentPos[1]+2-i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-1][currentPos[1]+2-i].setVisited(true);
							grid[currentPos[0]-1][currentPos[1]+2-i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-1][currentPos[1]+2-i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case SOUTH:
				if(isInsideArena(currentPos[0]+1, currentPos[1]-2+i)){
					if(!grid[currentPos[0]+1][currentPos[1]-2+i].isVisited()){
						// if not visited
						if(grid[currentPos[0]+1][currentPos[1]-2+i].isObstacle()){
							grid[currentPos[0]+1][currentPos[1]-2+i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+1][currentPos[1]-2+i].setVisited(true);
							grid[currentPos[0]+1][currentPos[1]-2+i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+1][currentPos[1]-2+i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case EAST:
				if(isInsideArena(currentPos[0]+2-i, currentPos[1]+1)){
					if(!grid[currentPos[0]+2][currentPos[1]+1].isVisited()){
						// if not visited
						if(grid[currentPos[0]+2][currentPos[1]+1].isObstacle()){
							grid[currentPos[0]+2][currentPos[1]+1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+2][currentPos[1]+1].setVisited(true);
							grid[currentPos[0]+2][currentPos[1]+1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+2][currentPos[1]+1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case WEST:
				if(isInsideArena(currentPos[0]-2+i, currentPos[1]-1)){
					if(!grid[currentPos[0]-2+i][currentPos[1]-1].isVisited()){
						// if not visited
						if(grid[currentPos[0]-2+i][currentPos[1]-1].isObstacle()){
							grid[currentPos[0]-2+i][currentPos[1]-1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-2+i][currentPos[1]-1].setVisited(true);
							grid[currentPos[0]-2+i][currentPos[1]-1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-2+i][currentPos[1]-1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;			
			}
			if(isObstacle)
				break;
		}
	}

	private void getLeftSensor(Grid[][] grid) {
		// TODO Auto-generated method stub
		boolean isObstacle = false;
		for(int i =0; i < SHORT_RANGE; i++){
			switch(this.direction){
			case NORTH:
				if(isInsideArena(currentPos[0]-1, currentPos[1]-2-i)){
					if(!grid[currentPos[0]-1][currentPos[1]-2-i].isVisited()){
						// if not visited
						if(grid[currentPos[0]-1][currentPos[1]-2-i].isObstacle()){
							grid[currentPos[0]-1][currentPos[1]-2-i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-1][currentPos[1]-2-i].setVisited(true);
							grid[currentPos[0]-1][currentPos[1]-2-i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-1][currentPos[1]-2-i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case SOUTH:
				if(isInsideArena(currentPos[0]+1, currentPos[1]+2+i)){
					if(!grid[currentPos[0]+1][currentPos[1]+2+i].isVisited()){
						// if not visited
						if(grid[currentPos[0]+1][currentPos[1]+2+i].isObstacle()){
							grid[currentPos[0]+1][currentPos[1]+2+i].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+1][currentPos[1]+2+i].setVisited(true);
							grid[currentPos[0]+1][currentPos[1]+2+i].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+1][currentPos[1]+2+i].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case EAST:
				if(isInsideArena(currentPos[0]-2-i, currentPos[1]+1)){
					if(!grid[currentPos[0]-2][currentPos[1]+1].isVisited()){
						// if not visited
						if(grid[currentPos[0]-2][currentPos[1]+1].isObstacle()){
							grid[currentPos[0]-2][currentPos[1]+1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]-2][currentPos[1]+1].setVisited(true);
							grid[currentPos[0]-2][currentPos[1]+1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]-2][currentPos[1]+1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;
			case WEST:
				if(isInsideArena(currentPos[0]+2+i, currentPos[1]-1)){
					if(!grid[currentPos[0]+2+i][currentPos[1]-1].isVisited()){
						// if not visited
						if(grid[currentPos[0]+2+i][currentPos[1]-1].isObstacle()){
							grid[currentPos[0]+2+i][currentPos[1]-1].setObstacle(true);
							isObstacle = true;
						}
						else{
							grid[currentPos[0]+2+i][currentPos[1]-1].setVisited(true);
							grid[currentPos[0]+2+i][currentPos[1]-1].setClearGrid(true);
						}
					}else{
						// alrady visited
						if(grid[currentPos[0]+2+i][currentPos[1]-1].isObstacle()){
							isObstacle = true;
						}
					}
				}
				break;			
			}
			if(isObstacle)
				break;
		}
	}

	// turn robot to given direction for RPI
	public String turnReqDirection(int curDeg, int reqDeg){
		switch(curDeg){
		case Robot.NORTH:
			switch(reqDeg){
			case Robot.EAST:
				return RpiCommProtocol.RIGHTTURN;
			case Robot.WEST:
				return RpiCommProtocol.LEFTTURN;
			case Robot.SOUTH:
				return RpiCommProtocol.GOBACK;
			case Robot.NORTH:
				return "";
			default:
				System.out.println("error: turning to required direction");
			}
			break;
			
		case Robot.SOUTH:
			switch(reqDeg){
			case Robot.EAST:
				return RpiCommProtocol.LEFTTURN;
			case Robot.WEST:
				return RpiCommProtocol.RIGHTTURN;
			case Robot.SOUTH:
				return "";
			case Robot.NORTH:
				return RpiCommProtocol.GOBACK;
			default:
				System.out.println("error: turning to required direction");
			}
			break;
			
			case Robot.EAST:
				switch(reqDeg){
				case Robot.EAST:
					return "";
				case Robot.WEST:
					return RpiCommProtocol.GOBACK;
				case Robot.SOUTH:
					return RpiCommProtocol.RIGHTTURN;
				case Robot.NORTH:
					return RpiCommProtocol.LEFTTURN;
				default:
					System.out.println("error: turning to required direction");
				}
				break;
				
			case Robot.WEST:
				switch(reqDeg){
				case Robot.EAST:
					return RpiCommProtocol.GOBACK;
				case Robot.WEST:
					return "";
				case Robot.SOUTH:
					return RpiCommProtocol.LEFTTURN;
				case Robot.NORTH:
					return RpiCommProtocol.RIGHTTURN;
				default:
					System.out.println("error: turning to required direction");
				}
				break;

			default:
				System.out.println("error: turning to required direction");
		}
		return null;
	}

	public String turnString(int[] prevPos, int[] curPos, int curDeg){
		switch(curDeg){
		case Robot.NORTH:
			if(prevPos[1] != curPos[1] && prevPos[0] == curPos[0]){
				if(prevPos[1] < curPos[1])	
					return RpiCommProtocol.RIGHTTURN;
				else 
					return RpiCommProtocol.LEFTTURN;
			}
			else
				return "";
		case Robot.SOUTH:
			if(prevPos[1] != curPos[1] && prevPos[0] == curPos[0]){
				if(prevPos[1] < curPos[1])	
					return RpiCommProtocol.LEFTTURN;
				else 
					return RpiCommProtocol.RIGHTTURN;
			}
			else
				return "";
		case Robot.EAST:
			if(prevPos[0] != curPos[0] && prevPos[1] == curPos[1]){
				if(prevPos[0] < curPos[0])	
					return RpiCommProtocol.RIGHTTURN;
				else 
					return RpiCommProtocol.LEFTTURN;
			}
			else
				return "";
		case Robot.WEST:
			if(prevPos[0] != curPos[0] && prevPos[1] == curPos[1]){
				if(prevPos[0] < curPos[0])	
					return RpiCommProtocol.LEFTTURN;
				else 
					return RpiCommProtocol.RIGHTTURN;
			}
			else
				return "";
			
		default:
			System.out.println("error: turn string");
		}
		return "";
	}
	
	public int getDirAftRightTurn(int curDeg){
		switch(curDeg){
		case NORTH:
			return EAST;
		case SOUTH:
			return WEST;
		case EAST:
			return SOUTH;
		case WEST:
			return NORTH;
		}
		return 0;
	}

	public int getDirAftLeftTurn(int curDeg){
		switch(curDeg){
		case NORTH:
			return WEST;
		case SOUTH:
			return EAST;
		case EAST:
			return NORTH;
		case WEST:
			return SOUTH;
		}
		return 0;
	}
}

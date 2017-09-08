package cz3004MDP.models;

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
}

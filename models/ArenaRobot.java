package cz3004MDP.models;

public interface ArenaRobot {

	// ROBOT DIRECTION
	static final int NORTH = 0;
	static final int EAST = 90;
	static final int SOUTH = 180;
	static final int WEST = 270;
	
	// ARENA SIZE
	static final int ROW = 20;
	static final int COLUMN = 15;
	
	// SENSOR RANGE
	static final int SHORT_RANGE = 2;
	static final int LONG_RANGE = 4;
	
	// START & END POSITION
	static final int[] STARTPOSITION = new int[]{18,1};
	static final int[] GOALPOSITION = new int[]{1,13};
	
	// FILE NAME
	static final String FILENAME1 = "PreDesign_Arena.txt";
	static final String FILENAME2 = "Explored_Arena.txt";

	
}

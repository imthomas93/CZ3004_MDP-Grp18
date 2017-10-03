package Model;

import java.awt.Color;

public interface RobotArenaProtocol{
	// ROBOT DIRECTION
	static final int NORTH = 0;
	static final int SOUTH = 180;
	static final int EAST = 90;
	static final int WEST = 270;
	
	// ARENA DIMENSION
	static final int ROW = 20;
	static final int COLUMN = 15;
	
	// START & GOAL ZONE
	static final int[] STARTPOSITION = new int[]{18,1};
	static final int[] GOALPOSITION = new int[]{1,13};
	
	// FILE IO
	static final String FILENAME = "Load_Arena.txt";
	static final String FILENAME2 = "Explored_Result.txt";
	
	 // ARENA DATA
	static final int OBSTACLE = 1;
	static final int NO_OBSTACLE = 0;
	static final int VISITED = 1;
	static final int NOT_VISITED = 0;
	static final int MAX_COST = 99999;
	
	// SENSOR DETAIlS
	static final int SHROTRANGE_SENSOR_DISTANCE = 2;
	static final int LONGRANGE_SENSOR_MAXIMUM_DISTANCE = 5;
	
	// ARENA UI COLOR CODE
	static final Color STARTZONECOLOR = Color.PINK;
	static final Color GOALZONECOLOR = Color.ORANGE;
	static final Color ROBOT_COLOR = Color.BLUE;
	static final Color ROBOTDIRECTION_COLOR = Color.LIGHT_GRAY;
	static final Color UNVISITED_COLOR = Color.BLACK;
	static final Color EMPTY_COLOR = Color.WHITE;
	static final Color WAYPOINT_COLOR = Color.CYAN;
	static final Color OBSTACLE_COLOR = Color.RED;
	static final Color CELLBORDER_COLOR = Color.GRAY;
	
	// Rpi Comm Protocol
	 static final String STRAIGHTMOVE = "W";
	 static final String STRAIGHTMOVE1 = "W1";
	 static final String TURNLEFT = "A";
	 static final String TURNRIGHT = "D";
	 static final String TURNBACK = "S";
	 static final String SCANARENA = "S";
	 static final String CALIBRATE = "C";		
	 static final String TABLET = "T";
	 static final String AUDUINO = "A";
	 static final String RPI = "R";		
	
}

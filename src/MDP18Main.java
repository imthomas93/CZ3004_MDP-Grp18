import Model.Arena;
import Model.RobotArenaProtocol;
import Model.Robot;

public class MDP18Main{
	private static int [] startingPosition;
	private static Robot robot;
	
	public static void main(String[] args){
		// Preload the start position for simulation purposes
		startingPosition = RobotArenaProtocol.STARTPOSITION;
		
		// Preload the robot direction with the start position
		robot = new Robot(startingPosition, RobotArenaProtocol.NORTH);
		
		// Load the UI Arena
		new Arena(robot);
	}
}

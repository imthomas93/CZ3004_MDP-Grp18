package cz3004MDP.services;

public interface RPiServiceInterface {

	int callibrate();
	
	int moveForward(int steps);
	
	// 1 for left
	// 2 for right
	int turn(int direction);
	
	int turnDegree(double degree);
	
	int moveDistance(double distance);

}

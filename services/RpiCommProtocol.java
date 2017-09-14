package cz3004MDP.services;

public interface RpiCommProtocol {
	
	static final String MOVESTRAIGHT = "M";
	static final String MOVEFORWARD = "F";
	static final String LEFTTURN = "L";
	static final String RIGHTTURN = "R";
	static final String GOBACK = "B";
	static final String CALIBRATE = "C";
	static final String SCANARENA = "A";
}

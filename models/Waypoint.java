package cz3004MDP.models;

public class Waypoint {

	private int waypointX;
	private int waypointY;
	
	public Waypoint(int x,int y){
		this.setWaypointX(x);
		this.setWaypointY(y);
	}

	public int getWaypointX() {
		return waypointX;
	}

	public void setWaypointX(int waypointX) {
		this.waypointX = waypointX;
	}

	public int getWaypointY() {
		return waypointY;
	}

	public void setWaypointY(int waypointY) {
		this.waypointY = waypointY;
	}

}

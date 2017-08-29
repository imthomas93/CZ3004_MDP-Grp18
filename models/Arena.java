package cz3004MDP.models;

public class Arena {
	
	private static final int X_GRID = 15;
	private static final int Y_GRID = 20;
	private Grid state;
	
	// Default Constructor
	public Arena(){
		for (int i = 0; i < X_GRID; i++){
			for (int j = 0; j < Y_GRID; j++){
				state = new Grid(i,j);
			}
		}
	}
	
	public void printArena(){
		for (int i = 0; i < X_GRID; i++){
			for (int j = 0; j < Y_GRID; j++){
				if(!state.isExplored())
					System.out.print("?");
				else if(state.isClearGrid())
					System.out.print("Y");
				else if (state.isObstacle())
					System.out.print("X");
				System.out.print(" ");
			}
			System.out.println();
		}
	}
}

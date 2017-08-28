package cz3004MDP.models;

public class Arena {

    public static final int xGRID = 15;
    public static final int yGRID = 20;
    public static final int START_GOAL_SIZE = 3;
    
    public enum state{
    	unknown, obstacle, free
    }
    
    // start zone
    private int[] start;
    // objective zone
    private int[] goal;
    // state of the specific maze's grid
    private state[][] mazeState;
    
    // default constructor
    public Arena(){}
    
    public Arena(int [] start, int [] goal){
    	this.setStart(start);
    	this.setGoal(goal);
    	this.setMazeState(new state[yGRID][xGRID]);
    }
    
    public Arena(int [] start, int [] goal, state[][] mazeState){
    	this.setStart(start);
    	this.setGoal(goal);
    	this.setMazeState(mazeState);
    }

	public int[] getStart() {
		return start;
	}

	public void setStart(int[] start) {
		this.start = start;
	}

	public int[] getGoal() {
		return goal;
	}

	public void setGoal(int[] goal) {
		this.goal = goal;
	}

	public state[][] getMazeState() {
		return mazeState;
	}

	public void setMazeState(state[][] mazeState) {
		this.mazeState = mazeState;
	}
     
    
}

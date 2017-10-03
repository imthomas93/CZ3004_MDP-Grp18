package Model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import Controllers.RealAlgorithmManager;
import Controllers.RpiManager;
import Controllers.SimAlgorithmManager;
import Controllers.SocketClientManager;
import Services.Utilities;

public class Arena extends JFrame implements RobotArenaProtocol{

	private Robot robot;
	private Arena arena;
	private Utilities utility = new Utilities();
	private SimAlgorithmManager simAlgoMgr;
	private RealAlgorithmManager realAlgoMgr;
	private RpiManager rpiMgr;
	private int[] wayPoint = new int[2];

	private boolean isSimulationDone = false;
	private boolean isRealRunNow = false;
	private String[] speedMeter = {"1", "5", "10"};
	private Timer timer;
	private Date date;
    long startTime = -1;
    long duration, now, clockTime ;
	DateFormat format = new SimpleDateFormat("mm:ss");
	JFormattedTextField jtfTimeLimit = new JFormattedTextField(format);

	
	// ARENA Var
    Grid[][] arenaDesign = new Grid[ROW][COLUMN];
    Grid[][] arenaReal = new Grid[ROW][COLUMN];

    // UI Var
	JPanel jpManual,jpSub1, jpSub2, jpSub3, jpArenaSub, jpLeft, jpRight, jpLeftMap, jpRightMap, jpToplabel;
	JButton btnSwitchSimOrReal, btnRealExp, btnRealFP, btnSimExp, btnSimFP, btnReset, btnImport, btnSimExport,btnEnableCoverageTerminal, btnEnableTimeLimitTerminal;
	JLabel jlbTimeLimit, jlArenaDesign, jlTimeCost, jlSpeed, jlbExpCoverage, jlbCoveragePercent, jlbTerminal, jlbTerminal2, jlConsoleArea, jlWayPoint;
	JTextField jtfExplorationCoverage;
	static JTextArea textbox;
	JComboBox jcbSpeed;
	JScrollPane scrollV;
	SelectGridClicker clickGrid = new SelectGridClicker();
	
	
	public Arena(Robot robot)
	{	
		// Demo waypoint
		wayPoint[0] = 5;
		wayPoint[1] = 9;
		
		// Setup UI for arena
		this.setupGUI();
		
		// prepare robot and arena
		this.robot = robot;
		arena = this;

		// prepare algo
		appendMessage("Mode: Simulation Mode");
		simAlgoMgr = new SimAlgorithmManager(robot, arena, wayPoint);
		
		// defined robot position on arena
		this.updateRobotPosition();
		resetArena();
		setStartGoalZone();
	}
	
	public Grid[][] getRealArena(){
		return arenaReal;
	}
	
	private void setupGUI()
	{
		// TODO Auto-generated method stub
		// UI Layout
		this.setTitle("MDP_GROUP 18 Simulator");
	   	this.setSize(1200,670);
	   	this.setLocation(100,80);
	   	this.setLayout(new BorderLayout());
	   	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   	
	   	//combobox to select speed 
	   	jcbSpeed = new JComboBox(speedMeter);
	   	jcbSpeed.setSelectedIndex(2);
	   
	   	btnEnableCoverageTerminal = new JButton("Disabled");
	   	btnEnableCoverageTerminal.addActionListener(new CoverageTerminalHandler());
	   	btnEnableTimeLimitTerminal = new JButton("Disabled");
	   	btnEnableTimeLimitTerminal.addActionListener(new TimeLimitTerminalHandler());
	   	
	   	// Jlabel for displaying info
	   	jlbTimeLimit = new JLabel("Time limit (sec):");

	   	jlArenaDesign = new JLabel("Design arena area:");

	   	jlSpeed = new JLabel("Speed (# of step/sec):");
	   	jlWayPoint = new JLabel("WayPoint Coord(r/c): " + wayPoint[0] + "," + wayPoint[1]);
	   	jlbCoveragePercent = new JLabel("Explored Coverage 0%");
	   	jlbExpCoverage = new JLabel("Coverage (%):");
	   	
	   	jlbTerminal = new JLabel("Coverage switch:");
	   	jlbTerminal2 = new JLabel("Timer switch:");
	   	
	   	jlConsoleArea = new JLabel("Console Box");
	   	
	   	// field for UI console
	   	jtfExplorationCoverage = new JTextField(5);
	   	jtfExplorationCoverage.setText("100");
	   	
	   	// GUI Console 
		textbox	= new JTextArea();
	   	textbox.setEditable(false);
	   	DefaultCaret caret = (DefaultCaret)textbox.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	   	scrollV = new JScrollPane(textbox);
	   	
	   	scrollV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	   	
	    // Panel layout for UI
	   	jpManual = new JPanel(new BorderLayout());
	   	jpManual.setPreferredSize(new Dimension(350, 500));
	   	jpSub1 = new JPanel(new GridLayout(6,6,20,0));
	   	jpSub3 = new JPanel(new GridLayout(1,1,0,0));

	   	jpSub1.add(jlbTimeLimit);
	   	jpSub1.add(jtfTimeLimit);
	   	jpSub1.add(jlbTerminal2);
	   	jpSub1.add(btnEnableTimeLimitTerminal);
	   	jpSub1.add(jlbExpCoverage);
	   	jpSub1.add(jtfExplorationCoverage);
	   	jpSub1.add(jlbTerminal);
	   	jpSub1.add(btnEnableCoverageTerminal);
	   	jpSub1.add(jlSpeed);
	   	jpSub1.add(jcbSpeed);
		jpSub1.add(jlWayPoint);;
	   
	   	jpSub3.add(scrollV);
	 
	   	jpManual.add(jpSub1,BorderLayout.NORTH);
	   	jpManual.add(jpSub3,BorderLayout.CENTER);
	   	
	   	jpLeft = new JPanel(new BorderLayout());
	   	jpRight = new JPanel(new BorderLayout());
	   	
	   	jpLeftMap = new JPanel(new GridLayout(ROW,COLUMN));
	   	jpRightMap = new JPanel(new GridLayout(ROW,COLUMN));
	   	jpLeftMap.setPreferredSize(new Dimension(400, 500));
	   	jpRightMap.setPreferredSize(new Dimension(400, 500));
	   	
	   	// set option for each grid and defined the start and end zone of arena
	   	this.setUpSimArena();
	   	this.updateArena(arenaDesign);
	   	this.updateArena(arenaReal);
	    this.setStartGoalZone(); 	
	    

	    // Button Setup
	   	jpArenaSub = new JPanel(new BorderLayout());
	   	jpSub2 = new JPanel(new GridLayout(3,3,10,10));
	   	
	   	// Jbutton for selecting options
	   	btnSwitchSimOrReal = new JButton("Switch Mode");
	   	btnSwitchSimOrReal.addActionListener(new SwitchSimRealHandler());
	   			
	   	btnSimExp = new JButton("Simulation Exploration");
	   	btnSimExp.addActionListener(new SimExplorationHandler());
	   	
	   	btnSimFP = new JButton(" Simulation Fastest path");
	   	btnSimFP.addActionListener(new SimFPHandler());
	   	
	   	btnReset = new JButton("Reset arena");
	   	btnReset.addActionListener(new resetArenaHandler());
	   	
	   	btnImport = new JButton("Import a predefined arena");
	   	btnImport.addActionListener(new ImportMapHandler());
	   	btnSimExport = new JButton("Export current real arena");
	   	btnSimExport.addActionListener(new ExportMapHandler());
	   	
		jpSub2.add(btnSwitchSimOrReal);
	   	jpSub2.add(btnReset);
	   	jpSub2.add(btnSimExp);
	   	jpSub2.add(btnSimFP);
	   	jpSub2.add(btnReset);
	   	jpSub2.add(btnImport);
	   	jpSub2.add(btnSimExport);
	   	
	    jpToplabel = new JPanel(new GridLayout(1,1));
	    jpToplabel.add(jlbCoveragePercent);
	
	   	jpLeft.add(jpLeftMap,BorderLayout.SOUTH);
	   	jpLeft.add(jlArenaDesign,BorderLayout.NORTH);

	   	jpRight.add(jpRightMap,BorderLayout.SOUTH);
	   	jpRight.add(jpToplabel,BorderLayout.NORTH);

	   	jpArenaSub.add(jpLeft,BorderLayout.WEST);
	   	jpArenaSub.add(jpRight, BorderLayout.EAST);
	   	jpArenaSub.add(jpSub2, BorderLayout.SOUTH);
	   	
	   	this.add(jpManual,BorderLayout.EAST);
	   	this.add(jpArenaSub,BorderLayout.WEST);
	   	jtfExplorationCoverage.setEditable(false);
	   	jtfTimeLimit.setEditable(false);
	   	this.setVisible(true);	
	}
		
	public void setUpSimArena()
	{
		// TODO Auto-generated method stub
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j <COLUMN; j++){
				// simulated arena is always visited
				// left side of the GUI
				arenaDesign[i][j] = new Grid(i, j);
				arenaDesign[i][j].addMouseListener(clickGrid);
				arenaDesign[i][j].setOpaque(true);
				arenaDesign[i][j].setGridStatus(1, 0);

				arenaDesign[i][j].setBorder(BorderFactory.createLineBorder(CELLBORDER_COLOR));
				jpLeftMap.add(arenaDesign[i][j]);
				
				// real Arena
				// right side of GUI
				arenaReal[i][j] = new Grid(i, j);
				arenaReal[i][j].setBorder(BorderFactory.createLineBorder(CELLBORDER_COLOR));
				arenaReal[i][j].setOpaque(true);
				jpRightMap.add(arenaReal[i][j]);

			}
		}
	}
	
	private void resetArena() {
		// TODO Auto-generated method stub
		
		// reset robot position first  - current and prev back to original state
		int[] currentPosition = new int[2];
		robot.setCurrentPosition(currentPosition);
		currentPosition[0] = 18;
		currentPosition[1] = 1;
		robot.setCurrentPosition(currentPosition);
		robot.setPreviousPosition(currentPosition);
		
		// reset the position of the degree robot is facing
		robot.setRobotHead(NORTH);
		
		// reset the 2 arena
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				arenaDesign[i][j].setGridStatus(VISITED, NO_OBSTACLE);
				arenaReal[i][j].setGridStatus(NOT_VISITED, NO_OBSTACLE);
				updateArena(arenaDesign);
				updateArena(arenaReal);
		    }
		}
		
		// update the robot position on the arena at default stage
		resetRobotPosition();
		
		// disable fastestPath option
		allowFastestPath(false);
	}
	
	private void resetRobotPosition(){
		
		int[] currentPosition = new int[2];
		currentPosition[0] = robot.getCurrentPosition()[0];
		currentPosition[1] = robot.getCurrentPosition()[1];

		// set the color of the robot
		for(int i=-1; i < 2; i++){
			for(int j=-1; j < 2; j++){
				arenaReal[currentPosition[0]+i][currentPosition[1]+j].setBackground(ROBOT_COLOR);
			}
		}
		switch(robot.getRobotHead()){
		case NORTH:
			currentPosition[0] = currentPosition[0] - 1;
			break;
		case SOUTH:
			currentPosition[0] = currentPosition[0] + 1;
			break;
		case EAST:
			currentPosition[1] = currentPosition[1] + 1;
			break;
		case WEST:
			currentPosition[1] = currentPosition[1] - 1;
			break;
		}
		// set robot position head color
		arenaReal[currentPosition[0]][currentPosition[1]].setBackground(ROBOTDIRECTION_COLOR);		
	}
	
	// for real run reset
	private void resetRobotPosition(int[] startPosition) {
		robot.setRobotHead(NORTH);
		
		// set the color of the robot
		for(int i=-1; i < 2; i++){
			for(int j=-1; j < 2; j++){
				arenaReal[startPosition[0]+i][startPosition[1]+j].setBackground(ROBOT_COLOR);
			}
		}
		switch(robot.getRobotHead()){
		case NORTH:
			startPosition[0] = startPosition[0] - 1;
			break;
		case SOUTH:
			startPosition[0] = startPosition[0] + 1;
			break;
		case EAST:
			startPosition[1] = startPosition[1] + 1;
			break;
		case WEST:
			startPosition[1] = startPosition[1] - 1;
			break;
		}
		arenaReal[startPosition[0]][startPosition[1]].setBackground(ROBOTDIRECTION_COLOR);		
	}
	
	private void setStartGoalZone()
	{
		// This method will prevent any obstacle to be set on start and goal zone
		// TODO Auto-generated method stub
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				int a = ROW - i - 1;
				int b = COLUMN - j - 1;
				arenaDesign[a][j].setBackground(STARTZONECOLOR);
				arenaDesign[a][j].removeMouseListener (clickGrid);
				
				arenaDesign[i][b].setBackground(GOALZONECOLOR);
				arenaDesign[i][b].removeMouseListener (clickGrid);
			}
		}	
	}
	
	private	void addMouseListener(){
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j < COLUMN; j++){
				arenaDesign[i][j].addMouseListener(clickGrid);
			}
		}
		updateArena(arenaDesign);
	}
		
	private void removeMouseListener(){
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j < COLUMN; j++){
				arenaDesign[i][j].removeMouseListener(clickGrid);
			}
		}
		updateArena(arenaReal);
	}
		
	public void updateRobotPosition()
	{	
		//update explored arena before update robot position
		updateArenaAfterMovement();
		
		// Display real time update on real arena
		updateArena(arenaReal);
		
		int[] currentPosition = new int[2];
		currentPosition[0] = robot.getCurrentPosition()[0];
		currentPosition[1] = robot.getCurrentPosition()[1];
		
		// set the color of the robot
		for(int i=-1; i < 2; i++){
			for(int j=-1; j < 2; j++){
				arenaReal[currentPosition[0]+i][currentPosition[1]+j].setBackground(ROBOT_COLOR);
			}
		}
		switch(robot.getRobotHead()){
		case NORTH:
			currentPosition[0] = currentPosition[0] - 1;
			break;
		case SOUTH:
			currentPosition[0] = currentPosition[0] + 1;
			break;
		case EAST:
			currentPosition[1] = currentPosition[1] + 1;
			break;
		case WEST:
			currentPosition[1] = currentPosition[1] - 1;
			break;
		}
		arenaReal[currentPosition[0]][currentPosition[1]].setBackground(ROBOTDIRECTION_COLOR);
	}
		
	public void updateArenaAfterMovement(){	
		int a, b;
		int[] curPos = robot.getPreviousPosition();
		for(int i=-1; i<2; i++){
			for(int j=-1; j<2; j++){
				a = curPos[0]+i;
				b = curPos[1]+j;
				
				// if grid position is not visited
				if(arenaReal[a][b].getGridStatus()[0] != 1)
					arenaReal[a][b].setGridStatus(1, 0);
			}
									
		}
		// init sensor data for next step
		if(isRealRunNow){
			// Real Run
			int [] sensorData = rpiMgr.getSensorReading();
			robot.getSensorsData(arenaReal, sensorData);
		}
		else
			robot.getSensorsData(arenaReal);

	}
	
	public void updateArena(Grid[][] arenaDesign)
	{
		for(int i=0; i < ROW; i++){
			for(int j = 0; j < COLUMN; j++){
				// if grid is not visited
				if(arenaDesign[i][j].getGridStatus()[0] == 0)
					arenaDesign[i][j].setBackground(UNVISITED_COLOR);
				// if grid is visited
				else{
					//Grid is NO_OBSTACLE
					if(arenaDesign[i][j].getGridStatus()[1] == 0)
						arenaDesign[i][j].setBackground(EMPTY_COLOR);
					//Grid has obstacle
					else if(arenaDesign[i][j].getGridStatus()[1] == 1)
						arenaDesign[i][j].setBackground(OBSTACLE_COLOR);
					else if(i == wayPoint[0] && j == wayPoint[1])
						arenaDesign[i][j].setBackground(WAYPOINT_COLOR);
				}		
			}
		}	
	}
	
	public static void appendMessage(String message){
		message += "\n";
		// for IDE console
		System.out.print(message);
		// for GUI console
		textbox.append(message);
	}
	
	public void startTimer() throws ParseException
	{
		startTime = -1;
	   	format.setTimeZone(TimeZone.getTimeZone("GMT"));
		date = format.parse(jtfTimeLimit.getText());
		duration = date.getTime();
		
		 timer = new Timer(10, new ActionListener() {
            
			public void actionPerformed(ActionEvent actionEvent) {
				  if (startTime < 0) {
	                     startTime = System.currentTimeMillis();
	                 }
	                 now = System.currentTimeMillis();
	                 clockTime = now - startTime;
	                 if (clockTime >= duration) {
	                     clockTime = duration;
	                     simAlgoMgr.setTimesUp(true);
	                 }
	                 format.setTimeZone(TimeZone.getTimeZone("UTC"));
	                 jtfTimeLimit.setText(format.format(duration - clockTime));
            }
        });
        timer.setInitialDelay(0);
        timer.start();
	}
	
	public void stopTimer()
	{
		System.out.println("Timer Stop");
		timer.stop();
		
	}
	
	public void updateCoverageAndTime(){
		double result = calculateExploredPercentage();
		jlbCoveragePercent.setText("Explored Coverage: "+result+"%");

	}

	public double calculateExploredPercentage(){
		int count = 0;
		double arenaSize = ROW*COLUMN;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if(arenaReal[i][j].getGridStatus()[0] == 1){
					count++;
				}
			}
		}
		return ((count/arenaSize)*100);
	}
	
	/*
	 * ACTIONLISTENER IMPLEMENTATION OVER HERE
	 * 
	 */
 	class SelectGridClicker extends MouseAdapter{
	
 		public void mouseClicked(MouseEvent event){
 			if(!isRealRunNow)
 				for (int i = 0; i < ROW; i++) 
 					for (int j = 0; j < COLUMN; j++) 
 						if(event.getSource()==arenaDesign[i][j])
 							if(arenaDesign[i][j].getGridStatus()[1]== NO_OBSTACLE){
 								arenaDesign[i][j].setGridStatus(VISITED, OBSTACLE);
 								arenaDesign[i][j].setBackground(OBSTACLE_COLOR);
 								arenaReal[i][j].setGridStatus(NOT_VISITED, OBSTACLE);
 								appendMessage("Selected Row: " + i + "; Column = "+ j);
 							}
 							else if(arenaDesign[i][j].getGridStatus()[1]== OBSTACLE){
 								arenaDesign[i][j].setGridStatus(VISITED, NO_OBSTACLE);
 								arenaDesign[i][j].setBackground(EMPTY_COLOR);
 								arenaReal[i][j].setGridStatus(NOT_VISITED, NO_OBSTACLE);
 								appendMessage("Undo Row: " + i + "; Column = "+ j);

 							}
 		}
	}
 	
 	class SwitchSimRealHandler implements ActionListener{
 		
 		public void actionPerformed(ActionEvent event){
 			//TODO, SWITCH LOGIC
 			//TODO, switch to block design arena
 			if(!isRealRunNow){				
 				isRealRunNow  = true;
 				btnSimExp.setEnabled(false);
 				btnSimFP.setEnabled(false);
 				
 				resetArena();
 				removeMouseListener();
 				resetRobotPosition();
				
 				setupRealRun();	
 				
 			}
 			else{
 				isRealRunNow  = false;	
 				Arena.appendMessage("Mode: Simulation Mode");
 				btnSimExp.setEnabled(true);
 				btnSimFP.setEnabled(true);;
 				
 				resetArena();
 				addMouseListener();
 				setStartGoalZone();
 				arena.updateRobotPosition();
 			}
 		}

		private void setupRealRun() {
			// TODO Auto-generated method stub

			rpiMgr = new RpiManager();
			
			int[] startPosition = new int[2];
			int[] startInformation = rpiMgr.getStartPositionFromAndroid();
			
			// Setting up robot position
			startPosition[0] = startInformation[0];
			startPosition[1] = startInformation[1];
			robot.setCurrentPosition(startPosition);

			int directionOfRobotHead = startInformation[2];
			robot.setRobotHead(directionOfRobotHead);

			// reset robot position
			resetRobotPosition(startInformation);
			
			Arena.appendMessage("Start Robot Position(r;c): "+ robot.getCurrentPosition()[0] + " ; " +  robot.getCurrentPosition()[1]);
			Arena.appendMessage("Start Robot Degree: " + robot.getRobotHead());
			
			// Actual Waypoint coord from android
			wayPoint = rpiMgr.getWayPointromAndroid();
			Arena.appendMessage("WayPoint coordinate from Android: " + wayPoint[0] + " ; " + wayPoint[1]);
			jlWayPoint.setText("WayPoint Coord(r/c): " + wayPoint[0] + "," + wayPoint[1]);

			// create algo object
			realAlgoMgr = new RealAlgorithmManager(robot, arena, wayPoint, rpiMgr);
			updateRobotPosition();
			appendMessage("Mode: Real Run Mode");	
			Arena.appendMessage("Robot is ready for execution, awaiting command to explore!");
					
			String input = rpiMgr.getInstructionFromAndroid();
			if(input.equals("EX")){
				realAlgoMgr.realGo();	
				
				//input = rpiMgr.getInstructionFromAndroid();
				if(input.equals("FP")){
					Arena.appendMessage("Starting Fastest Path(via WayPoint)...");
						realAlgoMgr.fpgo(arenaReal);
				}
			}
					
			//input = rpiMgr.getInstructionFromAndroid();
			if(input.equals("FP")){
				Arena.appendMessage("Starting Fastest Path(via WayPoint)...");
					realAlgoMgr.fpgo(arenaReal);
			}

		}
 	}
	
	class SimExplorationHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			appendMessage("Starting Exploration...");
			int speed = Integer.parseInt(jcbSpeed.getSelectedItem().toString());
			int coveredPercentage = Integer.parseInt(jtfExplorationCoverage.getText().toString());
			if(simAlgoMgr.getTimerTerminal()){
				try {
					startTimer();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			simAlgoMgr.setSpeed(speed);
			simAlgoMgr.setCoveredPercentage(coveredPercentage);
			simAlgoMgr.simGo();
			
			isSimulationDone = true;
		}
	}
	
	class SimFPHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String msg1 = "Pls run FP Algo after exploring!\n";
			String msg2 = "Starting fastest path now...\n";
			if(isSimulationDone == false){
				JOptionPane.showMessageDialog(null, "Fastest Path can only be run after exploration is done");
				appendMessage(msg1);

			}
			else{
				appendMessage(msg2);
				simAlgoMgr.fpgo(arenaReal);
			}
		}
	}

	class resetArenaHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			resetArena();
			btnEnableCoverageTerminal.setText("Disabled");
			btnEnableTimeLimitTerminal.setText("Disabled");
			simAlgoMgr.resetAlgo();
			if(!isRealRunNow)
				setStartGoalZone();
			
			jlbCoveragePercent.setText("Explored Coverage: 0%");

			isSimulationDone = false;
			appendMessage("Successfully reset arena!");
		}
	}
	
	class ExportMapHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event){
			ArrayList<String> result = new ArrayList<String>();
			result = utility.exportMap(arenaDesign);
			utility.saveArenaToFile(FILENAME,result);
			appendMessage("Arena Exported!");
		}
	}
	
	class ImportMapHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{			
			importMap(FILENAME);
			setStartGoalZone();
			
			//set the obstacle to the arenaReal
			for(int i=0; i < ROW; i++)
				for(int j = 0; j < COLUMN; j++)
					if(arenaDesign[i][j].getGridStatus()[1]== 1){
						arenaReal[i][j].setGridStatus(0, 1);
						arenaDesign[i][j].setBackground(OBSTACLE_COLOR);
					}
			
			appendMessage("Import arena successfully");
		}
		private void importMap(String fileName)
		{
			ArrayList<String> result = new ArrayList<String>();
			result = utility.getArenaFromFile(RobotArenaProtocol.FILENAME);
			String exploreResult = "", obstacleResult = "";
			int exploreIndex = 0, obstacleIndex = 0;

			exploreResult = utility.hexToBinary(result.get(0));
			obstacleResult = utility.hexToBinary(result.get(1));
			
			// remove the first and last 2 char.
			exploreResult = exploreResult.substring(2, exploreResult.length()-2);
			
			for(int i = RobotArenaProtocol.ROW-1; i >= 0; i--){
				for(int j = 0; j < RobotArenaProtocol.COLUMN; j++){
					if(exploreResult.charAt(exploreIndex) =='1'){
						// if visited
						arenaDesign[i][j].setGridStatus(exploreResult.charAt(exploreIndex), Character.getNumericValue(obstacleResult.charAt(obstacleIndex)));
						obstacleIndex++;
					}
					else{
						arenaDesign[i][j].setGridStatus(Character.getNumericValue(exploreResult.charAt(exploreIndex)), 0);	
						exploreIndex++;
					}
				}
			}
		}
	}
	
	class CoverageTerminalHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent event){
			if(simAlgoMgr.getCoveredTerminal()){
				btnEnableCoverageTerminal.setText("Disabled");
				jtfExplorationCoverage.setEditable(false);
			}
			else{
				btnEnableCoverageTerminal.setText("Enabled");
				jtfExplorationCoverage.setEditable(true);
			}
			simAlgoMgr.switchCoveredTerminal();
		}
	}
	
	class TimeLimitTerminalHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(simAlgoMgr.getTimerTerminal()){
				btnEnableTimeLimitTerminal.setText("Disabled");
				jtfTimeLimit.setEditable(false);
			}
			else{
				btnEnableTimeLimitTerminal.setText("Enabled");
				
				jtfTimeLimit.setEditable(true);
			}
			simAlgoMgr.switchTimerTerminal();
		}
	}

	public void allowFastestPath(boolean result) {
		// TODO Auto-generated method stub
		if(result)
			btnSimFP.setEnabled(true);
		else
			btnSimFP.setEnabled(false);
	}

}

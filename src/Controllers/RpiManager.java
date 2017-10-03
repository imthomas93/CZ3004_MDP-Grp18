package Controllers;

import java.util.concurrent.TimeUnit;

import Model.Arena;
import Model.RobotArenaProtocol;

public class RpiManager implements RobotArenaProtocol {
	
	private String message;
	SocketClientManager socketThread = null;
	 
	public RpiManager(){		
		try{
			//open socket
			SocketClientManager.openSocket();
		}
		catch(Exception e){
			//Terminate this main
			e.printStackTrace();
			return;
		}

		//Starting a thread to listen for instruction
		socketThread =  new SocketClientManager();
		socketThread.start();
	}

	
	public void sendInstruction(String instruction) {
		// TODO Auto-generated method stub
		try{
			instruction = instruction.trim();
			SocketClientManager.writeToSocket(instruction);
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int[] getStartPositionFromAndroid(){
		int[] result = new int[3]; 
		String andoridInput = "";
		
		try {
			// Testing
			SocketClientManager.writeToSocket(TABLET + "S");
			
			// Actual
			//SocketClientManager.writeToSocket(RPI + "s");

			synchronized(socketThread){
				socketThread.wait();
				andoridInput = SocketClientManager.receivedMsg;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] startPosition = andoridInput.split(";");
		result[0] = Integer.parseInt(startPosition[0]);
		result[1] = Integer.parseInt(startPosition[1]);
		result[2] = Integer.parseInt(startPosition[2]);
		
		return result;
	}
		
	public int[] getWayPointromAndroid(){
		String andoridInput = "";
		int [] result = new int[2];

		try {
			// Testing
			SocketClientManager.writeToSocket(TABLET + "W");
			
			// Actual
			//SocketClientManager.writeToSocket(RPI + "w");

			synchronized(socketThread){
				socketThread.wait();
				andoridInput = SocketClientManager.receivedMsg;
				System.out.println(andoridInput);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] wp = andoridInput.split(";");
		result[0] = Integer.parseInt(wp[0]);
		result[1] = Integer.parseInt(wp[1]);
		return result;
	}

	public String getInstructionFromAndroid() {
		// TODO Auto-generated method stub
		String andoridInput = "";
		try {
			synchronized(socketThread){
				socketThread.wait();
				andoridInput = SocketClientManager.receivedMsg;
				System.out.println(andoridInput );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return andoridInput;
	}


	public int[] getSensorReading() {
		// TODO Auto-generated method stub
		String sensorReading = "";
		int [] result = new int[5];
		
		try {
			// Actual
			SocketClientManager.writeToSocket(AUDUINO + SCANARENA);

			// Expected format
			// LeftFrontSensor;CenterFrontSensor;RightFrontSensor;LeftSensor;RightLongRangeSensor

			synchronized(socketThread){
				socketThread.wait();
				sensorReading = SocketClientManager.receivedMsg;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sensorReading = sensorReading.trim();
		String[] sensorData = sensorReading.split(":");
		
		System.out.println(sensorReading);
		for(int i = 0; i < 5;i++){
			result[i] = Integer.parseInt(sensorData[i]);
		}
		return result;
	}

}

package Controllers;

import java.io.IOException;
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
		// THIS METHOD SEND INSTRUCTION WITHOUT THE NEED TO WAIT FOR ACKNOWLEDGEMENT
		try{
			instruction += "#";
			instruction = instruction.trim();
			SocketClientManager.writeToSocket(instruction);
			TimeUnit.NANOSECONDS.sleep(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendInstruction2(String instruction) {
		// THIS METHOD SEND INSTRUCTION WITH THE NEED TO WAIT FOR ACKNOWLEDGEMENT
		try{
			instruction += "#";
			instruction = instruction.trim();			
			SocketClientManager.writeToSocket(instruction);
			while(true){
				synchronized(socketThread){
					Arena.appendMessage("Awaiting Movement ACK...");
					socketThread.wait();
					String input = SocketClientManager.receivedMsg;
				
					// tokenise message
					String[] inputData = input.split("!");
					
					if (inputData[0].equals("ACK")){
						RealAlgorithmManager.sensorData = inputData[1];
						Arena.appendMessage("ACK!\n");
						Arena.appendMessage(inputData[2]);
						break;
					}
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendInstruction3(String instruction) {
		// THIS METHOD SEND INSTRUCTION WITH THE NEED TO WAIT FOR ACKNOWLEDGEMENT
		try{
			instruction += "#";
			instruction = instruction.trim();
			
			SocketClientManager.writeToSocket(instruction);
			while(true){
				synchronized(socketThread){
					socketThread.wait();
					Arena.appendMessage("Awaiting calibration ACK");

					String input = SocketClientManager.receivedMsg;
				
					// tokenise message
					String[] inputData = input.split("!");
					if (inputData[0].equals("ACK")){
						Arena.appendMessage("ACK!");
						break;
					}
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int[] getStartPositionFromAndroid(){
		int[] result = new int[3]; 
		String andoridInput = "";
		
		try {			
			// ActuaL
			SocketClientManager.writeToSocket(TABLET + "S");
			
			// Testing
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
			//SocketClientManager.writeToSocket(RPI + "w");

			// Actual
			SocketClientManager.writeToSocket(TABLET + "W");

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
		// prepare to get EX or FP fron android
		String andoridInput = "";
		try {
			synchronized(socketThread){
				socketThread.wait();
				andoridInput = SocketClientManager.receivedMsg;
				System.out.println(andoridInput );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return andoridInput;
	}

	public void getSensorReading() {
		// Return intial sensor reading then robot is ready
		String sensorReading = "";		
		try {
			// Actual
			SocketClientManager.writeToSocket(AUDUINO + SCANARENA);

			// Expected format
			// LeftFrontSensor;CenterFrontSensor;RightFrontSensor;LeftSensor;RightLongRangeSensor

			synchronized(socketThread){
				socketThread.wait();
				sensorReading = SocketClientManager.receivedMsg;
				
				// tokenise message
				String[] inputData = sensorReading.split("!");	
				RealAlgorithmManager.sensorData = inputData[1];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void disconnect() {
		try {
			SocketClientManager.openedSocket.close();
			Arena.appendMessage("Socket Closed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

import java.util.Scanner;

public class Algorithm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String msg;
		
		try{
			//open socket
			SocketClient.openSocket();
		}
		catch(Exception e){
			//Terminate this main
			e.printStackTrace();
			return;
		}

		//Starting a thread to listen for instruction
		SocketClient socketThread =  new SocketClient();
		socketThread.start();
		
		//Replace this while true with your algorithm. Algorithm should not stop until received ';' from Android
		//or Algorithm finish exploration/Shortest path/etc
		while(true){
			
			//variable for sending
			msg=null;
			String instruction= null ;
			
			//This part is for testing purposes
			Scanner input = new Scanner(System.in);
			
			System.out.println("Waiting for Data...");
			
			//This part is for the algorithm to wait for updated state before continuing. 
			//Eg. Algorithm can't start until Arduino sent the initial state
			
			//Eg. Algorithm sent the first instruction(forward/left/right/reverse/etc) and waiting for the new 
			//state to be received to continue the rest of the algorithm
			try {
				synchronized(socketThread){
					//Waiting for instruction to arrive
					socketThread.wait();
					
					//After Instruction arrive, access it
					instruction = socketThread.receivedMsg;
					System.out.println(instruction);
					
					
					if(instruction.equals(";")){
						//Received a terminating token from Android.
						//Sending a ; token over to RPI closes the socket properly; please send ; if you want to terminate the connection
						//If we are restarting, just make the state blank and wait for arduino to send us the initial state again,
						//If we are restarting, try not to close the connection to prevent restarting the of the script
						SocketClient.writeToSocket(instruction);
						
						//Break the while loop or in your case, end the algorithm because of reasons(?)
						break;
					}
					//This part is for testing purposes
					msg = input.nextLine();
					//Sending msg to populate to ALL devices
					SocketClient.writeToSocket(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//breaks the while loop.(for testing purposes i used ; to terminate it. By right you should not do it)
			//This is when you algorithm made you proud and manage to give you what you desire! =D
			if(msg.equals(";")){
				break;
			}
		}
	}
}

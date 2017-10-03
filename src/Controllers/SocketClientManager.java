package Controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import Model.Arena;

public class SocketClientManager extends Thread
{
	Thread t;
	String threadName = "Socket Thread";
	boolean timeToBreak = false;
	static String server = "192.168.18.1";
	static int port = 911;
	static Socket openedSocket;
	static String receivedMsg = "";

	public void run(){
		System.out.println("Thread: "+threadName+" is running");
		listen();
	}
	
	private void listen(){
		try{
			while(true){
				//start listening to socket
				String msg = readFromSocket();
				
				synchronized(this){
					if(msg.length()!=0){
						if(msg.startsWith("*")){
							//Instruction from RPi
							System.out.println(msg.substring(1));
						}
						else{
							//Instruction from devices
							receivedMsg = msg;
							if(msg.equals("connected"))
								Arena.appendMessage("Connected to RPI");
							notifyAll();
						}
					}
				}
			}
		}
		catch(Exception e){

		}
	}

	private String readFromSocket() throws Exception
	{
		try 
		{
			// read text from the socket
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openedSocket.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			while (true) {
				int ch = bufferedReader.read();
				if ((ch < 0) || (ch == '\n')) {
					break;
				}
				buffer.append((char) ch);
			}
			String clientRequest = buffer.toString();
			return clientRequest;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}

	}
	public static void writeToSocket(String msg) throws Exception{
		try{
			// write text to the socket
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openedSocket.getOutputStream()));
			bufferedWriter.write(msg);
			bufferedWriter.flush();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	public static void openSocket() throws Exception
	{
		Socket socket;

		// create a socket with a timeout
		try
		{
			InetAddress inteAddress = InetAddress.getByName(server);
			SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

			// create a socket
			socket = new Socket();

			// this method will block no more than timeout ms.
			int timeoutInMs = 10*1000;   // 10 seconds
			socket.connect(socketAddress, timeoutInMs);
			
			System.out.println("Successfully Connected to Rpi's Socket");
			openedSocket = socket;
		} 
		catch (SocketTimeoutException ste) 
		{
			System.err.println("Timed out waiting for the socket.");
			ste.printStackTrace();
			throw ste;
		}
	}

}
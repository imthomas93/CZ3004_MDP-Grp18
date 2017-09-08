package cz3004MDP.services;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Algorithm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String msg;
		String testServerName = "192.168.18.1";
		int port = 911;
		Socket socket = null;
		try{
			//open socket
			socket = openSocket(testServerName, port);
			System.out.println("Successfully Connected to Rpi's Socket");
		}
		catch(Exception e){
			return;
		}

		SimpleSocketClient socketThread =  new SimpleSocketClient(socket);
		socketThread.start();
		boolean toBreak = false;
		while(true){
			msg=null;
			Scanner input = new Scanner(System.in);
			System.out.print("Data to broadcast: ");
			msg = input.nextLine();
			
			try{
				// write text to the socket
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bufferedWriter.write(msg);
				bufferedWriter.flush();
				
			}catch(Exception e){
				
			}
			
			if(msg.equals(";")){
				break;
			}
		}

	}
	private static Socket openSocket(String server, int port) throws Exception
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

			return socket;
		} 
		catch (SocketTimeoutException ste) 
		{
			System.err.println("Timed out waiting for the socket.");
			ste.printStackTrace();
			throw ste;
		}
	}


}

package cz3004MDP.services;

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

/**
 *
 * A complete Java class that demonstrates how to use the Socket
 * class, specifically how to open a socket, write to the socket,
 * and read from the socket.
 *
 * @author alvin alexander, alvinalexander.com.
 *
 */
public class SimpleSocketClient extends Thread
{
	Thread t;
	String threadName = "Socket Thread";
	boolean timeToBreak = false;
	Socket socket;
	public SimpleSocketClient(Socket socket)
	{
		this.socket = socket;
	}

	public void run(){
		System.out.println("Thread: "+threadName+" is running");
		listen();
	}
	private void listen(){
		try{
			while(true){
				//start listening to socket
				String msg = readFromSocket(socket);
				if(msg.length()!=0){
					System.out.println(msg);
				}
			}
		}
		catch(Exception e){

		}
	}

	private String readFromSocket(Socket socket) throws Exception
	{
		try 
		{
			// read text from the socket
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//				StringBuilder sb = new StringBuilder();
			//				String str;
			//				while ((str = bufferedReader.readLine()) != null)
			//				{
			//					sb.append(str + "\n");
			//				}

			StringBuffer buffer = new StringBuffer();
			while (true) {
				int ch = bufferedReader.read();
				if ((ch < 0) || (ch == '\n')) {
					break;
				}
				buffer.append((char) ch);
			}
			String clientRequest = buffer.toString();
			//bufferedReader.close();
			// close the reader, and return the results as a String
			return clientRequest.toString();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}

	}

}
package cz3004MDP.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPService {

	// confirm RPI IP add
	public static final String RPI_IP_ADD = "";
	// confirm RPI port
	public static final int RPI_PORT = 4040;
	// confirm time to rety sending frame
	public final int RETRY_TIME = 1000;
	// confirm delay in sending message
	public final int DELAY_TIME = 2;
	
	private static TCPService instance;
	private Socket client;
	private PrintWriter toRPi;
	private Scanner fromRPi;
	
	public static TCPService getInstance(){
		if (instance.equals(null))
			instance = new TCPService();
		
		return instance;
	}
	
	// default contructor
	private TCPService(){}
	
	
	public void connectHost(){
		try{
			client = new Socket(RPI_IP_ADD, RPI_PORT);
			toRPi = new PrintWriter(client.getOutputStream());
			fromRPi = new Scanner(client.getInputStream());
		}
		catch (IOException ioe){
			ioe.printStackTrace();
			try{
				Thread.sleep(RETRY_TIME);
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
			// retry to connect to RPI
			connectHost();
		}
		System.out.println("RPI Connected");
	}
	
	public void sendMessage(String outputStream){
		try{
			Thread.sleep(DELAY_TIME);
			toRPi.println(outputStream);
			// clear message
			toRPi.flush();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
			try{
				Thread.sleep(RETRY_TIME);
			}
			catch(InterruptedException ie2){
				ie2.printStackTrace();
			}
			// retry sending message
			connectHost();
			sendMessage(outputStream);
		}
	}
	
	public void readMessage(){
		String inputStream = "";
		try{
			inputStream = fromRPi.nextLine();
			System.out.println("message: " + inputStream);
		}
		catch(Exception e){
			e.printStackTrace();
			try{
				Thread.sleep(RETRY_TIME);
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
			// retry reading message
			connectHost();
			readMessage();
		}
	}
	
	public void closeConnection(){
		try{
			if (!client.isClosed())
				client.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
			try{
				Thread.sleep(RETRY_TIME);
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
			// try to close current connection
			closeConnection();
		}
		System.out.println("RPI Disconnected");

	}
}

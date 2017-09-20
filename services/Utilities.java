package cz3004MDP.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

import cz3004MDP.models.ArenaRobot;
import cz3004MDP.models.Grid;

import java.io.PrintStream;


public class Utilities implements ArenaRobot{
	
	public Grid[][] importMap(String fileName, Grid[][] grid){
		ArrayList<String> arena = new ArrayList<String>();
		arena = getArenaFromFile(FILENAME1);
		String exploreBin = "";
		String obstacleBin = "";
		int expCounter = 0;
		int obsCounter = 0;
		
		exploreBin = hexToBinary(arena.get(0));
		obstacleBin = hexToBinary(arena.get(1));
		
		// remove the start and end "11" from explore string
		exploreBin = exploreBin.substring(2, exploreBin.length()-2);
		

		for(int i = ROW-1; i>=0;i--){
			for(int j = 0; j<COLUMN; j++){
				if(exploreBin.charAt(expCounter) == '1'){
					// visited
					grid[i][j].setVisited(true);
					if(obstacleBin.charAt(obsCounter) == '1'){
						// contain obstacle = not a clear grid
						grid[i][j].setObstacle(true);
						grid[i][j].setClearGrid(false);
					}
					else{
						// does not contain obstacle = a clear grid
						grid[i][j].setObstacle(false);
						grid[i][j].setClearGrid(true);
					}
				}
				else{
					grid[i][j].setVisited(false);
					grid[i][j].setObstacle(false);
					grid[i][j].setClearGrid(false);
				}
				expCounter++;
				obsCounter++;
			}
		}
		return grid;
	}
	
	public ArrayList<String>  exportMap(Grid[][] grid){
		String exploreBin = "";
		String obstacleBin = "";
		ArrayList<String> result = new ArrayList<String>();

		for (int i = ROW-1; i >= 0; i--) {
			for (int j = 0; j < COLUMN; j++) {	
			
				if(!grid[i][j].isVisited())
					// grid is not yet visited
					exploreBin = concat(exploreBin, "0");
				else{
					exploreBin = concat(exploreBin, "1");
					if(!grid[i][j].isObstacle())
						// grid do not contain an obstacle
						obstacleBin = concat(obstacleBin, "0");
					else
						obstacleBin = concat(obstacleBin, "1");
				}
			}
		}
		// start and end string must contain "11"
		exploreBin = "11" + exploreBin + "11";
		String exploreHexResult = binaryToHex(exploreBin);
		result.add(exploreHexResult);

		obstacleBin = padBitstreamToFullByte(obstacleBin);
		String obstacleHexResult = binaryToHex(obstacleBin);
		result.add(obstacleHexResult);
		
		return result;
	}
	
	public String binaryToHex(String binary){
		int counter = 1;
		int sum = 0;
		
		String result = "";
		
		for(int i = 0; i <binary.length(); i++){
			if (counter == 1)
				sum = sum + Integer.parseInt(binary.charAt(i) + "")*8;
			else if (counter == 2)
				sum = sum + Integer.parseInt(binary.charAt(i) + "")*4;
			else if (counter == 3)
				sum = sum + Integer.parseInt(binary.charAt(i) + "")*2;
			else if(counter == 4 || i < binary.length()+1){
				sum = sum + Integer.parseInt(binary.charAt(i) + "")*4;
				
				counter = 0;
				if (sum < 10)
					result = concat(result,String.valueOf(sum));
				else if(sum == 10)
					result = concat(result,"A");
	            else if(sum == 11)
	            	result = concat(result,"B");
	            else if(sum == 12)
	            	result = concat(result,"C");
	            else if(sum == 13)
	            	result = concat(result,"D");
	            else if(sum == 14)
	            	result = concat(result,"E");
	            else if(sum == 15)
	            	result = concat(result,"F");
	            sum=0;
			}
			counter ++;
		}
		
		return result;
	}

	private String concat(String hex, String valueOf) {
		// TODO Auto-generated method stub
		hex = hex+valueOf;
		return hex;
	}
	
	public String hexToBinary(String hex){
		int length = hex.length()*4;
		
		String result = new BigInteger(hex, 16).toString(2);
		
		if (result.length() < length){
			int diff = length = result.length();
			String pad = "";
			for (int i = 0; i < diff; ++i){
				pad = pad.concat("0");
			}
			result = pad.concat(result);	
		}
		return result;
	}

	private String padBitstreamToFullByte(String bitstream)
	{
		if (bitstream.length() % 8 == 0)
			return bitstream;
		else
		{
			String fullByteBitStream = bitstream;
			int remainder = bitstream.length()%8;
			for(int i = 0; i<remainder; i++)
			{
				fullByteBitStream = concat(fullByteBitStream, "0");
			}
			return fullByteBitStream;
		}
		
	}

	public void saveArenaToFile(String fn, ArrayList<String> arenaDetails){		
		File file = new File(fn);
		
		try{
			if (file.exists()){
				file.delete();
				file.createNewFile();
			}
			
			PrintStream fileStream = new PrintStream(file);
			for (int i = 0; i < arenaDetails.size();i++){
				fileStream.println(arenaDetails.get(i));
			}
			fileStream.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public ArrayList<String> getArenaFromFile(String fn){
		ArrayList<String> result = new ArrayList<String>();
		File file = new File(fn);
		
		try{
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				result.add(sc.next());
			}
			sc.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return result;

	}
}
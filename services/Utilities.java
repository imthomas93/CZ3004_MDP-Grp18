package cz3004MDP.services;

import java.math.BigInteger;

import javax.management.timer.Timer;

public class Utilities {
	
	public void startTimer(Timer timer){
		System.out.println("timer start");
		timer.start();
	}
	
	public void stopTimer(Timer timer){
		System.out.println("timer stop");
		timer.stop();
	}
	
	public void ImportMap(){
		
	}
	
	public void ExportMap(){
		
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

	// NEED MODIFY
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
		
	}}


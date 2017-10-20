package Services;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JOptionPane;

import Model.Grid;
import Model.RobotArenaProtocol;
import Model.Arena;

public class Utilities {
	//Export map and import map functions 
		public String binToHex(String binary)
		{
			int count = 1;
		    int sum = 0;
		    String hex = "";
		    for(int i = 0; i < binary.length(); i++){
		        if(count == 1)
		            sum+=Integer.parseInt(binary.charAt(i) + "")*8;
		        else if(count == 2)
		            sum+=Integer.parseInt(binary.charAt(i) + "")*4;
		        else if(count == 3)
		            sum+=Integer.parseInt(binary.charAt(i) + "")*2;
		        else if(count == 4 || i < binary.length()+1){
		            sum+=Integer.parseInt(binary.charAt(i) + "")*1;
		            count = 0;
		            if(sum < 10)
		            	hex = Concat(hex,String.valueOf(sum));
		            else if(sum == 10)
		            	hex = Concat(hex,"A");
		            else if(sum == 11)
		            	hex = Concat(hex,"B");
		            else if(sum == 12)
		            	hex = Concat(hex,"C");
		            else if(sum == 13)
		            	hex = Concat(hex,"D");
		            else if(sum == 14)
		            	hex = Concat(hex,"E");
		            else if(sum == 15)
		            	hex = Concat(hex,"F");
		            sum=0;
		        }
		        count++;  
		    }
			return hex;
		}
		
		public String hexToBinary(String hex) {
			int len = hex.length() * 4;
		    String bin = new BigInteger(hex, 16).toString(2);

		    if(bin.length() < len){
		        int diff = len - bin.length();
		        String pad = "";
		        for(int i = 0; i < diff; ++i){
		            pad = pad.concat("0");
		        }
		        bin = pad.concat(bin);
		    }
		    return bin;
		}
		
		private String Concat(String a, String b) {
		        a += b;
		        return a;
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
					fullByteBitStream = Concat(fullByteBitStream, "0");
				}
				return fullByteBitStream;
			}
			
		}
		
		public void saveArenaToFile(String fileName, ArrayList<String> arenaDetails){
			File file = new File(fileName);
			try {
				if(file.exists() == true)
				{
					file.delete();
					file.createNewFile();
				}
				PrintStream fileStream = new PrintStream(file);
				for(int i = 0; i < arenaDetails.size(); i++)
				{
					fileStream.println(arenaDetails.get(i));
				}
		      fileStream.close();
		      Arena.appendMessage("Explored Arena is saved successfully. Running fastest path is allowed now.");
		      JOptionPane.showMessageDialog(null, "Explored Arena is saved successfully. Running fastest path is allowed now.");
		    } catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to save arena");
			}
		}
		
		public ArrayList<String> getArenaFromFile(String fileName)
		{
			ArrayList<String> arenaDetails = new ArrayList<String>(); 
			File file = new File(fileName); 	
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNext()){
					arenaDetails.add(sc.next());
				}
				sc.close();
				JOptionPane.showMessageDialog(null, "Import arena into simulator successfully");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to import arena from text file.");
			}
			return arenaDetails;
		}
			
		public ArrayList<String> exportMap(Grid[][] arenaDesign)
		{
			String exploreBinary = "";
			String obstacleBinary = "";
			for (int i = RobotArenaProtocol.ROW-1; i >= 0; i--) {
				for (int j = 0; j < RobotArenaProtocol.COLUMN; j++) {
							if(arenaDesign[i][j].getGridStatus()[0] == 0)
								exploreBinary = Concat(exploreBinary,"0");
							else
							{
								exploreBinary = Concat(exploreBinary,"1");
								if(arenaDesign[i][j].getGridStatus()[1]==0)
								{
									obstacleBinary = Concat(obstacleBinary,"0");
								}
								else
									obstacleBinary = Concat(obstacleBinary,"1");
							}
			        }
				}
				exploreBinary = "11" + exploreBinary +"11";
				String exploreHex = binToHex(exploreBinary);
				
				obstacleBinary = padBitstreamToFullByte(obstacleBinary);
				String obstacleHex= binToHex(obstacleBinary);
				
				ArrayList<String> arenaDetails = new ArrayList<String>();
				arenaDetails.add(exploreHex);
				arenaDetails.add(obstacleHex);
				return arenaDetails;
		}

		public void playExploreSuccessSound() {
			// TODO Auto-generated method stub
			try {
				File audioFile = new File("ExploreSuccess.wav");
				 
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				Clip audioClip = (Clip) AudioSystem.getLine(info);
				audioClip.open(audioStream);
				audioClip.start();
				
				//audioClip.close();
				//audioStream.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		public void playExploreSound() {
			// TODO Auto-generated method stub
			try {
				File audioFile = new File("Explore.m4a");
				 
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				Clip audioClip = (Clip) AudioSystem.getLine(info);
				audioClip.open(audioStream);
				audioClip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

}

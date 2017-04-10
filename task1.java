import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class task1 {
	
	static int windowSizeW = 10000;
	static double epsilon = 0.01;
	static int subWindow = -1;
	static int lineReal = 0;
	static int queryIndex = 0;
	static String stream1 = "...";
	static String query = "...";
	
	// save the information for query
	static ArrayList<Integer> queryTime = new ArrayList<Integer>();
	static ArrayList<String> queryIP = new ArrayList<String>();
	static ArrayList<Integer> queryWindow = new ArrayList<Integer>();
	static ArrayList<Integer> queryIpPart1 = new ArrayList<Integer>();
	
	// build the data structure to save the jumping window information
	static int[][] tempWindow = new int[256][101];
	
	// save all frequencies
	static ArrayList<Integer> frequencyAll = new ArrayList<Integer>();
	
	public static void main(String[] args) {
		// get the subwindow size
		JumpingWindow(windowSizeW, epsilon);
		
		/*****
		 * Load the query file and save information
		 *****/
		loadQuery(1);
		
		/***** 
		 * READ THE STREAM "file1.tsv"
		 * line by line
		 *****/
		File file = new File(stream1);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// read line by line until the end
			while ((tempString = reader.readLine()) != null) {
				
				// show the line number
				String[] parts = tempString.split("\\s+");
				String src = parts[0];
				String dest = parts[1];
					
				// change the srcIP to integer: e.g. 127.0.0.1 TO 2130706433
				String[] IP = src.split("\\.");
				int ipPart1 = Integer.parseInt(IP[0]);
							
				/*****
				 * case1: the line is not at the query time
				 *****/
				if(lineReal != queryTime.get(queryIndex)){
					insertEvent(ipPart1);
				}
				
				/*****
				 * case 2: the line is at the query time
				 *****/
				if(lineReal == queryTime.get(queryIndex)){
					
					insertEvent(ipPart1);
					
					int frequency = 0;
					if(queryWindow.get(queryIndex) == 0){
						frequency = getFreqEstimation(queryIpPart1.get(queryIndex));
					}else{
						frequency = getFreqEstimation(queryIpPart1.get(queryIndex), queryWindow.get(queryIndex));
					}
					
					frequencyAll.add(frequency);
					
					queryIndex++;
				}
				
				if(queryIndex == 200){
					System.out.println("The frequency result is " + frequencyAll);
					break;
				}
					
				lineReal++;            
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
	}
	
	
	public static void JumpingWindow(int windowSizeW, double epsilon){
		// get the jumping Window's size
		subWindow = (int) (windowSizeW * epsilon*2);
	} 
	
	
	// This is used to renew the matrix 
	static void insertEvent(int srcIP){
		
		if((lineReal % 100) != 0){
			tempWindow[srcIP][100] += 1;
		}else{
			// first, renew the right most column
			tempWindow[srcIP][100] += 1;
			// then, shift the matrix by one to the left, and delete the most left one
			for(int i = 0; i < 100; i++){
				for(int j = 0; j < 256; j++){
					tempWindow[j][i] = tempWindow[j][i+1];
				}
			}
			// reset the right most column of tempWindow to be all zero
			for(int j = 0; j < 256; j++){
				tempWindow[j][100] = 0;
			}
		}
		
	}
	
	
	// This is when the W = W1
	static int getFreqEstimation(int srcIP, int queryWindowSizeW1){
		int frequency = 0;
		int newWindowLine = lineReal % 100;
		int divisor = (queryWindowSizeW1 - newWindowLine)/100;		
		int extra = queryWindowSizeW1 - divisor*100 - newWindowLine;
    
		if((newWindowLine == 0)){
			if(extra == 0){
				for(int i = 0; i < divisor; i++){
					frequency += tempWindow[srcIP][99-i];
				}
			}else{
				for(int i = 0; i < divisor; i++){
					frequency += tempWindow[srcIP][99-i];
				}
				frequency += tempWindow[srcIP][99-divisor]/2;
			}
		}else{
			frequency += tempWindow[srcIP][100];
			if(extra == 0){
				for(int i = 0; i < divisor; i++){
					frequency += tempWindow[srcIP][99-i];
				}
			}else{
				for(int i = 0; i < divisor; i++){
					frequency += tempWindow[srcIP][99-i];
				}
				frequency += tempWindow[srcIP][99-divisor]/2;
			}
		}
		
		return frequency;
		
	}
	
	
	 // This is when the W = 10000
	static int getFreqEstimation(int srcIP){
		
		int frequency = 0;
		if((lineReal % 100) == 0){
			for(int i = 0; i < 100; i++){
				frequency += tempWindow[srcIP][i];
			}
		}else{
			for(int i = 1; i < 101; i++){
				frequency += tempWindow[srcIP][i];
			}
			frequency += tempWindow[srcIP][0]/2;
		}
		
		return frequency;
	}
	
	// This is used to save the query information
	public static void loadQuery(int start){
		if (start == 1) {
		File fileQuery = new File(query);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileQuery));
			String tempString = null;
			int line = 1;
			// read line by line until the end
			while ((tempString = reader.readLine()) != null) {
				// show the line number
				String[] parts = tempString.split("\\s+");
				String Qtime = parts[0]; 
				int queryTimeTemp = Integer.parseInt(Qtime);
				String IP = parts[1];
				String window = parts[2];
				int windowNum = Integer.parseInt(window);
					
				// add Qtime and IP to the arraylist
				queryTime.add(queryTimeTemp);
				queryIP.add(IP);
				queryWindow.add(windowNum);
				
				// save the first part of each queryIP
				String[] ips = IP.split("\\.");
				String part1 = ips[0];
				int part1Num = Integer.parseInt(part1);
				queryIpPart1.add(part1Num);
					
				line++;            
				}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		}
	}

}

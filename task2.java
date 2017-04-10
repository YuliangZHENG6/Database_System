import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Random;

public class task2 {
	
	static double epsilon = 0.01;
	static double pr1 = 0.1;
	static double pr2 = 0.9;
	static int avaSpace =  (int) Math.pow(10,7);
	static String stream1 = "...";
	static String query = "...";
	
	static ArrayList<Integer> frequencyAll = new ArrayList<Integer>();
	
	// parameters for using bloom filter
	static int n = 400000; 
	static int bitArrayLength = (int) (Math.log(pr1)/Math.log(0.6185)*n); //m = 1.9170e+06
	static int queryNum = 200; // n = 200
	static int hashFuncNum = (int) (Math.log(2)*Math.log(pr1)/Math.log(0.6185)); //k = 3
	static int[] bitArray = new int[bitArrayLength];
	//create three hash function for the bloom filter method:
    // Using that a*x mod m
	static long a1 = 7; //Math.random();
	static long a2 = 3; //Math.random();
	static long a3 = 5; //Math.random();
	static long b1 = 23;
	static long b2 = 2445;
	static long b3 = 630;
	
	// parameters for using CM
	static int w = (int) Math.ceil(Math.exp(1)/epsilon); // w = 272
	static int d = (int) Math.ceil(Math.log(1/(1-pr2))); // d = 3
	static int[][] bitMap = new int[d][w];
	// for hash function
	static long aCM1 = 7; //Math.random();
	static long aCM2 = 3; //Math.random();
	static long aCM3 = 4; //Math.random();
	static long bCM1 = 57;
	static long bCM2 = 201;
	static long bCM3 = 5;
	
  
	// save the information for query
	static ArrayList<Integer> queryTime = new ArrayList<Integer>();
	static ArrayList<String> queryIP = new ArrayList<String>();
	static ArrayList<Long> queryNumber = new ArrayList<Long>();
	
	
	public static void main(String[] args) {
		
		long ipNumber = 0L;
			
		/***** 
		 * Two parameters: queryTime & queryIP
		 *****/
		loadQuery(1);
		
		/***** READ THE STREAM line by line
		 * "file1.tsv"
		 * Using Bloom Filter to decide whether the query ip exists or not
		 * Using CM to calculate the estimate frequency
		 *****/
		File file = new File(stream1);
		BufferedReader reader = null;
		int queryIndex = 0; // to monitor the query process
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// read line by line until the end
			while ((tempString = reader.readLine()) != null) {
				
				ipNumber = 0L;
				
				// show the line number
				String[] parts = tempString.split("\\s+");
				String src = parts[0];
   			String dest = parts[1];
				
				// change the srcIP to integer: e.g. 127.0.0.1 TO 2130706433
				String[] IP = src.split("\\.");
				int[] power = {24, 16, 8, 0};
				for(int i = 0; i < 4; i = i+1) {
					String ip = IP[i];
					long num = Integer.parseInt(ip);
					long addNum = (int) (num*(Math.pow(2, power[i])));
					ipNumber += addNum;
				}				
				
				/*****
				 * case1: the line is not at the query time
				 *****/
				if(line != queryTime.get(queryIndex)){
					addArrival(ipNumber);
				}
				
				/*****
				 * case 2: the line is at the query time
				 *****/
				if(line == queryTime.get(queryIndex)){
					
					addArrival(ipNumber);
					
					System.out.println("Arrived and the query index is " + queryIndex);
					/*STEP 1:
					 * check whether it shows before in the stream
					 * using Bloom Filter
					 */
					/*STEP 2:
					 * check the frequency of this src ip
					 * using CM
					 */
					long ipQ = queryNumber.get(queryIndex);
					boolean appearance = isAppear(ipQ);
					if(appearance){
						int freq = getFreqEstimation(ipQ);
						frequencyAll.add(freq);
						System.out.println("Query time at " + queryTime.get(queryIndex) + " , the frequency for " + queryIP.get(queryIndex) + " is " + freq);
					}else{
						frequencyAll.add(0);
						System.out.println("Query time at " + queryTime.get(queryIndex) + " , it never appears before.");
					}
					
					
					queryIndex++;
				}
				
				if(queryIndex == 200){
					System.out.println("The frequency result is " + frequencyAll);
					break;
				}
					
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
	
	// This is used to check if the space is enough
	public task2(int availableSpace, float pr1, float epsilon, float pr2) throws OutOfMemoryError, Exception{
		
		long memoryNow = (bitArrayLength + w*d)*4;
		System.out.println("Data Structure Memory NOW : " + memoryNow);
		if(memoryNow > availableSpace) {
			throw new Exception("The memory is too large.");
		}
	}
	
	
	// This is used to build the "bit array for Bloom Filter", the "matrix" for CM 
	static void addArrival(long key) {
		/*****
		 * Bloom Filter
		 *****/
		// Create three hash function: map the bitArray and the key
		// In the mode: a*key mod m
		
		// 1st:
		int index1 = (int) ((a1*key + b1)% bitArrayLength);
		bitArray[index1] = 1;
		// 2nd:
		int index2 = (int) ((a2*key + b2)% bitArrayLength);
		bitArray[index2] = 1;
		//3rd:
		int index3 = (int) ((a3*key +b3)% bitArrayLength);
		bitArray[index3] = 1;
			
		
		/*****
		 * CM
		 *****/
		// need d = 3 hash function to map the bitMap row by row
		
		// 1st:
		int indexCM1 = (int) ((aCM1*key +bCM1) % w);
		bitMap[0][indexCM1] += 1;
		// 2nd:
		int indexCM2 = (int) ((aCM2*key +bCM2)% w);
		bitMap[1][indexCM2] += 1;
		//3rd:
		int indexCM3 = (int) ((aCM3*key +bCM3)% w);
		bitMap[2][indexCM3] += 1;
	
	}
	
	
	// This is using the CM to store the frequency
	static int getFreqEstimation(long key) {
		
		int frequency = 0;
		
		// 1st:
		int indexCM1 = (int) ((aCM1*key +bCM1) % w);
		int result1 = bitMap[0][indexCM1];
		// 2nd:
		int indexCM2 = (int) ((aCM2*key + bCM2) % w);
		int result2 = bitMap[1][indexCM2];
		//3rd:
		int indexCM3 = (int) ((aCM3*key +bCM3) % w);
		int result3 = bitMap[2][indexCM3];
	    
	  frequency = Math.min(result1, result2);
	  frequency = Math.min(frequency, result3);
		
		return frequency;
	}

	
	// This is used to load the query file
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
				String QIP = parts[1];
				
				// add Qtime and IP to the arraylist
				queryTime.add(queryTimeTemp);
				queryIP.add(QIP);
				
				// change the queryIP to integer: e.g. 127.0.0.1 TO 2130706433
				String[] IP = QIP.split("\\.");
				int[] power = {24, 16, 8, 0};
				long ipNumber = 0;
				for(int i = 0; i < 4; i = i+1) {
					String ip = IP[i];
					long num = Integer.parseInt(ip);
					long addNum = (int) (num*(Math.pow(2, power[i])));
					ipNumber += addNum;
				}
				queryNumber.add(ipNumber);
					
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
	
	
	// This is used to check if this ip appears before
	public static boolean isAppear(long key) {
		
		// 1st:
		int index1 = (int) ((a1*key + b1) % bitArrayLength);
		// 2nd:
		int index2 = (int) ((a2*key + b2) % bitArrayLength);
		//3rd:
		int index3 = (int) ((a3*key + b3)% bitArrayLength);
		
		if(bitArray[index1] == 1 && bitArray[index2] == 1 && bitArray[index3] == 1){
			return true;
		}else{
			return false;
		}	
	}
	
	
}


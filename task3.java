import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class task3 {
	
	static double pr = 0.1;
	static String stream1 = "...";
	static String query = "...";
	
	static ArrayList<Integer> appearanceAll = new ArrayList<Integer>();
	static ArrayList<Boolean> appearanceAllBoolean = new ArrayList<Boolean>();
	
	
	// parameters for using bloom filter
	static int n = 400000; 
	static int bitArrayLength = (int) (Math.log(pr)/Math.log(0.6185)*n); 
	static int queryNum = 200; 
	static int hashFuncNum = (int) (Math.log(2)*bitArrayLength/queryNum);
	
	//create three hash function for the bloom filter method:
	// Using that a*x mod m
	static long a1 = 2; //Math.random();
	static long a2 = 3; //Math.random();
	static long a3 = 5; //Math.random();
	static long b1 = 23;
	static long b2 = 2524;
	static long b3 = 674;
	
	// create different arrays for bloom filter:
	// Using a matrix instead, where each row refers to one array
	static int[][] bitArrays= new int[32][bitArrayLength];
	
	
	// save the information for query
	static long[] queryStart = new long[queryNum];
	static long[] queryEnd = new long[queryNum];
	static ArrayList<String> queryStartIP = new ArrayList<String>();
	static ArrayList<String> queryEndIP = new ArrayList<String>();
	
	public static void main(String[] args) {
  
		/***** Call "task2.java" to return the queries
		 * Two parameters: queryTime & queryIP
		 *****/
		loadQuery(1);
		
		/***** READ THE STREAM line by line
		 * "file1.tsv"
		 * create the bloom filter array
		 *****/
		File file = new File(stream1);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// read line by line until the end
			while ((tempString = reader.readLine()) != null) {
				
				long ipNumber = 0L;
				
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
				
				// add this ipSrc to the bloom filter array
				insertValue(ipNumber);
					
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
		
		/*****
		 * Now, start to do the range query
		 *****/
		for(int i = 0; i < 200; i++){
			boolean isExist = existsInRange(queryStart[i], queryEnd[i]);
			if(isExist){
				appearanceAll.add(1);
			}else{
				appearanceAll.add(0);
			}
			appearanceAllBoolean.add(isExist);
			System.out.println("This is query " + i + ", it's appearance result is " + isExist);
		}
		
		System.out.println("The result for all is  " + appearanceAll);
		System.out.println(appearanceAllBoolean);
	}

	// Compute some parameters ???
	public task3(double pr) {
	}
	
	
	// This create the bloom filter array
	static void insertValue(long key) {
		
		/*****
		 * Bloom Filter
		 *****/
		// Create three hash function: map the bitArray and the key
		// In the mode: a*key mod m
		
		for(int i = 0; i < 32; i++){
			long keyNew = (long) (key/Math.pow(2, i));
			// 1st:
			int index1 = (int) ((a1*keyNew + b1) % bitArrayLength);
			bitArrays[i][index1] = 1;
			// 2nd:
			int index2 = (int) ((a2*keyNew + b2) % bitArrayLength);
			bitArrays[i][index2] = 1;
			//3rd:
			int index3 = (int) ((a3*keyNew + b3) % bitArrayLength);
			bitArrays[i][index3] = 1;
		}
		
	} 
	
	
	static boolean existsInRange(long l, long r) {
		
		for(int i = 31; i >= 0; i-- ){
			long keyL = (long) (l/Math.pow(2, i));
			long keyR = (long) (r/Math.pow(2, i));
			
			/***** 
			 * check if can occupy a whole chunk
			 * 1. if so:
			 * (1) if return true => Yes, exist; 
			 * (2) if return false => check for next chunk, if possible; or return false 
			 * 2. if not:
			 * check for next layer
			 *****/
			for (long j = keyL; j <= keyR; j++){
				long keyTest = j;
				// check is at ith layer, this chunk exists
				boolean isExistThisChunk = isResult(i, keyTest);
				if(isExistThisChunk){
					// 1. this chunk is contained complete in range:
					long left = (long) (j*Math.pow(2, i));
					long right = (long) (left + Math.pow(2, i) -1);
					if((l <= left) && (right <= r)){
						return true;
					}
					// 2. this chunk is not contained complete, so, search for next layer	
				}
			}
		}
		
		return false;	
	}
	
	
	// This is used to test if one key exists in a specific layer of sketch
	static boolean isResult(int layer, long key){
		
		long j = key;
		int i = layer;
		
		// 1st:
		int index1 = (int) ((a1*j + b1) % bitArrayLength);
		int result1 = bitArrays[i][index1];
		// 2nd:
		int index2 = (int) ((a2*j + b2) % bitArrayLength);
		int result2 =bitArrays[i][index2];
		//3rd:
		int index3 = (int) ((a3*j + b3) % bitArrayLength);
		int result3 = bitArrays[i][index3];
		
		// decide
		if((result1 == 1) && (result2 == 1) && (result3 == 1)){
			return true;	
		}else{
			return false;
		}
		
	}
	
	
	// This is used to load the query file
	public static void loadQuery(int start){
		
			if (start == 1) {
			File fileQuery = new File(query);
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(fileQuery));
				String tempString = null;
				int line = 0;
				// read line by line until the end
				while ((tempString = reader.readLine()) != null) {
					
					long ipNumberSrc = 0L;
					long ipNumberDest = 0L;
					
					// show the line number
					String[] parts = tempString.split("\\s+");
					String src = parts[0]; 
					String dest = parts[1];
					
					/***** 
					 * add src and dest to the arraylist
					 *****/
					queryStartIP.add(src);
					queryEndIP.add(dest);
					
					/***** 
					 * change them to be long
					 *****/
					String[] srcs = src.split("\\.");
					String[] dests = dest.split("\\.");
					int[] power = {24, 16, 8, 0};
					for(int i = 0; i < 4; i++) {
						String srcNow = srcs[i];
						String destNow = dests[i];
						//
						long numSrc = Integer.parseInt(srcNow);
						long numDest = Integer.parseInt(destNow);
						//
						long addNumSrc = (int) (numSrc*(Math.pow(2, power[i])));
						long addNumDest = (int) (numDest*(Math.pow(2, power[i])));
						//
						ipNumberSrc += addNumSrc;
						ipNumberDest += addNumDest;
					}
					
					queryStart[line] = ipNumberSrc;
					queryEnd[line] = ipNumberDest;
						
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class FileDetails {

	int fileNum;
	int category;
	int loc[][];
	int patternSize = MyRandom.getRandomNum(FileConstants.MIN_BLOCK_SIZE, FileConstants.MAX_BLOCK_SIZE);
	ArrayList<Integer> pattern;
	int[][] deviantPattern;
	
	public FileDetails(int f, int c, int[][] l) {	
		
		fileNum = f;
		category = c;
		
		int length = l.length;
		loc = new int[length][l[0].length];
		
		for(int i = 0; i < length; i++) {
			for(int j = 0; j < l[0].length; j++) {
				loc[i][j] = l[i][j];
			}
		}
		
		//Assigning the blocks
		ArrayList<Integer> blocks = new ArrayList<Integer>();
		pattern = new ArrayList<Integer>();
		for(int i = 0; i < FileConstants.BLOCK_SIZE; i++) {
			blocks.add(i);
		}
		Collections.shuffle(blocks);
		
		for(int i = 1; i <= patternSize; i++) {
			pattern.add(blocks.get(i));
		}
		
		//Assigning the deviant blocks
		deviantPattern = new int[patternSize*3][patternSize];
		for(int i = 0; i < 3*patternSize; i++) {
			int random = MyRandom.getRandomNum(1, FileConstants.BLOCK_SIZE);
			
			while(pattern.contains(random)) {
				random = MyRandom.getRandomNum(1, FileConstants.BLOCK_SIZE);
			}
			
			int k = i/3;
			
			for(int j = 0; j < patternSize; j++) {
				if(j == k) {
					deviantPattern[i][j] = random;
				}
				else {				
					deviantPattern[i][j] = pattern.get(j);
				}
			}
			
			
		}
				
	}
	
	public String getPattern() {
		
		String temp = "";
		Iterator<Integer> it  = pattern.iterator();
		while(it.hasNext()) {
			temp += ", B"+it.next();
		}
		return temp;
	}
	
	
	public String[] getDeviantPattern() {
		String[] temp = new String[deviantPattern.length];
		
		for(int i = 0; i < deviantPattern.length; i++) {
			temp[i] = "";
			for(int j = 0; j < deviantPattern[0].length; j++) {
				temp[i] += ", B"+deviantPattern[i][j];
			}
		}		
		return temp;
	}
	
	
	static int getRack(int node) {
		int r = ((node - node % 10) / 10) + 1;
		return r;
	}
	
	static int getStartingIndex(int node) {
		int i = (node - node % 10) + 1;
		return i;
	}
	
	public String toString() {
		String str = " F" + fileNum;
		str += " Category : " + category + "\n";
		
		for(int i = 0; i < loc.length; i++) {
			for(int j = 0; j < loc[0].length; j++) {
				str += loc[i][j] + " : ";
			}
			str += "\n";
		}
		str += "\n";
		
		System.out.println(pattern);
		/*for(int i = 0; i < deviantPattern.length; i++) {
			for(int j = 0; j < deviantPattern[0].length; j++) {
				System.out.print(deviantPattern[i][j] + "\t");
			}
			System.out.println();
		}*/
		
		return str;
	}
	
}

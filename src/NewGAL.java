import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NewGAL	{
	
	ArrayList<Integer> cat1;
	ArrayList<Integer> cat2;
	ArrayList<Integer> cat3;
	ArrayList<Integer> files;
	ArrayList<Integer> nodes;
	
	HashMap<Integer, FileDetails> fileMap;
	
	ArrayList<String> fileLog;

	SystemConfig systemConfig;
	
	public NewGAL() {
		
		systemConfig = SystemConfig.getInstance();
		files = new ArrayList<Integer>();
		fileLog = new ArrayList<String>();
		fileMap = new HashMap<Integer, FileDetails>();	
		
		//Assigning all the nodes to node variable
		nodes = new ArrayList<Integer>();
		for(int i = FileConstants.MIN_DATA_NODE; i <= FileConstants.MAX_DATA_NODE; i++) {
			nodes.add(i);
		}
		Collections.shuffle(nodes);
		
		categorize();
		cat1Assignment();		
		cat2Assignment();
		
		getAccessLog();
		writeToCSV();

	}
	
	public void categorize() {
		
		//Assigning files to array
		for(int i = 1; i <= systemConfig.getFiles(); i ++) {
		files.add(i);
		}
		Collections.shuffle(files);
				
		cat1 = new ArrayList<Integer>();
		cat2 = new ArrayList<Integer>();
		cat3 = new ArrayList<Integer>();
		
		int totalFiles = systemConfig.getFiles();
		int cat1Files = (FileConstants.CAT_1_PERCENTAGE*totalFiles)/100;
		int cat2Files = (FileConstants.CAT_2_PERCENTAGE*totalFiles)/100;
		int cat3Files = (FileConstants.CAT_3_PERCENTAGE*totalFiles)/100;
						
		for(int i = 0; i < cat1Files; i ++) {
			cat1.add(files.get(i));
		}
		for(int i = cat1Files; i < (cat1Files + cat2Files); i++) {
			cat2.add(files.get(i));
		}
		for(int i = (cat1Files + cat2Files); i < (cat1Files + cat2Files + cat3Files); i++) {
			cat3.add(files.get(i));
		}
		
	}
	
	public void cat1Assignment() {
		for(int i = 0; i < cat1.size(); i++) {
			assignDataNode(cat1.get(i), 1);		
		}
	}
	public void cat2Assignment() {
		for(int i = 0; i < cat2.size(); i++) {
			assignDataNode(cat2.get(i), 2);		
		}
	}
	
	public void assignDataNode(int file, int category) {
		
		int tempNode1, tempNode2;
		int tempRack1, tempRack2, tempRack3, tempRack4;
		int[][] location;
		
		if(category == 1) {
			
			location = new int[4][2];
			
		//First Location
			tempNode1 = nodes.get(0);
			tempRack1 = FileDetails.getRack(tempNode1);
			location[0][0] = tempRack1; location[0][1] = tempNode1;
			nodes.remove(0);
			
		//Second Location
			tempNode2 = nodes.get(0);
			tempRack2 = FileDetails.getRack(tempNode2);
			
			while(tempRack1 == tempRack2) {
				tempNode2 = nodes.get(MyRandom.getRandomNum(0, nodes.size()));
				tempRack2 = FileDetails.getRack(tempNode2);
			}
			
			location[1][0] = tempRack2; location[1][1] = tempNode2;
			nodes.remove(new Integer(tempNode2));
			
		//Third Location
			tempNode2 = MyRandom.getRandomNum(FileDetails.getStartingIndex(tempNode1), FileDetails.getStartingIndex(tempNode1) + 9);
			while(tempNode2 == tempNode1) {
				tempNode2 = MyRandom.getRandomNum(FileDetails.getStartingIndex(tempNode1), FileDetails.getStartingIndex(tempNode1) + 9);				
			}
			tempRack3 = FileDetails.getRack(tempNode2);
			location[2][0] = tempRack3; location[2][1] = tempNode2;
			nodes.remove(new Integer(tempNode2));
			
		//Fourth Location
			tempNode2 = nodes.get(0);
			tempRack4 = FileDetails.getRack(tempNode2);
			
			while(tempRack4 == tempRack1 || tempRack4 == tempRack2) {
				tempNode2 = nodes.get(MyRandom.getRandomNum(0, nodes.size()));
				tempRack4 = FileDetails.getRack(tempNode2);
			}
			
			location[3][0] = tempRack4; location[3][1] = tempNode2;
			nodes.remove(new Integer(tempNode2));
			
			fileMap.put(file, new FileDetails(file, category, location));
			
		}
		
		else if(category == 2) {
			
			location = new int[3][2];
			
		//First Location
			tempNode1 = nodes.get(0);
			tempRack1 = FileDetails.getRack(tempNode1);
			location[0][0] = tempRack1; location[0][1] = tempNode1;
			nodes.remove(0);
			
		//Second Location
			tempNode2 = nodes.get(0);
			tempRack2 = FileDetails.getRack(tempNode2);
			
			while(tempRack1 == tempRack2) {
				tempNode2 = nodes.get(MyRandom.getRandomNum(0, nodes.size()));
				tempRack2 = FileDetails.getRack(tempNode2);
			}
			
			location[1][0] = tempRack2; location[1][1] = tempNode2;
			nodes.remove(new Integer(tempNode2));
			
		//Third Location
			tempNode2 = MyRandom.getRandomNum(FileDetails.getStartingIndex(tempNode1), FileDetails.getStartingIndex(tempNode1) + 9);
			while(tempNode2 == tempNode1) {
				tempNode2 = MyRandom.getRandomNum(FileDetails.getStartingIndex(tempNode1), FileDetails.getStartingIndex(tempNode1) + 9);				
			}
			tempRack3 = FileDetails.getRack(tempNode2);
			location[2][0] = tempRack3; location[2][1] = tempNode2;
			nodes.remove(new Integer(tempNode2));
			
			
			fileMap.put(file, new FileDetails(file, category, location));
		}
	}
	
	public void getAccessLog() {
		
		for(int i = 0; i < cat1.size(); i++) {
			FileLog(cat1.get(i));		
			System.out.println(fileMap.get(cat1.get(i)));
		}
		
		for(int i = 0; i < cat2.size(); i++) {
			FileLog(cat2.get(i));
			System.out.println(fileMap.get(cat2.get(i)));
		}
	}
	
	public void FileLog(int fileNum) {

		FileDetails tempFileDetails = fileMap.get(fileNum);
		if(tempFileDetails.category == 1) {
			
			//First Location
			int randomNumber = MyRandom.getRandomNum(FileConstants.CAT_11_MIN, FileConstants.CAT_11_MAX);
			int randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_11_MIN_PERCENTAGE, FileConstants.CAT_11_MAX_PERCENTAGE);
			int patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_11_MIN_PERCENTAGE, FileConstants.CAT_11_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 0);
			
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[0]);
			
			//Second Location
			randomNumber = MyRandom.getRandomNum(FileConstants.CAT_12_MIN, FileConstants.CAT_12_MAX);
			randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_12_MIN_PERCENTAGE, FileConstants.CAT_12_MAX_PERCENTAGE);
			patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_12_MIN_PERCENTAGE, FileConstants.CAT_12_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 1);
				
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[1]);
			
			//Third Location
			randomNumber = MyRandom.getRandomNum(FileConstants.CAT_13_MIN, FileConstants.CAT_13_MAX);
			randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_13_MIN_PERCENTAGE, FileConstants.CAT_13_MAX_PERCENTAGE);		
			patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_13_MIN_PERCENTAGE, FileConstants.CAT_13_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 2);
			
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[2]);
			
			//Fourth Location
			randomNumber = MyRandom.getRandomNum(FileConstants.CAT_14_MIN, FileConstants.CAT_14_MAX);
			randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_14_MIN_PERCENTAGE, FileConstants.CAT_14_MAX_PERCENTAGE);
			patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_14_MIN_PERCENTAGE, FileConstants.CAT_14_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 3);
				
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[3]);	
			
			
		}
		else if(tempFileDetails.category == 2) {
			//First Location
			int randomNumber = MyRandom.getRandomNum(FileConstants.CAT_21_MIN, FileConstants.CAT_21_MAX);
			int randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_21_MIN_PERCENTAGE, FileConstants.CAT_21_MAX_PERCENTAGE);
			int patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_21_MIN_PERCENTAGE, FileConstants.CAT_21_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 0);
			
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[0]);
			
			//Second Location
			randomNumber = MyRandom.getRandomNum(FileConstants.CAT_22_MIN, FileConstants.CAT_22_MAX);
			randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_22_MIN_PERCENTAGE, FileConstants.CAT_22_MAX_PERCENTAGE);
			patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_22_MIN_PERCENTAGE, FileConstants.CAT_22_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 1);
				
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[1]);
			
			//Third Location
			randomNumber = MyRandom.getRandomNum(FileConstants.CAT_23_MIN, FileConstants.CAT_13_MAX);
			randomPercentage = MyRandom.getRandomNum(FileConstants.CAT_23_MIN_PERCENTAGE, FileConstants.CAT_23_MAX_PERCENTAGE);		
			patternPercentage = MyRandom.getRandomNum(FileConstants.CAT_23_MIN_PERCENTAGE, FileConstants.CAT_23_MAX_PERCENTAGE);
			getRandomBlock(tempFileDetails, randomNumber, patternPercentage, 2);
			
			getCat3Log(randomNumber, randomPercentage, tempFileDetails.loc[2]);
		}
		
	}
	
	public void getCat3Log(int randomNumber, int randomPercentage, int loc[]) {
		
	int x = (randomNumber*(100 - randomPercentage))/randomPercentage;

		int type1 = MyRandom.getRandomNum(FileConstants.CAT_3_MIN, FileConstants.CAT_3_MAX);
		int type2 = x - type1;
		
		int type1FileNumber = MyRandom.getRandomNum(0, cat3.size());
		int type2FileNumber = MyRandom.getRandomNum(0, cat3.size());
		while(type2FileNumber == type1FileNumber) {
			type2FileNumber = MyRandom.getRandomNum(0, cat3.size());
		}
		
		for(int i = 0; i < type1; i++) {
			getCat3RandomBlock(loc, cat3.get(type1FileNumber));
		}
		for(int i = 0; i < type2; i++) {
			getCat3RandomBlock(loc, cat3.get(type2FileNumber));
		}
		
	}
	
	public void getCat3RandomBlock(int[] loc, int fileNumber) {
		
		ArrayList<Integer> blocks = new ArrayList<Integer>();
		String tempBlocks = "";
		int numberOfBlocks = MyRandom.getRandomNum(FileConstants.MIN_BLOCK_SIZE, FileConstants.MAX_BLOCK_SIZE);
		
		for(int i = 1; i <= FileConstants.BLOCK_SIZE; i++) {
			blocks.add(i);
		}
		Collections.shuffle(blocks);
		
		for(int i = 0; i < numberOfBlocks; i++) {
			tempBlocks += ", B"+blocks.get(i);
		}
		fileLog.add("R" + loc[0] + ", D" + loc[1] + ", F" + fileNumber + tempBlocks);			
	}
	
	public void getRandomBlock(FileDetails fileDetails, int randomNumber, int patternPercentage, int row) {
		
		String pattern = fileDetails.getPattern();
		String[] deviantPattern = fileDetails.getDeviantPattern();
		
		for(int i = 0; i < (randomNumber*patternPercentage)/100; i++) {
			fileLog.add("R" + fileDetails.loc[row][0] + ", D" + fileDetails.loc[row][1] + ", F" + fileDetails.fileNum + pattern);
		}
		
		for(int i = 0; i < ((100 - patternPercentage)*randomNumber)/100; i++) {
			int k = i % deviantPattern.length;
			fileLog.add("R" + fileDetails.loc[row][0] + ", D" + fileDetails.loc[row][1] + ", F" + fileDetails.fileNum + deviantPattern[k]);			
		}
		
		
		
		//fileLog.add("R" + loc[0] + ", D" + loc[1] + ", F" + fileNumber + blocks);
	}
	
	public void writeToCSV() {
		Collections.shuffle(fileLog);
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/Arish/Desktop/Output.csv"))){			
			System.out.println("Writing to CSV File...");
			
			for(int i = 0; i < fileLog.size(); i++) {
				writer.write(fileLog.get(i));
				writer.newLine();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished Writing...");
		System.out.println(fileLog.size());
	}
	
}
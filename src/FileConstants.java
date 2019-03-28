
public interface FileConstants {
	
	SystemConfig systemConfig = SystemConfig.getInstance();
	
	//Block sizes
	static final int MIN_BLOCK_SIZE = systemConfig.getMinSessionSize();
	static final int MAX_BLOCK_SIZE = systemConfig.getMaxSessionSize();
	static final int BLOCK_SIZE = systemConfig.getBlocks();
	
	//Number of Nodes
	static final int MIN_DATA_NODE = 1;
	static final int MAX_DATA_NODE = 200;
	
	//Percentage Assignment
	static final int CAT_1_PERCENTAGE = 5;
	static final int CAT_2_PERCENTAGE = 20;
	static final int CAT_3_PERCENTAGE = 75;
	
	//Category 1 Assignment
	static final int CAT_11_MIN = 4000;
	static final int CAT_11_MAX = 4500;
	static final int CAT_11_MIN_PERCENTAGE = 81;
	static final int CAT_11_MAX_PERCENTAGE = 85;
	
	static final int CAT_12_MIN = 4500;
	static final int CAT_12_MAX = 5000;
	static final int CAT_12_MIN_PERCENTAGE = 86;
	static final int CAT_12_MAX_PERCENTAGE = 90;
	
	static final int CAT_13_MIN = 3500;
	static final int CAT_13_MAX = 4000;
	static final int CAT_13_MIN_PERCENTAGE = 76;
	static final int CAT_13_MAX_PERCENTAGE = 80;
	
	static final int CAT_14_MIN = 3000;
	static final int CAT_14_MAX = 3500;
	static final int CAT_14_MIN_PERCENTAGE = 70;
	static final int CAT_14_MAX_PERCENTAGE = 75;
	
	//Category 2 Assignment
	static final int CAT_21_MIN = 4000;
	static final int CAT_21_MAX = 4500;
	static final int CAT_21_MIN_PERCENTAGE = 64;
	static final int CAT_21_MAX_PERCENTAGE = 66;
	
	static final int CAT_22_MIN = 3500;
	static final int CAT_22_MAX = 4000;
	static final int CAT_22_MIN_PERCENTAGE = 67;
	static final int CAT_22_MAX_PERCENTAGE = 69;
	
	static final int CAT_23_MIN = 3100;
	static final int CAT_23_MAX = 3500;
	static final int CAT_23_MIN_PERCENTAGE = 61;
	static final int CAT_23_MAX_PERCENTAGE = 63;
	
	//Category 3 Assignment
	static final int CAT_3_MIN = 300;
	static final int CAT_3_MAX = 500;
	static final int CAT_3_MINPERCENTAGE = 0;
	static final int CAT_3_MAXPERCENTAGE = 60;
	
}

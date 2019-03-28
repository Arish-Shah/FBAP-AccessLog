

public class MyRandom {
	
	static int fileSize;
	static int blockSize;
	static int datanodeSize;
	static int rackSize;
	static int minSessionSize;
	static int maxSessionSize;
	
	static	{
		SystemConfig systemConfig = SystemConfig.getInstance();
		fileSize = systemConfig.getFiles();
		blockSize = systemConfig.getBlocks(); 
		datanodeSize = systemConfig.getDataNodes();
		rackSize = systemConfig.getRacks();
		minSessionSize = systemConfig.getMinSessionSize();
		maxSessionSize = systemConfig.getMaxSessionSize();
	}
	public static int getRandomFile()	{
	
		int fileNum = (int)((Math.random()+ 1.0 / fileSize) * fileSize ) % fileSize + 1;
		return fileNum;
	}
	public static int getRandomBlock()	{
		//blockSize = 1000;
		int blockNum = (int)(( Math.random() + 1.0 / blockSize ) * blockSize ) % blockSize + 1 ;
		return blockNum;
	}
	public static int getRandomDatanode(int rack)	{
		int dn = (int) (datanodeSize / rackSize);
		int datanodeNum = (int)(((rack-1) * dn) + ((Math.random() + 1.0 / dn ) * dn ) % dn + 1) ;
		return datanodeNum;
	}
	public static int getRandomNum(int min,int max)	{	// random number in range inclusive.
		
		int num = (int)(( Math.random() + 1.0 / (max-min) ) * (max*100) ) % (max-min) + min;
		return num;
	}
	public static int getRandomRack()	{
		int rackNum = (int)(( Math.random() + 1.0 / rackSize ) * rackSize ) % rackSize + 1 ;
		return rackNum;
	}
	public static int getRandomSessionSize()	{
		int sessionDiff = maxSessionSize - minSessionSize;
		int sessionSize = (int)(( Math.random() + 1.0 / sessionDiff) * (maxSessionSize*100)) % sessionDiff + minSessionSize;
		return sessionSize;
	}
	
	
}

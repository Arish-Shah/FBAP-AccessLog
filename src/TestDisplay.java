

public class TestDisplay {

	public static void displaySystemConfig(SystemConfig systemConfig)	{
		
		System.out.println("************************************************************");
		System.out.println("************* System Configuraton **************************");
		System.out.println("************************************************************");
		System.out.println("System Configuration is : ");
		System.out.println("No of Racks = "+systemConfig.getRacks());
		System.out.println("No of DataNodes = "+systemConfig.getDataNodes());
		System.out.println("Cluster Size = "+systemConfig.getClusterSize());
		System.out.println("No of Files = "+systemConfig.getFiles());
		System.out.println("No of Blocks = "+systemConfig.getBlocks());
		System.out.println("Min Session Size = "+systemConfig.getMinSessionSize());
		System.out.println("Max Session Size = "+systemConfig.getMaxSessionSize());
		System.out.println("************************************************************");
		System.out.println();
	}
	public static void displayMyRandomTest() {
		System.out.println("************************************************************");
		System.out.println("************* Random Number Testing ************************");
		System.out.println("************************************************************");
		System.out.println("Random Block : "+MyRandom.getRandomBlock());
		System.out.println("Random File : "+MyRandom.getRandomFile());
		int rack = MyRandom.getRandomRack();
		System.out.println("Random Rack : "+rack);
		System.out.println("Random Datanode in Rack "+rack+" is :"+MyRandom.getRandomDatanode(rack));
		System.out.println("Random Num in 30 and 60 is : "+MyRandom.getRandomNum(30,60));
		System.out.println("Random Session Size is : "+MyRandom.getRandomSessionSize());
		System.out.println("************************************************************");
		System.out.println();
	}	
}

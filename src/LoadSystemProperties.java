
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LoadSystemProperties {
	
	static Properties systemProperties;
		
	public static SystemConfig loadProperties(File file)	{
		
		systemProperties = new Properties();
		SystemConfig systemConfig = SystemConfig.getInstance();
		
		try (FileReader fis = new FileReader(file))	{
			
			systemProperties.load(fis);
			setSystemConfig(systemConfig);
			return systemConfig;
		 }	
		 catch (FileNotFoundException e2) {
			
			 	e2.printStackTrace();
		 }	
		 catch (IOException e) {
			
				e.printStackTrace();
		 }
		return  null;
	}
	
	public static void setSystemConfig(SystemConfig systemConfig)	{
				
		int racks = Integer.parseInt(systemProperties.getProperty("Rack"));
		int dataNodes = Integer.parseInt(systemProperties.getProperty("DataNodes"));
		int files = Integer.parseInt(systemProperties.getProperty("Files"));
		int blocks = Integer.parseInt(systemProperties.getProperty("Blocks"));
		int logEntries = Integer.parseInt(systemProperties.getProperty("LogEntries"));
		int minSessionSize = Integer.parseInt(systemProperties.getProperty("MinSessionSize"));
		int maxSessionSize = Integer.parseInt(systemProperties.getProperty("MaxSessionSize"));
		int clusterSize = dataNodes/racks;
		//int popFiles = Integer.parseInt(systemProperties.getProperty("PopFiles"));
		//int avgFiles = Integer.parseInt(systemProperties.getProperty("AvgFiles"));
		//int unpopFiles = Integer.parseInt(systemProperties.getProperty("UnpopFiles"));
				
		systemConfig.setRacks(racks);		
		systemConfig.setDataNodes(dataNodes);
		systemConfig.setClusterSize(clusterSize);
		systemConfig.setFiles(files);		
		systemConfig.setBlocks(blocks);	
		systemConfig.setLogEntries(logEntries);
		systemConfig.setMinSessionSize(minSessionSize);
		systemConfig.setMaxSessionSize(maxSessionSize);
	//	systemConfig.setPopFiles(popFiles);
	//	systemConfig.setAvgFiles(avgFiles);
	//	systemConfig.setUnpopFiles(unpopFiles);
	}
}

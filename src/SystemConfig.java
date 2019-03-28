
public class SystemConfig {	

	private int racks;
	private int dataNodes;
	private int files;
	private int blocks;
	private int clusterSize;
	private int minSessionSize;
	private int maxSessionSize;
	private int logEntries;
	private int popFiles;
	private int avgFiles;
	private int unpopFiles;
	
	public int getPopFiles() {
		return popFiles;
	}

	public void setPopFiles(int popFiles) {
		this.popFiles = popFiles;
	}

	public int getAvgFiles() {
		return avgFiles;
	}

	public void setAvgFiles(int avgFiles) {
		this.avgFiles = avgFiles;
	}

	public int getUnpopFiles() {
		return unpopFiles;
	}

	public void setUnpopFiles(int unpopFiles) {
		this.unpopFiles = unpopFiles;
	}
	LoadSystemProperties loadSystemProperties;
	static SystemConfig systemConfig = null;
	
	public int getMinSessionSize() {
		return minSessionSize;
	}

	public void setMinSessionSize(int minSessionSize) {
		this.minSessionSize = minSessionSize;
	}
	public int getMaxSessionSize() {
		return maxSessionSize;
	}

	public void setMaxSessionSize(int maxSessionSize) {
		this.maxSessionSize = maxSessionSize;
	}
	public int getLogEntries() {
		return logEntries;
	}

	public void setLogEntries(int logEntries) {
		this.logEntries = logEntries;
	}

	private SystemConfig()	{						// private Constructor
		
	}
	
	public static SystemConfig getInstance()	{	// Singleton-pattern
		
		if(systemConfig==null)	{
			systemConfig = new SystemConfig();
		}
		//LoadSystemProperties loadSystemProperties = LoadSystemProperties.getInstance();
		//loadSystemProperties.setSystemConfig(systemConfig);
		return systemConfig;
	}
	
	public int getClusterSize() {
		return clusterSize;
	}

	public void setClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
	}

	public int getRacks() {
		return racks;
	}
	public void setRacks(int rack) {
		this.racks = rack;
	}
	public int getDataNodes() {
		return dataNodes;
	}
	public void setDataNodes(int dataNodes) {
		this.dataNodes = dataNodes;
	}
	public int getFiles() {
		return files;
	}
	public void setFiles(int files) {
		this.files = files;
	}
	public int getBlocks() {
		return blocks;
	}
	public void setBlocks(int blocks) {
		this.blocks = blocks;
	}
}

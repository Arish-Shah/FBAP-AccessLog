import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class GlobalSupport {
	
	DataNode[] dns;
	int totalFreq;
	
	int[] fileFreqG;
	int[][] blockFreqG;
	
	double[] fileSuppG;
	double[][] blockSuppG;
	
	Map<String,Double> pMapG;
	Map<String,Integer> pFreqMapG;
	Map<String,Integer> pBaseFreqMapG;
	
	public GlobalSupport(DataNode[] dns)	{
		this.dns = dns;
		SystemConfig systemConfig = SystemConfig.getInstance();
		fileFreqG = new int[systemConfig.getFiles()+1];
		blockFreqG = new int[systemConfig.getFiles()+1][systemConfig.getBlocks()+1];
		
		fileSuppG = new double[systemConfig.getFiles()+1];
		blockSuppG = new double[systemConfig.getFiles()+1][systemConfig.getBlocks()+1];
		
		for(int i=0;i<fileFreqG.length;i++)	{
			fileFreqG[i] = 0;
		}
		for(int i=0;i<blockFreqG.length;i++)	{
			for(int j=0;j<blockFreqG[i].length;j++)	
				blockFreqG[i][j] = 0;
		}
		
		pMapG = new HashMap<String,Double>();
		pFreqMapG = new HashMap<String,Integer>();
		pBaseFreqMapG = new HashMap<String,Integer>();
	}
	public void calGlobalSupport()	{
		
		calGlobalFreq();
		calGS();
		calPatternFreq();
		calPatternSupp();
	}
	
	private void calPatternSupp() {
		
		Set<String> keys = pFreqMapG.keySet();
		Iterator<String> keyItr = keys.iterator();
		
		while(keyItr.hasNext())	{
			String pat = keyItr.next();
			int freq = pFreqMapG.get(pat);
			int baseFreq = pBaseFreqMapG.get(pat);
			double suppValue = (double)freq / baseFreq;
			pMapG.put(pat, suppValue);
		}
	}
	
	private void calPatternFreq() {
		
		for(int k=1;k<dns.length;k++){
			
			Map<String,Integer> pFreqMapL = dns[k].getpFreqMap();
			Map<String,Integer> pBaseFreqMapL = dns[k].getpMapBaseFreq();
			
			Set<String> keys = pFreqMapL.keySet();
			Iterator<String> keyItr = keys.iterator();
			
			while(keyItr.hasNext())	{
				String pat = keyItr.next();
				int newFreq = pFreqMapL.get(pat);
				int newBaseFreq = pBaseFreqMapL.get(pat);
				
				if(pFreqMapG.containsKey(pat))	{
					int oldFreq = pFreqMapG.get(pat);
					int oldBaseFreq = pBaseFreqMapG.get(pat);
					
					pFreqMapG.put(pat, oldFreq+newFreq);
					pBaseFreqMapG.put(pat, oldBaseFreq+newBaseFreq);
				}
				pFreqMapG.put(pat, newFreq);
				pBaseFreqMapG.put(pat, newBaseFreq);
			}
			
		}
	}
	private void calGlobalFreq() {
		 
		for(int k=1;k<dns.length;k++){
			int[] fileFreqDN = dns[k].getfFreq();
			int[][] blockFreqDN = dns[k].getbFreq();
			totalFreq += dns[k].getTotalSessions();
			for(int i=1; i<blockFreqDN.length;i++)
				fileFreqG[i] += fileFreqDN[i]; 
			for(int i=1; i<blockFreqDN.length;i++)	{
				//fileFreqG[i] += fileFreqDN[i]; 
				for(int j=1; j<blockFreqDN[i].length;j++)	{
					blockFreqG[i][j] += blockFreqDN[i][j]; 
				}
			}
		}
	}
	private void calGS() {
	
		for(int i=1;i<fileSuppG.length;i++)	{
			fileSuppG[i] = (double)fileFreqG[i] / totalFreq;
			//System.out.println("fileSuppG["+i+"] : "+fileSuppG[i]);
		}
		for(int i=1;i<blockSuppG.length;i++)	{
			for(int j=1;j<blockSuppG[i].length;j++)	{
				if(fileFreqG[i]>=0.3)					
					blockSuppG[i][j] = (double) blockFreqG[i][j] / fileFreqG[i];
				else
					blockSuppG[i][j]=0;
				//System.out.println("blockSuppG["+i+"]["+j+"] : "+blockSuppG[i][j]);
			}
		}
	}
	public int getTotalFreq() {
		return totalFreq;
	}
	public void setTotalFreq(int totalFreq) {
		this.totalFreq = totalFreq;
	}
	public int[] getFileFreqG() {
		return fileFreqG;
	}
	public void setFileFreqG(int[] fileFreqG) {
		this.fileFreqG = fileFreqG;
	}
	public int[][] getBlockFreqG() {
		return blockFreqG;
	}
	public void setBlockFreqG(int[][] blockFreqG) {
		this.blockFreqG = blockFreqG;
	}
	public double[] getFileSuppG() {
		return fileSuppG;
	}
	public void setFileSuppG(double[] fileSuppG) {
		this.fileSuppG = fileSuppG;
	}
	public double[][] getBlockSuppG() {
		return blockSuppG;
	}
	public void setBlockSuppG(double[][] blockSuppG) {
		this.blockSuppG = blockSuppG;
	}
	public Map<String, Double> getpMapG() {
		return pMapG;
	}
	public void setpMapG(Map<String, Double> pMapG) {
		this.pMapG = pMapG;
	}
	public Map<String, Integer> getpFreqMapG() {
		return pFreqMapG;
	}
	public void setpFreqMapG(Map<String, Integer> pFreqMapG) {
		this.pFreqMapG = pFreqMapG;
	}
	public Map<String, Integer> getpBaseFreqMapG() {
		return pBaseFreqMapG;
	}
	public void setpBaseFreqMapG(Map<String, Integer> pBaseFreqMapG) {
		this.pBaseFreqMapG = pBaseFreqMapG;
	}
}
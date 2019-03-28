import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class DataNode {

	int dnNum;
	int totalSessions;		// Stores count of requested session on a datanode.
	List<String> log;		//	Example, F1,B1,B2,B3,B4,B5
	
	int[] fFreq;					//File Freq
	int[][] bFreq;					//Block Freq
	Map<String,Integer> pFreqMap;	//Pattern Freq
	
	double[] fSupp;
	double[][] bSupp;
	Map<String, Double> pSuppMap;	//Pattern Support with all patterns.
	TreeMap<String, Double> pMap;		//Pattern Support with short-listed patterns.
	TreeMap<String, Double> fMap;		// File Support Map.
	TreeMap<String, Double> bMap;		// Block Support Map.
	
	SystemConfig systemConfig;
	private Map<String, Integer> pMapBaseFreq;
	
	
	public DataNode(int dnNum) {
		
		this.dnNum = dnNum;
		totalSessions = 0;
		log = new LinkedList<String>();
		systemConfig = SystemConfig.getInstance();
		
		fFreq = new int[systemConfig.getFiles()+1];
		bFreq = new int[systemConfig.getFiles()+1][systemConfig.getBlocks()+1];
		
		fSupp = new double[systemConfig.getFiles()+1];
		bSupp = new double[systemConfig.getFiles()+1][systemConfig.getBlocks()+1];
		
		pFreqMap = new TreeMap<String,Integer>();
		pMapBaseFreq = new HashMap<String,Integer>();
		pSuppMap = new LinkedHashMap<String,Double>();
		
		pMap = new TreeMap<String,Double>();
		fMap = new TreeMap<String,Double>();
		bMap = new TreeMap<String,Double>();
		
		for(int i=0;i<bFreq.length;i++){	// initialize frequencies to zero.
			fFreq[i]=0;
			for(int j=0;j<bFreq[i].length;j++){
				bFreq[i][j]=0;
			}
		}
	}
	public void addLog(String session)	{	// Add log entries that belongs to this data node. // Ex: F1 B1 B2 B3
		log.add(session);
		totalSessions++;
	}
	public void calFreq(){
		String session ="";
		Iterator<String> itr = log.iterator();
		while(itr.hasNext())	{
			session = itr.next();
			int[] fbNum = getFBNum(session);	// gets file and block numbers from the session.
			fFreq[fbNum[0]]++;
			for(int i=1;i<fbNum.length;i++)
				bFreq[fbNum[0]][fbNum[i]]++;
		}
	}
	

	private int[] getFBNum(String session) {
		String[] tokens = session.split(",");
		int[] fbNum = new int[tokens.length];
		for(int i=0;i<tokens.length;i++)
			fbNum[i] = Integer.parseInt(tokens[i].substring(1));
		return fbNum;
	}
	public void calSupport()	{
		calFileSupport();
		calBlockSupport();
		calPatternSupport();
		//System.out.println("DataNode- "+dnNum);
		//System.out.println("pSuppMap : "+pSuppMap);
		List<String> list = getPopularPattern();
		addListToMap(list);
		
		fMap = sortMapByValue(fMap); 
		bMap = sortMapByValue(bMap); 
		pMap = sortMapByValue(pMap); 
	//	System.out.println("pMap : "+pMap);
	}
	public double getBlockSupp(int i,int j){
		if((i>0 && i<bSupp.length)&&(j>0 && j<bSupp[i].length))
			return bSupp[i][j];
		else
			return 0;
	}
	private void addListToMap(List<String> list) {
		
		Iterator<String> listItr = list.iterator();
		while(listItr.hasNext())	{
			String key = listItr.next();
			
			double value = pSuppMap.get(key);
			if(value!=1.0)	{
				pMap.put(key, value);
				//System.out.println(key+" : "+value);
			}
		}
	}
	private void calFileSupport()	{
		for(int i=1;i<fFreq.length;i++)	{
			double supportValue = (double)fFreq[i] / log.size();
			fSupp[i] = supportValue;
			if(supportValue >= 0.01 )
				fMap.put("F"+i, supportValue);
		}
	}
	private void calBlockSupport()	{
		for(int i=1;i<bFreq.length;i++)	{
			for(int j=1;j<bFreq[i].length;j++)	{
				if(fMap.containsKey("F"+i)&& bFreq[i][j]>50)	{	// Hardcoded Value 50 to reject sessions that occurred once by chance.
					double supportValue = (double)bFreq[i][j] / fFreq[i];
					bSupp[i][j] = supportValue;
					if((supportValue>=0.01) && (fMap.containsKey("F"+i))&& bFreq[i][j]>50)	//Hardcoded to eliminate less, but repetitive sessions.
						bMap.put("F"+i+" B"+j, supportValue);
				}					
			}
		}
	}
	private void calPatternSupport()	{
		
		calPatternFreq();
		
		String pat;				//,basePat;
		double patFreq,baseFreq;
		double patSupp;
		
		Set<String> s = pFreqMap.keySet();
		Iterator<String> itr = s.iterator();
		
		while(itr.hasNext())	{
			pat = itr.next();
			patFreq = (double)pFreqMap.get(pat);
			baseFreq = (double)pMapBaseFreq.get(pat);
			
			if(patFreq>50)	{
				
				patSupp = patFreq / baseFreq;
				pSuppMap.put(pat, patSupp);
			}
		}
	}
	public void calPatternFreq(){
		String session ="";
		Iterator<String> itr = log.iterator();
		while(itr.hasNext())	{
			session = itr.next();
			String[] sessionTokens = session.split(",");
			if(fMap.containsKey(sessionTokens[0])&& fMap.get(sessionTokens[0])>=0.1)	{
				if(bMap.containsKey(sessionTokens[0]+" "+sessionTokens[1]) && (bMap.get(sessionTokens[0]+" "+sessionTokens[1])>= Cat.minSup1))	{
					addPatternFreq(sessionTokens);
				}
			}
		}
		calBaseFreq();
	}
	
	private void addPatternFreq(String[] sessionTokens) {
		
		String pat;
		
		for(int i=1;i<sessionTokens.length-1;i++)	{
			pat = sessionTokens[0]+" "+sessionTokens[i]; 
			for(int j=i+1;j<sessionTokens.length;j++){
				pat += " "+sessionTokens[j]; 
				updatePatternFreq(pat);
			}
		}
	}
	private void updatePatternFreq(String pat) {
		
		if(pFreqMap.containsKey(pat)){		
			int value = pFreqMap.get(pat);
			value++;
			pFreqMap.put(pat, value);
		}
		else
			pFreqMap.put(pat, 1);		
	}
	private void calBaseFreq() {
		Set<String> pMapSet = pFreqMap.keySet();
		//System.out.println("pMapSet : "+pMapSet);
		Iterator<String> pMapKeysItr = pMapSet.iterator();
		
		while(pMapKeysItr.hasNext())	{
			int baseFreq = 0;
			String pMapKey = pMapKeysItr.next();
			String[] keyTokens  = pMapKey.split(" ");
			
			Iterator<String> logItr = log.iterator(); // search log
			while(logItr.hasNext())	{
				
				String logSession = logItr.next();
				String[] logSessionTokens = logSession.split(",");
				if(logSessionTokens[0].equals(keyTokens[0]))	{	// Checks whether the files are same or not.
					outer :	for(int i=1;i<keyTokens.length;i++)	{
						for(int j=1;j<logSessionTokens.length;j++)	{
							if(keyTokens[i].equals(logSessionTokens[j]))	{
								baseFreq++;
								break outer;
							}
						}
					}
				}
			}
			if(baseFreq!=0)
				pMapBaseFreq.put(pMapKey,baseFreq);
		}		
	}
	private List<String> getPopularPattern()	{	// cut out unnecessary patterns (or sub patterns).
		
		Set<String> pMapKeys= pSuppMap.keySet();
		Iterator<String> keysItr = pMapKeys.iterator();
		LinkedList<String> uniquePatList = new LinkedList<String>();
		while(keysItr.hasNext())	{
			
			String key = keysItr.next();
			if(!(uniquePatList.isEmpty()))	{		
				if(pSuppMap.get(key) >= Cat.minSup1)	// checks for longest (unique) pattern
					addPatternIfUnique(uniquePatList,key);
			}
			else	{
				if(pSuppMap.get(key) > Cat.minSup1)
					uniquePatList.add(key);
			}
		}
		//System.out.println("uniquePatList : "+uniquePatList);
		return uniquePatList;
		
	}
	private void addPatternIfUnique(LinkedList<String> uniquePatList, String key) {	// add Pattern to list if it is unique and longest with good support.
	
		String keyStr="",listPatStr="";
		
		ListIterator<String> patListItr = uniquePatList.listIterator();
		String[] keyTokens = key.split(" ");
		for(int i=1;i<keyTokens.length;i++)
			keyStr += keyTokens[i];
		
		while(patListItr.hasNext())	{
		
			String listPat = patListItr.next();
			String[] listPatTokens = listPat.split(" ");
			if(keyTokens[0].equals(listPatTokens[0]))	{ 
				
				listPatStr="";
				for(int i=1;i<listPatTokens.length;i++)
					listPatStr += listPatTokens[i];
				
				if(keyStr.length()>= listPatStr.length()){
				
					boolean res = keyStr.contains(listPatStr);
					if(res==true)	{
						patListItr.remove();	// remove the pattern stored in the list as it is the subsequence of key.
					}
				}
				else	{
					
					boolean res = listPatStr.contains(keyStr);
					if(res==true)	{
						return;	// Don't add key to list as it is a subsequence.
					}
				}
			}
			
		}
		patListItr.add(key);	// add key to list if it is not a subsequence.	
	}
	public Map<String, Integer> getpFreqMap() {
		return pFreqMap;
	}
	public void setpFreqMap(Map<String, Integer> pFreqMap) {
		this.pFreqMap = pFreqMap;
	}
	public Map<String, Integer> getpMapBaseFreq() {
		return pMapBaseFreq;
	}
	public void setpMapBaseFreq(Map<String, Integer> pMapBaseFreq) {
		this.pMapBaseFreq = pMapBaseFreq;
	}
	public int getDnNum() {
		return dnNum;
	}
	public void setDnNum(int dnNum) {
		this.dnNum = dnNum;
	}
	public int getTotalSessions() {
		return totalSessions;
	}
	public void setTotalSessions(int totalSessions) {
		this.totalSessions = totalSessions;
	}
	public int[] getfFreq() {
		return fFreq;
	}
	public void setfFreq(int[] fFreq) {
		this.fFreq = fFreq;
	}
	public int[][] getbFreq() {
		return bFreq;
	}
	public void setbFreq(int[][] bFreq) {
		this.bFreq = bFreq;
	}
	
	public TreeMap<String, Double> getpMap() {
		return pMap;
	}
	public Map<String, Double> getpSuppMap() {
		return pSuppMap;
	}
	public void setpSuppMap(Map<String, Double> pSuppMap) {
		this.pSuppMap = pSuppMap;
	}
	public void setpMap(TreeMap<String, Double> pMap) {
		this.pMap = pMap;
	}
	public TreeMap<String, Double> getfMap() {
		return fMap;
	}
	public void setfMap(TreeMap<String, Double> fMap) {
		this.fMap = fMap;
	}
	public TreeMap<String, Double> getbMap() {
		return bMap;
	}
	public void setbMap(TreeMap<String, Double> bMap) {
		this.bMap = bMap;
	}
	
	public void displayLog() {
		Iterator<String> itr = log.iterator();
		while(itr.hasNext())
			System.out.println(itr.next());
	}
	public void displayPatFreqMap() {
		Set<String> s = pFreqMap.keySet();
		Iterator<String> itr = s.iterator();
		while(itr.hasNext())	{
			String key = itr.next();
			System.out.println(key+" ---> "+pFreqMap.get(key));
		}
	}
	
	public void displayPatSuppMap() {
		
		Set<String> s = pSuppMap.keySet();
		Iterator<String> itr = s.iterator();
		while(itr.hasNext())	{
			String key = itr.next();
			double supp = pSuppMap.get(key);
			if (supp!=1.0)
			System.out.println(key+" ===> "+supp);
		}
	}
	public TreeMap<String, Double> sortMapByValue(TreeMap<String, Double> map){
		Comparator<String> comparator = new ValueComparator(map);
		//TreeMap is a map sorted by its keys. 
		//The comparator is used to sort the TreeMap by keys. 
		TreeMap<String, Double> result = new TreeMap<String, Double>(comparator);
		result.putAll(map);
		return result;
	}
	public static void main(String[] args){
		Main.initSystem();
		String[] tokens = {"F1","B1","B2","B3","B4","B5"};
		String pat;
		for(int i=1;i<tokens.length-1;i++)	{
			pat = tokens[0] +" "+tokens[i]; 
			for(int j=i+1;j<tokens.length;j++){
				pat += " "+tokens[j]; 
				System.out.println(pat);
			}
		}
		String s1 = "B2 B3";
		String s2 = "B1 B2 B3 B4";
		System.out.println("s2.contains(s1) is : "+s2.contains(s1));
		System.out.println("s1.contains(s2) is : "+s1.contains(s2));
		
		outer :	for(int i=5;i<=10;i++)	{
					for(int j=1;j<10;j++)	{
						System.out.println(i+" * "+j+" = "+(i*j));
						if(i*j==25)	
							break outer;
				}
			}
		
		DataNode dn = new DataNode(1);	// Longest Pattern.
		Map<String,Double> sMap = new LinkedHashMap<String,Double>();
		sMap.put("F1 B1 B2", 0.6);
		sMap.put("F1 B1 B2 B3", 0.7);
		sMap.put("F1 B3 B4", 0.7);
		sMap.put("F1 B1 B2 B3 B4", 0.7);
		sMap.put("F1 B2 B3", 0.7);
		sMap.put("F1 B2 B4", 0.6);
		System.out.println("B1B2B3B4.contains(B3B4) :"+"B1B2B3B4".contains("B3B4"));
		Set<String> pMapKeys= sMap.keySet();
		Iterator<String> keysItr = pMapKeys.iterator();
		LinkedList<String> uniquePatList = new LinkedList<String>();
		while(keysItr.hasNext())	{
			
			String key = keysItr.next();
			if(!(uniquePatList.isEmpty()))	{
				if(sMap.get(key) > Cat.minSup1)
					dn.addPatternIfUnique(uniquePatList,key);
			}
			else	{
				if(sMap.get(key) > Cat.minSup1)
					uniquePatList.add(key);
			}
		}
		System.out.println("uniquePatList : "+uniquePatList);
		
		String loc1 = "R1D10"; 
		String[] tok = loc1.split("D");
		for(int i=0;i<tok.length;i++)
			System.out.println("tok["+i+"] = "+tok[i]);
		StringTokenizer st = new StringTokenizer(loc1,"RD");
		String x = st.nextToken();
		String y = st.nextToken();
		System.out.println("st["+0+"] = "+x+", st["+1+"] = "+y);
	}
}
//a comparator that compares Strings
class ValueComparator implements Comparator<String>{

	TreeMap<String, Double> map = new TreeMap<String, Double>();

	public ValueComparator(TreeMap<String, Double> map){
		this.map.putAll(map);
	}

	@Override
	public int compare(String s1, String s2) {
		if(map.get(s1) >= map.get(s2)){
			return -1;
		}else{
			return 1;
		}	
	}
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;


public class GAL {

	Set<String> cat1FilesList;	// Stores list of pop files(cat-1).
	Set<String> cat2FilesList;	// Stores list of avg files(cat-2).
	Set<String> cat3FilesList;	// Stores list of unpop files(cat-3).
	ArrayList<String> blockList;
	
	SystemConfig systemConfig;
	
	MetaDataInfo metaDataInfo;
	HashMap<String,String>[] hm_MetaData;
	
	public GAL()	{
		
		cat1FilesList = new TreeSet<String>();
		cat2FilesList = new TreeSet<String>();
		cat3FilesList = new TreeSet<String>();
		
		systemConfig = SystemConfig.getInstance();
		
		metaDataInfo = MetaDataInfo.getMetaDataInfo();
		hm_MetaData =  metaDataInfo.getMD_HashMap();
		
		blockList = new ArrayList<String>();
		blockList.ensureCapacity(systemConfig.getBlocks());
		
		for(int i=1;i<=systemConfig.getBlocks();i++)	{
			blockList.add("B"+i);
		}
	}
	
	public List<String> generateLog()	{
		categorize();
		List<String> list = new ArrayList<String>();
		list.addAll(getAllCat1Sessions());
		list.addAll(getAllCat2Sessions());
		list.addAll(getAllCat3Sessions());
		Collections.shuffle(list);
		return list;
	}

	private void categorize()	{
		
		List<String> fileList = new LinkedList<String>();
		
		for(int i=1;i<=systemConfig.getFiles();i++)	{ // Store all files in list
			fileList.add("F"+i);
		}
		
		int cat1FileCount = systemConfig.getFiles()* AccessLogConfig.CAT1_FILE_PER/100;	// getting File Counts for categories.
		int cat2FileCount = systemConfig.getFiles()* AccessLogConfig.CAT2_FILE_PER/100;
				
		for(int i=0;i<cat1FileCount;i++)	{							// Getting a random file collection that belongs to cat1.
			int randFile = MyRandom.getRandomNum(0, fileList.size());
			cat1FilesList.add(fileList.remove(randFile));
		}
		for(int i=0;i<cat2FileCount;i++)	{							// Getting a random file collection that belongs to cat2.
			int randFile = MyRandom.getRandomNum(0, fileList.size());
			cat2FilesList.add(fileList.remove(randFile));
		}
		ListIterator<String> li = fileList.listIterator();				// Remaining files added to collection that belongs to cat3.
		while(li.hasNext()){
			cat3FilesList.add(li.next());
			li.remove();
		}
		
		System.out.println("Cat-1 Files size : "+cat1FilesList.size()+" \n CAT-1 Files List is : \n"+cat1FilesList);
		System.out.println("Cat-2 Files size : "+cat2FilesList.size()+" \n CAT-2 Files List is : \n"+cat2FilesList);
		System.out.println("Cat-3 Files size : "+cat3FilesList.size()+" \n CAT-3 Files List is : \n"+cat3FilesList);
	}
	
	private List<String> getAllCat1Sessions()	{
		
		List<String> cat1LogList = new LinkedList<String>(); 
		Iterator<String> itr = cat1FilesList.iterator();
		
		while(itr.hasNext())	{
			
			String fileName = itr.next();
			List<String> cat1FileLog = getCat1FileLog(fileName);
			cat1LogList.addAll(cat1FileLog);
			//break;
		}
		return cat1LogList;
	}
	private List<String> getCat1FileLog(String fileName)	{
		
		List<String> cat1FileLog = new LinkedList<String>();
		
		String popSession = getRandSession(MyRandom.getRandomSessionSize());
		String[] blocks = popSession.split(",");
		
		int[] fileBlock = new int[2];
		fileBlock[0] = Integer.parseInt(fileName.substring(1));
		fileBlock[1] = Integer.parseInt(blocks[0].substring(1));
	
		int[][] newLoc = getNewLocations(fileBlock,AccessLogConfig.CAT1);	// 6 by 2 array.
		
		int[] entryCount = getEntryCount(AccessLogConfig.CAT1);							// 1 is the category
		int[] devEntryCount = getDevEntryCount(entryCount,AccessLogConfig.CAT1);	// 1 is the category
	
	/*
	 	System.out.println("Request Locations : ");
		for(int i=0;i<newLoc.length;i++)	{
			System.out.println("Location : "+(i+1)+" - R"+newLoc[i][0]+" - D"+newLoc[i][1]+" - Entry Count "+entryCount[i]+" devEntryCount"+devEntryCount[i]);
		}
		*/
		String[] devSessionList = getDevSessionList(popSession,blocks.length,AccessLogConfig.CAT1);	
		
		for(int i=0;i<newLoc.length;i++)	{
			List<String> localLog= getLogEntries(fileName,newLoc[i][0],newLoc[i][1],entryCount[i],devEntryCount[i],popSession,devSessionList);
			cat1FileLog.addAll(localLog);
		}
		return cat1FileLog;
	}
	private List<String> getLogEntries(String fileName,int rack, int dataNode, int entryCount, int devEntryCount,
			String popSession, String[] devSessionList) {
		String session;
		List<String> localLog = new ArrayList<String>(entryCount);
		for(int i=0;i<entryCount;i++)	{						
			if(i<devEntryCount)
				session = "R"+rack+",D"+dataNode+","+fileName+","+devSessionList[i%devSessionList.length];
			else	
				session = "R"+rack+",D"+dataNode+","+fileName+","+popSession;
				
			localLog.add(session);
		}
		return localLog;
	}
	private List<String> getAllCat2Sessions()	{
		
		List<String> cat2LogList = new LinkedList<String>(); 
		Iterator<String> itr = cat2FilesList.iterator();
		
		while(itr.hasNext())	{
			
			String fileName = itr.next();
			List<String> cat2FileLog = getCat2FileLog(fileName);
			cat2LogList.addAll(cat2FileLog);
			//break;
		}
		return cat2LogList;
	}
	
	private List<String> getCat2FileLog(String fileName)	{
		
		List<String> cat2FileLog = new LinkedList<String>();
		
		//int fileEntriesCount1 = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(80, 95) / 100;
		
		String popSession = getRandSession(MyRandom.getRandomSessionSize());
		//System.out.println("Popular Session in CAT-2 : "+popSession);	
		
		String[] blocks = popSession.split(",");
		
		int[] fileBlock = new int[2];
		fileBlock[0] = Integer.parseInt(fileName.substring(1));
		fileBlock[1] = Integer.parseInt(blocks[0].substring(1));
	
		int[][] newLoc = getNewLocations(fileBlock,AccessLogConfig.CAT2);	// 5 by 2 array.
		
		int[] entryCount = getEntryCount(AccessLogConfig.CAT2);						
		int[] devEntryCount = getDevEntryCount(entryCount,AccessLogConfig.CAT2);	
		String[] devSessionList = getDevSessionList(popSession,blocks.length,AccessLogConfig.CAT2);
		
		
		for(int i=0;i<newLoc.length;i++)	{		// put entries datanode-wise.
			
				List<String> localLog= getLogEntries2(fileName,newLoc[i][0],newLoc[i][1],entryCount[i],devEntryCount[i],popSession,devSessionList);
				cat2FileLog.addAll(localLog);
		}
		return cat2FileLog;
	}
	private List<String> getLogEntries2(String fileName,int rack, int dataNode, int entryCount, int devEntryCount,
			String popSession, String[] devSessionList) {
		
		String session;
		List<String> localLog = new ArrayList<String>(entryCount);
		
		//int otherEntryPer = 100 - MyRandom.getRandomNum((int)(Cat.minSup1*100)+1,(int)(Cat.minSup*100)-1);
		//int otherEntries = entryCount * otherEntryPer / 100;
		int otherEntryPer = 5;
		int otherEntries = entryCount * otherEntryPer / 100;
		//System.out.println("popSession.split(" ").length : "+popSession.split(" ").length+" "+);
		//System.out.println("fileName : "+fileName+"  rack : "+rack+" Datanode : "+dataNode);
		//System.out.println("entryCount : "+entryCount+"  devEntryCount : "+devEntryCount);
		//System.out.println("otherEntryPer : "+otherEntryPer+"  otherEntries : "+otherEntries);
		List<String> blockList2 = getBlockList(popSession);
		for(int i=0;i<otherEntries;i++)	{
			String randSession = getRandSession2(blockList2,popSession.split(",").length);
			session = "R"+rack+",D"+dataNode+","+fileName+","+randSession;
			localLog.add(session);
		}
		for(int i=0;i<entryCount-otherEntries;i++)	{						
			if(i<devEntryCount)
				session = "R"+rack+",D"+dataNode+","+fileName+","+devSessionList[i%devSessionList.length];
			else	
				session = "R"+rack+",D"+dataNode+","+fileName+","+popSession;
				
			localLog.add(session);
		}
		return localLog;
	}
	private List<String> getBlockList(String popSession) {
		
		String[] popSessionTokens = popSession.split(" ");
		List<String> popSessionList = new ArrayList<String>();
		
		for(int j=0;j<popSessionTokens.length;j++)	{
			popSessionList.add(popSessionTokens[j]);
		}
		
		List<String> blockList2 = new ArrayList<String>();
		for(int i=1;i<systemConfig.getBlocks();i++)	{
			blockList2.add("B"+i);
		}
		blockList2.removeAll(popSessionList);
	//	String session = getRandSession2(blockList2,popSessionTokens.length);
		return blockList2;
	}

	private String getRandSession2(List<String> blockList2, int sessionSize) {
		
		List<String> sessionList = new ArrayList<String>();
		for(int i=0;i<sessionSize;i++)	{
			int index = MyRandom.getRandomNum(1, blockList2.size());
			sessionList.add(blockList2.remove(index));
		}
		blockList2.addAll(sessionList);
		String sessionStr ="";
		Iterator<String> itr = sessionList.iterator();
		while(itr.hasNext())	{
			sessionStr += itr.next()+",";
		}
		return sessionStr.substring(0, sessionStr.length()-1);
	}

	private List<String> getAllCat3Sessions()	{
	
	List<String> cat3LogList = new LinkedList<String>(); 
	Iterator<String> itr = cat3FilesList.iterator();
	
	while(itr.hasNext())	{
		
		String fileName = itr.next();
		List<String> cat3FileLog = getCat3FileLog(fileName);
		cat3LogList.addAll(cat3FileLog);
		//break;
	}
	return cat3LogList;
}
	private List<String> getCat3FileLog(String fileName)	{
		
		List<String> cat3FileLog = new LinkedList<String>();
		
		//int fileEntriesCount1 = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(80, 95) / 100;
		
		String popSession = getRandSession(MyRandom.getRandomSessionSize());
		//System.out.println("Popular Session is : "+session);	
		
		String[] blocks = popSession.split(",");
		
		int[] fileBlock = new int[2];
		fileBlock[0] = Integer.parseInt(fileName.substring(1));
		fileBlock[1] = Integer.parseInt(blocks[0].substring(1));
	
		int[][] newLoc = getNewLocations(fileBlock,AccessLogConfig.CAT3);	// 6 by 2 array.
		int[] entryCount = getEntryCount(AccessLogConfig.CAT3);							// 1 is the category
		int[] devEntryCount = getDevEntryCount(entryCount,AccessLogConfig.CAT3);	// 1 is the category
		String[] devSessionList = getDevSessionList(popSession,blocks.length,AccessLogConfig.CAT3);	
		
		for(int i=0;i<newLoc.length;i++)	{
			List<String> localLog= getLogEntries3(fileName,newLoc[i][0],newLoc[i][1],entryCount[i],devEntryCount[i],popSession,devSessionList);
			cat3FileLog.addAll(localLog);
		}
		return cat3FileLog;
	}
	private List<String> getLogEntries3(String fileName,int rack, int dataNode, int entryCount, int devEntryCount,
			String popSession, String[] devSessionList) {
		
		String session;
		List<String> localLog = new ArrayList<String>(entryCount);
		
		int otherEntryPer = 100 - MyRandom.getRandomNum((int)(Cat.minSup1*100)-20,(int)(Cat.minSup1*100)-10);	// Min_Value: 31 and Max_Value: 39
		int otherEntries = (int)(entryCount * otherEntryPer / 100);
		List<String> blockList3= getBlockList(popSession);
		for(int i=0;i<otherEntries;i++)	{
			
			String randSession = getRandSession2(blockList3,popSession.split(" ").length);
			session = "R"+rack+",D"+dataNode+","+fileName+","+randSession;
			localLog.add(session);
		}
		for(int i=0;i<entryCount-otherEntries;i++)	{						
			if(i<devEntryCount)
				session = "R"+rack+",D"+dataNode+","+fileName+","+devSessionList[i%devSessionList.length];
			else	
				session = "R"+rack+",D"+dataNode+","+fileName+","+popSession;
				
			localLog.add(session);
		}
		return localLog;
	}	
	

	private int[] getDevEntryCount(int[] entryCount, int cat) {
		int[] devEntryCount = new int[getSize(cat)];
		
		if(cat==AccessLogConfig.CAT1)	{
			devEntryCount[0] = entryCount[0] * MyRandom.getRandomNum(15, 25) / 100;	
			devEntryCount[1] = entryCount[1] * MyRandom.getRandomNum(5, 10) / 100;
			devEntryCount[2] = entryCount[2] * MyRandom.getRandomNum(10, 20) / 100;
			devEntryCount[3] = entryCount[3] * MyRandom.getRandomNum(25, 30) / 100;
			devEntryCount[4] = entryCount[4] * MyRandom.getRandomNum(30, 35) / 100;
			devEntryCount[5] = entryCount[5] * MyRandom.getRandomNum(35, 40) / 100;
		}
		else if(cat==AccessLogConfig.CAT2)	{
			
		/*	devEntryCount[0] = entryCount[0] * MyRandom.getRandomNum(58, 60) / 100;	
			devEntryCount[1] = entryCount[1] * MyRandom.getRandomNum(52,54 ) / 100;
			devEntryCount[2] = entryCount[2] * MyRandom.getRandomNum(55, 57) / 100;
			devEntryCount[3] = entryCount[3] * MyRandom.getRandomNum(61, 63) / 100;
			devEntryCount[4] = entryCount[4] * MyRandom.getRandomNum(64, 68) / 100;
			*/
			
			devEntryCount[0] = entryCount[0] * MyRandom.getRandomNum(37, 39) / 100;	
			devEntryCount[1] = entryCount[1] * MyRandom.getRandomNum(31, 34) / 100;
			devEntryCount[2] = entryCount[2] * MyRandom.getRandomNum(35, 36) / 100;
			devEntryCount[3] = entryCount[3] * MyRandom.getRandomNum(40, 43) / 100;
			devEntryCount[4] = entryCount[4] * MyRandom.getRandomNum(44, 48) / 100;
			
		}
		else	{
			
			/*
			
			devEntryCount[0] = entryCount[0] * MyRandom.getRandomNum(64, 66) / 100;	
			devEntryCount[1] = entryCount[1] * MyRandom.getRandomNum(61, 63) / 100;
			devEntryCount[2] = entryCount[2] * MyRandom.getRandomNum(67, 70) / 100;
			devEntryCount[3] = entryCount[3] * MyRandom.getRandomNum(71, 74) / 100;
			
			*/
			devEntryCount[0] = entryCount[0] * MyRandom.getRandomNum(44, 46) / 100;	
			devEntryCount[1] = entryCount[1] * MyRandom.getRandomNum(41, 43) / 100;
			devEntryCount[2] = entryCount[2] * MyRandom.getRandomNum(47, 50) / 100;
			devEntryCount[3] = entryCount[3] * MyRandom.getRandomNum(51, 54) / 100;
			
		}
		return devEntryCount;
	}

	private int getSize(int cat)	{		// Some Hard-Coded Values like 6,5,4.
		if(cat==AccessLogConfig.CAT1)
			return 6;
		else if(cat==AccessLogConfig.CAT2)
			return 5;
		else
			return 4;
	}
	private int[] getEntryCount(int cat) {
		
		int[] entryCount = new int[getSize(cat)];
		
		if(cat==AccessLogConfig.CAT1)	{
			
			entryCount[0] = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(12, 15) / 100;		
			entryCount[2] = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(15, 16) / 100;
			entryCount[3] = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(11, 12) / 100;
			entryCount[4] = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(9, 10) / 100;
			entryCount[5] = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(8, 9) / 100;
			
			entryCount[1] = AccessLogConfig.CAT1_FILE_COUNT - (entryCount[0]+entryCount[2]+entryCount[3]+entryCount[4]+entryCount[5]);
		}
		else if(cat==AccessLogConfig.CAT2)	{
			
			entryCount[0] = AccessLogConfig.CAT2_FILE_COUNT * MyRandom.getRandomNum(12, 16) / 100;		
			entryCount[2] = AccessLogConfig.CAT2_FILE_COUNT * MyRandom.getRandomNum(16, 20) / 100;
			entryCount[3] = AccessLogConfig.CAT2_FILE_COUNT * MyRandom.getRandomNum(8, 12) / 100;
			entryCount[4] = AccessLogConfig.CAT2_FILE_COUNT * MyRandom.getRandomNum(6, 8) / 100;
						
			entryCount[1] = AccessLogConfig.CAT2_FILE_COUNT - (entryCount[0]+entryCount[2]+entryCount[3]+entryCount[4]);
		}
		else	{
			entryCount[0] = AccessLogConfig.CAT3_FILE_COUNT * MyRandom.getRandomNum(15, 20) / 100;		
			entryCount[2] = AccessLogConfig.CAT3_FILE_COUNT * MyRandom.getRandomNum(8, 15) / 100;
			entryCount[3] = AccessLogConfig.CAT3_FILE_COUNT * MyRandom.getRandomNum(8, 15) / 100;
			
			entryCount[1] = AccessLogConfig.CAT3_FILE_COUNT - (entryCount[0]+entryCount[2]+entryCount[3]);
		}
		return entryCount;
	}

	private int[][] getNewLocations(int[] fileBlock, int cat) {
		
		int[][] oldLoc = getOrigLocations(fileBlock);
		int[][] reqLoc = getReqLocations(oldLoc,cat);
		return reqLoc;
	}
	private	 int[][] getOrigLocations(int[] fileBlock){
		
		String[] loc = new String[3];
		int[][] oldLoc = new int[3][2];  
	//	System.out.println("file : "+fileBlock[0]+" Block : "+fileBlock[1]);
		loc[0] = metaDataInfo.getLoc1("F"+fileBlock[0]+" B"+fileBlock[1]);
		loc[1] = metaDataInfo.getLoc2("F"+fileBlock[0]+" B"+fileBlock[1]);
		loc[2] = metaDataInfo.getLoc3("F"+fileBlock[0]+" B"+fileBlock[1]);
		
	//	System.out.println("Old Locations : ");
		
		for(int i=0;i<3;i++)	{
			String[] s = loc[i].split("D");
			oldLoc[i][0] = Integer.parseInt(s[0].substring(1,s[0].length()));
			oldLoc[i][1] = Integer.parseInt(s[1]);
		
	//		System.out.println("R"+oldLoc[i][0]+" D"+oldLoc[i][1]);
		
		}
		
		return oldLoc;
	}
	
	private int[][] getReqLocations(int[][] oldLoc,int cat) { // Get Request Locations for File.
		
		int size = getSize(cat);
		
		int[][] reqLoc = new int[size][2];

		int tempRack,dn;
		
		LinkedList<Integer> rackList = new LinkedList<Integer>();
		for(int i=1;i<systemConfig.getRacks();i++)	{
			rackList.add(i);
			
		}
													// 1st Pop Location (write client).
		reqLoc[0][0] = oldLoc[0][0];	// same rack
		reqLoc[0][1] = oldLoc[0][1];	// same datanode
		
		rackList.remove(new Integer(reqLoc[0][0]));
		
									// Other Location
		for(int i=1;i<size;i++)	{
			
			int indexRL = MyRandom.getRandomNum(0, rackList.size()-1);	// get a random index for rackList.
			tempRack = rackList.get(indexRL);
			
			if(i==1)	{							// 2nd Location.
				while(tempRack == oldLoc[1][0])	{	// repeat until a different rack is obtained.
					indexRL = MyRandom.getRandomNum(0, rackList.size()-1);	// get a random index for rackList.
					tempRack = rackList.get(indexRL);
				}
				
				reqLoc[i][0] = tempRack;
				rackList.remove(indexRL);
				reqLoc[i][1] = MyRandom.getRandomDatanode(reqLoc[i][0]); // random datanode;
			}
			else if(i==2)	{						// 3rd Location.
				reqLoc[i][0] = reqLoc[0][0];
				while(true)	{
					dn = MyRandom.getRandomDatanode(reqLoc[i][0]); // random datanode;
					if(dn!=reqLoc[0][1] && dn!=oldLoc[2][1])	{	// get a different datanode other than WDN and Datanode where 3rd copy of HDFS is placed.
						reqLoc[i][1] = dn;
						break;
					}
				}
			}
			else if(i==3)	{
				
				while(tempRack == oldLoc[1][0])	{
					indexRL = MyRandom.getRandomNum(0, rackList.size()-1);	// get a random index for rackList.
					tempRack = rackList.get(indexRL);
				}
				
				reqLoc[i][0] = tempRack;
				rackList.remove(indexRL);
				reqLoc[i][1] = MyRandom.getRandomDatanode(reqLoc[i][0]); // random datanode;
			}
			else	{
				indexRL = MyRandom.getRandomNum(0, rackList.size()-1);	// get a random index for rackList.
				reqLoc[i][0] = rackList.remove(indexRL);
				reqLoc[i][1] = MyRandom.getRandomDatanode(reqLoc[i][0]); // random datanode;
			}
		}
		return reqLoc;
	}
	
	private String[] getDevSessionList(String popSession,int size, int cat) {
		
		String deviantSession = "";
		String[] devSessionList = null;
		String[] sessionTokens = popSession.split(",");
		if(cat == AccessLogConfig.CAT1)
			devSessionList = new String[2*size];
		else if(cat == AccessLogConfig.CAT2)
			devSessionList = new String[4*size];
		else
			devSessionList = new String[6*size];
		
		int pos=0;
		//if(cat==AccessLogConfig.CAT1)	{
			
			for(int i=0;i<devSessionList.length;i++)	{
				int j;
				deviantSession="";
				
				String newSession = getRandSession(sessionTokens.length);
				String[] newSessionTokens = newSession.split(",");
			
				int flag=1;
					
					for(j=0;j<newSessionTokens.length;j++)	{
						if(newSessionTokens[j]==sessionTokens[pos])	{	// change every block in the session
							flag=0;
							break;
						}
					}
					if(flag==1)
						newSessionTokens[pos] = sessionTokens[pos];
					pos++;
					
					if(pos > sessionTokens.length-1)
						pos=0;
					for(j=0;j<newSessionTokens.length-1;j++)	{
						deviantSession += newSessionTokens[j]+",";
					}
					deviantSession += newSessionTokens[j];	// last block without comma.
					
				devSessionList[i] = deviantSession;
			}
	/*	}
		else	{
		
			for(int i=0;i<devSessionList.length;i++)	{
				int j;
				deviantSession="";
				
				String newSession = getRandSession(sessionTokens.length);
				String[] newSessionTokens = newSession.split(",");
			
				
				
				for(j=0;j<newSessionTokens.length-1;j++)	{
						deviantSession += newSessionTokens[j]+",";
				}
				
				deviantSession += newSessionTokens[j];	// last block without comma.
					
				devSessionList[i] = deviantSession;
			}
		}*/
		return devSessionList;
	}
	
	public String getRandSession(int sessionSize )	{
		
		List<String> sessionList = new ArrayList<String>();
		for(int i=0;i<sessionSize;i++)	{
			int index = MyRandom.getRandomNum(1, blockList.size());
			sessionList.add(blockList.remove(index));
		}
		blockList.addAll(sessionList);
		String sessionStr ="";
		Iterator<String> itr = sessionList.iterator();
		while(itr.hasNext())	{
			sessionStr += itr.next()+",";
		}
		return sessionStr.substring(0, sessionStr.length()-1);
	}
	/*	public List<String> getBlockList()	{
			ArrayList<String> blockList = new ArrayList<String>();
			blockList.ensureCapacity(systemConfig.getBlocks());
			for(int i=1;i<=systemConfig.getBlocks();i++)	{
				blockList.add("B"+i);
			}
			return blockList;
		}*/
		
		public String getLoc_1(String file,String block)	{
			return hm_MetaData[0].get(file+block);
		}
		public String getLoc_2(String file,String block)	{
			return hm_MetaData[1].get(file+block);
		}
		public String getLoc_3(String file,String block)	{
			return hm_MetaData[2].get(file+block);
		}
		
		
		
		public static void main(String[] args){
			
			String a = "Hello,world";
			System.out.println(a+" "+a.substring(0, a.length()-1));
			
			initSystem();
			System.out.println("Finished Initializing System...");
			
			String session = "B1,B2,B3,B4,B5,B6,B7,B8,B9,B10";
			GAL gal = new GAL();
			String[] dList = gal.getDevSessionList(session,10,3);
			System.out.println("For Session : B1,B2,B3,B4,B5,B6,B7,B8,B9,B10");
			System.out.println("Deviant Session List is :");
			for(int i=0;i<dList.length;i++)
				System.out.println(dList[i]);
		}
		public static void initSystem()	{
			File file = new File("C:\\Users\\Arish\\Desktop\\eclipse-workspace\\rs\\input\\System-Config.properties");
			SystemConfig systemConfig = LoadSystemProperties.loadProperties(file);
			TestDisplay.displaySystemConfig(systemConfig);	
		}
		/*
		public void transferALToFile(String fileName,List<String> list) {
			
			try (FileWriter writerAL = new FileWriter(fileName);
					 BufferedWriter bufferedWriterAL = new BufferedWriter(writerAL))	{
						Iterator<String> itr = list.iterator();
						while(itr.hasNext())	{
							bufferedWriterAL.write(itr.next());
							bufferedWriterAL.newLine();
						}
						bufferedWriterAL.flush();
						writerAL.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}				
		}*/
}


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class GenerateAccessLog {

	List<String> cat1FilesList;	// Stores list of pop files(cat-1).
	List<String> cat2FilesList;	// Stores list of avg files(cat-2).
	List<String> cat3FilesList;	// Stores list of unpop files(cat-3).
	ArrayList<String> blockList;
	
	SystemConfig systemConfig;
	HashMap<String,String>[] hm_MetaData;
	
	public GenerateAccessLog()	{
		cat1FilesList = new ArrayList<String>();
		cat2FilesList = new ArrayList<String>();
		cat3FilesList = new ArrayList<String>();
		systemConfig = SystemConfig.getInstance();
		hm_MetaData = MetaDataInfo.getMetaDataInfo().getMD_HashMap();
		categorize();
		
		blockList = new ArrayList<String>();
		blockList.ensureCapacity(systemConfig.getBlocks());
		for(int i=1;i<=systemConfig.getBlocks();i++)	{
			blockList.add("B"+i);
		}
	}
	private void categorize()	{
		
		List<String> fileList = new LinkedList<String>();
		
		for(int i=1;i<=systemConfig.getFiles();i++)	{ // Store all files in list
			fileList.add("F"+i);
		}
		
		int popFileCount = systemConfig.getFiles()*systemConfig.getPopFiles()/100;
		int avgFileCount = systemConfig.getFiles()*systemConfig.getAvgFiles()/100;
				
		for(int i=0;i<popFileCount;i++)	{
			int randFile = MyRandom.getRandomNum(0, fileList.size());
			cat1FilesList.add(fileList.remove(randFile));
		}
		for(int i=0;i<avgFileCount;i++)	{
			int randFile = MyRandom.getRandomNum(0, fileList.size());
			cat2FilesList.add(fileList.remove(randFile));
		}
		ListIterator<String> li = fileList.listIterator();
		while(li.hasNext()){
			cat3FilesList.add(li.next());
			li.remove();
		}
		
		System.out.println("Cat-1 Files List is : \n"+cat1FilesList);
		System.out.println("Cat-2 Files List is :\n"+cat2FilesList);
		System.out.println("Cat-3 Files List is :\n"+cat3FilesList);
	}
	public List<String> generateLog()	{
		List<String> list = new ArrayList<String>();
		list.addAll(getAllCat1Sessions());
		list.addAll(getAllCat2Sessions());
		list.addAll(getAllCat3Sessions());
		Collections.shuffle(list);
		return list;
	}
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
	}
	
	private List<String> getAllCat1Sessions()	{
		List<String> cat1LogList = new LinkedList<String>(); 
		Iterator<String> itr = cat1FilesList.iterator();
		
		while(itr.hasNext())	{
			
			String fileName = itr.next();
				
			int fileEntriesCount1 = AccessLogConfig.CAT1_FILE_COUNT * MyRandom.getRandomNum(80, 95) / 100;
			
			String session = getRandSession(MyRandom.getRandomSessionSize());
			//System.out.println("Popular Session is : "+session);	
			
			String[] blocks = session.split(",");
			
			int[] fileBlock = new int[2];
			fileBlock[0] = Integer.parseInt(fileName.substring(1));
			fileBlock[1] = Integer.parseInt(blocks[1].substring(1));
		
			int[][] newLoc = getNewLocations(fileBlock);	// 4 by 2 array.
		
			
			int entryCount1 = fileEntriesCount1 * MyRandom.getRandomNum(10, 15) / 100;
			int entryCount3 = fileEntriesCount1 * MyRandom.getRandomNum(10, 15) / 100;
			int entryCount4 = fileEntriesCount1 * MyRandom.getRandomNum(10, 15) / 100;
			int entryCount2 = fileEntriesCount1 - (entryCount1+entryCount3+entryCount4);
			
			int devEntryCount1 = entryCount1 * MyRandom.getRandomNum(20, 30)/100;
			int devEntryCount2 = entryCount2 * MyRandom.getRandomNum(8, 15)/100;
			int devEntryCount3 = entryCount3 * MyRandom.getRandomNum(15, 20)/100;
			int devEntryCount4 = entryCount4 * MyRandom.getRandomNum(20, 30)/100;
			
			String popSession = "";
			
			List<String> deviantSessionList = getDeviantSessionList(session,2*blocks.length);	//devSession => A Deviant Session
			String[] dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			
			for(int i=0;i<entryCount1;i++)	{				// 1st Location (WDN)
				if(i<devEntryCount1)
					popSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+dsList[i%dsList.length];
				else	
					popSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+session;
					
				cat1LogList.add(popSession);
			}
			
			deviantSessionList = getDeviantSessionList(session,10);	//devSession => A Deviant Session 
			dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			
			for(int i=0;i<entryCount2;i++)	{	// 2nd Location
				if(i<devEntryCount2)
					popSession = "R"+newLoc[0][0]+",D"+newLoc[1][1]+","+fileName+","+dsList[i%dsList.length];	
				else	
					popSession = "R"+newLoc[1][0]+",D"+newLoc[1][1]+","+fileName+","+session;
					
				cat1LogList.add(popSession);
			}
			
			deviantSessionList = getDeviantSessionList(session,2*blocks.length);	//devSession => A Deviant Session 
			dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			
			for(int i=0;i<entryCount3;i++)	{	// 3rd Location
				if(i<devEntryCount3)
					popSession = "R"+newLoc[0][0]+",D"+newLoc[2][1]+","+fileName+","+dsList[i%dsList.length];	
				else
					popSession = "R"+newLoc[2][0]+",D"+newLoc[2][1]+","+fileName+","+session;
				
				cat1LogList.add(popSession);
			}
			deviantSessionList = getDeviantSessionList(session,2*blocks.length);	//devSession => A Deviant Session 
			dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			
			for(int i=0;i<entryCount4;i++)	{	// 4th Location
				if(i<devEntryCount4)
					popSession = "R"+newLoc[0][0]+",D"+newLoc[3][1]+","+fileName+","+dsList[i%dsList.length];	
				else
					popSession = "R"+newLoc[3][0]+",D"+newLoc[3][1]+","+fileName+","+session;
				cat1LogList.add(popSession);
			}
			for(int i=0;i<AccessLogConfig.CAT1_FILE_COUNT - fileEntriesCount1;i++)	{
			
				String unpopSession = getRandSession(MyRandom.getRandomSessionSize());
				int randRackNum = MyRandom.getRandomRack();
				int randDataNode = MyRandom.getRandomDatanode(randRackNum);
				unpopSession = "R"+randRackNum+",D"+randDataNode+","+fileName+","+session;
				cat1LogList.add(unpopSession);
			}
		}
		return cat1LogList;
	}
	
	private List<String> getDeviantSessionList(String session,int size) {
		
		String deviantSession = "";
		String[] sessionTokens = session.split(",");
		
		List<String> deviantSessionList = new LinkedList<String>();
			
		int pos=0;
		for(int i=0;i<size;i++)	{
			int j;
			deviantSession="";
			String newSession = getRandSession(sessionTokens.length);
			String[] newSessionTokens = newSession.split(",");
			if(i%2==0)	{
				
				for(j=0;j<newSessionTokens.length-1;j++)	{
					deviantSession += newSessionTokens[j]+",";
				}
				deviantSession += newSessionTokens[j];
				//System.out.println("deviantSession for 0 - "+deviantSession);
			}
			else{
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
				if(pos>sessionTokens.length-1)
					pos=0;
				for(j=0;j<newSessionTokens.length-1;j++)	{
					deviantSession += newSessionTokens[j]+",";
				}
				deviantSession += newSessionTokens[j];	// last block without comma.
				//System.out.println("deviantSession for 1 - "+deviantSession);
			}
			deviantSessionList.add(deviantSession);
		}
		return deviantSessionList;
		
	}
	private List<String> getAllCat2Sessions()	{
		
		List<String> cat2LogList = new LinkedList<String>(); 
		Iterator<String> itr = cat2FilesList.iterator();
		
		while(itr.hasNext())	{
			
			String fileName = itr.next();
			int avg_per = MyRandom.getRandomNum(80,95);
			int fileEntriesCount1 = AccessLogConfig.CAT2_FILE_COUNT * avg_per / 100;
			int fileEntriesCount2 = AccessLogConfig.CAT2_FILE_COUNT - fileEntriesCount1;
						
			String session = getRandSession(MyRandom.getRandomSessionSize());
			String[] blocks = session.split(",");
			
			int[] fileBlock = new int[2];
			fileBlock[0] = Integer.parseInt(fileName.substring(1));
			fileBlock[1] = Integer.parseInt(blocks[1].substring(1));
		
			int[][] newLoc = getNewLocations(fileBlock);	// 4 by 2 array.
		
			int pop_percent1 = MyRandom.getRandomNum(20, 25);
			int pop_percent2 = MyRandom.getRandomNum(50, 55);
			
			int entryCount1 = fileEntriesCount1 * pop_percent1 / 100;
			int entryCount2 = fileEntriesCount1 * pop_percent2 / 100;
			int entryCount3 = fileEntriesCount1 - (entryCount1 + entryCount2);	
			
			int devEntryCount1 = entryCount1 * MyRandom.getRandomNum(30,40)/100;
			int devEntryCount2  = entryCount2 * MyRandom.getRandomNum(30,40)/100;
			int devEntryCount3  = entryCount3 * MyRandom.getRandomNum(30,40)/100;
			
			String avgSession = "";
			
			List<String> deviantSessionList = getDeviantSessionList(session,3*blocks.length);	//devSession => A Deviant Session
			
			String[] dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			for(int i=0;i<entryCount1;i++)	{
				if(i<devEntryCount1)
					avgSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+dsList[i%dsList.length];
				else
					avgSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+session;
				cat2LogList.add(avgSession);
			}
			deviantSessionList = getDeviantSessionList(session,3*blocks.length);	//devSession => A Deviant Session
			dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			for(int i=0;i<entryCount2;i++)	{
				if(i<devEntryCount2)
					avgSession = "R"+newLoc[1][0]+",D"+newLoc[1][1]+","+fileName+","+dsList[i%dsList.length];
				else
					avgSession = "R"+newLoc[1][0]+",D"+newLoc[1][1]+","+fileName+","+session;
				cat2LogList.add(avgSession);
			}
			deviantSessionList = getDeviantSessionList(session,3*blocks.length);	//devSession => A Deviant Session
			dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			for(int i=0;i<entryCount3;i++)	{
				if(i<devEntryCount3)
					avgSession = "R"+newLoc[2][0]+",D"+newLoc[2][1]+","+fileName+","+dsList[i%dsList.length];
				else
					avgSession = "R"+newLoc[2][0]+",D"+newLoc[2][1]+","+fileName+","+session;
				cat2LogList.add(avgSession);
			}
			
			for(int i=0;i<fileEntriesCount2;i++)	{
			
				String randSession = getRandSession(MyRandom.getRandomSessionSize());
				int randRackNum = MyRandom.getRandomRack();
				int randDataNode = MyRandom.getRandomDatanode(randRackNum);
				randSession = "R"+randRackNum+",D"+randDataNode+","+fileName+","+session;
				cat2LogList.add(randSession);	
			}
		}
		return cat2LogList;
	}
	
	private List<String> getAllCat3Sessions()	{
		
		List<String> cat3LogList = new LinkedList<String>(); 
		Iterator<String> itr = cat3FilesList.iterator();
		
		while(itr.hasNext())	{
			
			String fileName = itr.next();
			
			int fileEntryPercent = MyRandom.getRandomNum(80,95);
			int fileEntryCount1 = AccessLogConfig.CAT3_FILE_COUNT * fileEntryPercent / 100;
			int fileEntryCount2 = AccessLogConfig.CAT3_FILE_COUNT - fileEntryCount1;
			
			//System.out.println("File : "+fileName+" unPop per : "+unpop_per+" Random_per : "+(100-unpop_per)+" unpopEntryCount = "+unpopEntriesCount);
			
			String session = getRandSession(MyRandom.getRandomSessionSize());
			String[] blocks = session.split(",");
			
			int[] fileBlock = new int[2];
			fileBlock[0] = Integer.parseInt(fileName.substring(1));
			fileBlock[1] = Integer.parseInt(blocks[1].substring(1));
		
			int[][] newLoc = getNewLocations(fileBlock);	// 4 by 2 array.
		
			int pop_percent1 = MyRandom.getRandomNum(30, 35);
					
			int entryCount1 = fileEntryCount1 * pop_percent1 / 100;
			int entryCount2 = fileEntryCount1 - entryCount1;
					
			int devEntryCount1 = entryCount1 * MyRandom.getRandomNum(50,60)/100;
			int devEntryCount2  = entryCount2 * MyRandom.getRandomNum(50,60)/100;
				
			String finalSession = "";
			
			List<String> deviantSessionList = getDeviantSessionList(session,5*blocks.length);	//devSession => A Deviant Session
			
			String[] dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			for(int i=0;i<entryCount1;i++)	{
				if(i<devEntryCount1)
					finalSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+dsList[i%dsList.length];
				else
					finalSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+session;
				cat3LogList.add(finalSession);
			}
			
			deviantSessionList = getDeviantSessionList(session,5*blocks.length);	//devSession => A Deviant Session
			dsList = new String[deviantSessionList.size()];
			dsList = deviantSessionList.toArray(dsList);
			for(int i=0;i<entryCount2;i++)	{
				if(i<devEntryCount2)
					finalSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+dsList[i%dsList.length];
				else
					finalSession = "R"+newLoc[0][0]+",D"+newLoc[0][1]+","+fileName+","+session;
				cat3LogList.add(finalSession);
			}
			
			for(int i=0;i<fileEntryCount2;i++)	{
			
				String unpopSession = getRandSession(MyRandom.getRandomSessionSize());
				int randRackNum = MyRandom.getRandomRack();
				int randDataNode = MyRandom.getRandomDatanode(randRackNum);
				unpopSession = "R"+randRackNum+",D"+randDataNode+","+fileName+","+session;
				cat3LogList.add(unpopSession);	
			}
		}
		return cat3LogList;
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
		public List<String> getBlockList()	{
			ArrayList<String> blockList = new ArrayList<String>();
			blockList.ensureCapacity(systemConfig.getBlocks());
			for(int i=1;i<=systemConfig.getBlocks();i++)	{
				blockList.add("B"+i);
			}
			return blockList;
		}
		
		public String getLoc_1(String file,String block)	{
			return hm_MetaData[0].get(file+block);
		}
		public String getLoc_2(String file,String block)	{
			return hm_MetaData[1].get(file+block);
		}
		public String getLoc_3(String file,String block)	{
			return hm_MetaData[2].get(file+block);
		}
		private int[][] getNewLocations(int[] popSession)	{
			
			int[][] req_Loc = new int[4][2];
			int[][] rdn_Loc = getOrigLocations(popSession);
			req_Loc = getReqLocations(rdn_Loc);
		
			return req_Loc;
		}
		private	 int[][] getOrigLocations(int[] popSession){
			
			String l1,l2,l3;
			int[][] rdn_Loc = new int[3][2];  
			
			l1 = hm_MetaData[0].get("F"+popSession[0]+" B"+popSession[1]);
			l2 = hm_MetaData[1].get("F"+popSession[0]+" B"+popSession[1]);
			l3 = hm_MetaData[2].get("F"+popSession[0]+" B"+popSession[1]);
					
			String[] s = l1.split("D");
			rdn_Loc[0][0] = Integer.parseInt(s[0].substring(1,s[0].length()));
			rdn_Loc[0][1] = Integer.parseInt(s[1]);
			
			s = l2.split("D");
			rdn_Loc[1][0] = Integer.parseInt(s[0].substring(1,s[0].length()));
			rdn_Loc[1][1] = Integer.parseInt(s[1]);
			
			s = l3.split("D");
			rdn_Loc[2][0] = Integer.parseInt(s[0].substring(1,s[0].length()));
			rdn_Loc[2][1] = Integer.parseInt(s[1]);
			
			return rdn_Loc;
		}
		
		private int[][] getReqLocations(int[][] rdn_Loc) { // Get Request Locations for File.
			
			int[][] req_Loc = new int[4][2];

			int r,dn;
											// 1st Pop Location (write client).
			req_Loc[0][0] = rdn_Loc[0][0];	// same rack
			req_Loc[0][1] = rdn_Loc[0][1];	// same datanode
			
											// 2nd Pop Location				
			do	{	
				r = MyRandom.getRandomRack();	// random rack			
			}while (r==rdn_Loc[0][0] || r == rdn_Loc[1][0]);
			
			req_Loc[1][0] = r;
			
			dn = MyRandom.getRandomDatanode(req_Loc[1][0]); // random datanode
			req_Loc[1][1] = dn;
											// 3rd Pop Location
			req_Loc[2][0] = rdn_Loc[2][0];	// same rack
			
			do 	{
				dn = MyRandom.getRandomDatanode(req_Loc[2][0]); // different datanode
			}while(dn == rdn_Loc[0][1] || dn == rdn_Loc[2][1]);
			req_Loc[2][1] = dn;
					
			do	{	
				r = MyRandom.getRandomRack();					//getOtherRack( rdn_Loc[1][0]);
			}while (r==req_Loc[0][0] || r == req_Loc[1][0]);
			
			req_Loc[3][0] = r; 			
			req_Loc[3][1] = MyRandom.getRandomDatanode(req_Loc[3][0]);	// random datanode
			
			return req_Loc;
		}
		
		public static void main(String[] args){
			
			String a = "Hello,world";
			System.out.println(a+" "+a.substring(0, a.length()-1));
			String session = "B1,B2,B3,B4,B5,B6,B7,B8,B9,B10";
			List<String> dList = new GenerateAccessLog().getDeviantSessionList(session,10);
			Iterator<String> itr = dList.iterator();
			while(itr.hasNext())
				System.out.println(itr.next());
		}
}
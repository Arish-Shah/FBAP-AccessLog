import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Main {

	public static void main(String[] args) {
		
		initSystem();
		System.out.println("Finished Initializing System...");
	//	TestDisplay.displayMyRandomTest();	// Random Number Testing
		
	//	generateMetaDataHDFS();
		System.out.println("Metadata Generated...");
		
		generateAccessLog();
		System.out.println("Access Log Generated...");
		
	/*	DataNode[] dns = readLog();
		System.out.println("Log Reading Successful...");
		
		for(int i=1;i<dns.length;i++){		// DN starts from 1 to N
			dns[i].calFreq();
			
			writeLFFile(dns[i],i);
			writeLFBlock(dns[i],i);
			
		}
		System.out.println("Freq Calculation Successful...");
		
		for(int i=1;i<dns.length;i++){
			dns[i].calSupport();
			
			writeLFPattern(dns[i],i);
			
			writeLSFile(dns[i],i);
			writeLSBlock(dns[i],i);
			writeLSPattern(dns[i],i);
			
		}
		System.out.println("Local Support Calculation Successful...");
				
		GlobalSupport gs = new GlobalSupport(dns);
		gs.calGlobalSupport();	
		
		writeGFFile(gs);
		writeGFBlock(gs);
		writeGFPattern(gs);
		
		System.out.println("Global Frequency Calculation Successful...");
		//System.out.println("Finished Calculating Frequency Values...");
		
		writeGSFile(gs);
		writeGSBlock(gs);
		writeGSPattern(gs);
		System.out.println("Global Support Calculation Successful...");
		
		//System.out.println("Finished Calculating Global Support Values...");
		
		CategorizeBean catBlockBean = getCategorization(dns,gs);
		writeCategorize(catBlockBean);
		System.out.println("Block Categorization Successful...");
		
		String[][] mdSBPA = generateMetaDataFBAP(catBlockBean,dns,gs);
		writeMDSBPA(mdSBPA);
		System.out.println("Meta Data FBAP Generated Successful...");
		
		CategorizeBean catFileBean = getCatFileBean(catBlockBean);		// Evaluation - Replica Factor.
		writeRF(catFileBean);
		System.out.println("Replica Factor Data Generated Successful...");
		
		//CategorizeBean catFileBean = getCatFileBean(catBean);		// Evaluation - Replica Factor.
		//writeRF(catFileBean);
		//System.out.println("Replica Factor Data Generated...");
	*/}

	private static void writeRF(CategorizeBean catFileBean) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/Rf.csv"))){
			
			SystemConfig systemConfig = SystemConfig.getInstance();
			
			Set<String> cat1 = catFileBean.getCat1();
			Set<String> cat2 = catFileBean.getCat2();
			//Set<String> cat3 = catFileBean.getCat3();
			int copies=2;
			writer.write("S.No."+", "+"File Name"+","+" HDFS Replications"+", "+"FBAP Replications");
			writer.newLine();
			for(int i=1;i<=systemConfig.getFiles();i++)
			{
				String fileName = "F"+i;
				if(cat1.contains(fileName))
					copies = 4;
				else if(cat2.contains(fileName))
					copies = 3;
				else //if(cat3.contains(fileName))
					copies = 2;
				
				writer.write(i+", "+fileName+","+" 3,"+copies);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}


	private static CategorizeBean getCatFileBean(CategorizeBean catBlockBean) {
		
		CategorizeBean catFileBean = new CategorizeBean();
		
		List<String> fileList = new ArrayList<String>();
		
		for(int i=1;i<SystemConfig.getInstance().getFiles();i++)	{
			fileList.add("F"+i);
		}
		
		Set<String> catFile1 = new TreeSet<String>();
		Set<String> catFile2 = new TreeSet<String>();
		Set<String> catFile3 = new TreeSet<String>();
		
		Set<String> cat1 = catBlockBean.getCat1();
		Iterator<String> cat1Itr = cat1.iterator();
		while(cat1Itr.hasNext())	{
			String pat = cat1Itr.next();
			String[] patToken = pat.split(" ");
			//if(!catFile.contains(patToken[0].trim()))
			//	System.out.println(patToken[0].trim());
			String fileName = patToken[0].trim();
			if(fileList.contains(fileName))	{
				catFile1.add(fileName);
				fileList.remove(fileName);
			}
		}	
		Set<String> cat2 = catBlockBean.getCat2();
		//System.out.println("cat2 blocks : "+cat2);
		Iterator<String> cat2Itr = cat2.iterator();
		while(cat2Itr.hasNext())	{
			String pat = cat2Itr.next();
			String[] patToken = pat.split(" ");
			//if(!catFile.contains(patToken[0].trim()))
			//	System.out.println(patToken[0].trim());
			String fileName = patToken[0].trim();
			if(fileList.contains(fileName))	{
				catFile2.add(fileName);
			//	System.out.println("catFile2"+catFile2);
				fileList.remove(fileName);
			}
		}
		
		Iterator<String> cat3Itr = fileList.iterator();
		while(cat3Itr.hasNext())	{
			catFile3.add(cat3Itr.next());
		}
		catFileBean.setCat1(catFile1);
		catFileBean.setCat2(catFile2);
		catFileBean.setCat3(catFile3);
		System.out.println("Cat1 files size : "+catFileBean.getCat1().size()+"\nContents : "+catFileBean.getCat1());
		System.out.println("Cat1 files size : "+catFileBean.getCat2().size()+"\nContents : "+catFileBean.getCat2());
		System.out.println("Cat1 files size : "+catFileBean.getCat3().size()+"\nContents : "+catFileBean.getCat3());
	
		return catFileBean;
	}
/*	private static Set<String> getFileCat(Set<String> cat)	{
		Set<String> catFile = new TreeSet<String>();
		Iterator<String> catItr = cat.iterator();
		while(catItr.hasNext())	{
			String pat = catItr.next();
			String[] patToken = pat.split(" ");
			//if(!catFile.contains(patToken[0].trim()))
			//	System.out.println(patToken[0].trim());
			catFile.add(patToken[0].trim());
		}
		return catFile;
		
	}*/
	

	private static void writeCategorize(CategorizeBean catBean) {
		writeCat1(catBean.getCat1());
		writeCat2(catBean.getCat2());
		writeCat3(catBean.getCat3());
		writeFBAPList(catBean.getFBAPList());
		
	}

	private static void writeFBAPList(List<String> fbapList) {
	
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/Cat/FBAP_List.txt"))){
			Iterator<String> fbapItr = fbapList.iterator();
			while(fbapItr.hasNext())	{
				String pattern = fbapItr.next();	
				writer.write(pattern);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}

	private static void writeCat3(Set<String> cat3) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/Cat/Cat3.txt"))){
			Iterator<String> catItr = cat3.iterator();
			while(catItr.hasNext())	{
				String block = catItr.next();	
				writer.write(block);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}

	private static void writeCat2(Set<String> cat2) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/Cat/Cat2.txt"))){
			Iterator<String> catItr = cat2.iterator();
			while(catItr.hasNext())	{
				String block = catItr.next();	
				writer.write(block);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}

	private static void writeCat1(Set<String> cat1) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/Cat/Cat1.txt"))){
			Iterator<String> catItr = cat1.iterator();
			while(catItr.hasNext())	{
				String block = catItr.next();	
				writer.write(block);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}		
	}

	private static void writeMDSBPA(String[][] mdSBPA) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/MetaData_FBAP.txt"))){
			
			for(int i=0;i<mdSBPA.length;i++)	{
				
				writer.write(mdSBPA[i][0]+"\t"+mdSBPA[i][1]+"\t"+mdSBPA[i][2]+"\t"+mdSBPA[i][3]+"\t"+mdSBPA[i][4]);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
		
	}

	private static String[][] generateMetaDataFBAP(CategorizeBean catBean, DataNode[] dns, GlobalSupport gs) {
		
		SystemConfig systemConfig = SystemConfig.getInstance();
		String[][] mdSBPA = new String[systemConfig.getFiles()* systemConfig.getBlocks()][5]; // "f1 b1","1st","2nd","3rd","4th".
		
		String dn1="",dn3="";
		Set<String> cat1 = catBean.getCat1();	// e.g.,"F1 B1"
		Set<String> cat2 = catBean.getCat2();
		//Set<String> cat3 = catBean.getCat3();
		
		MetaDataInfo metaDataInfo = MetaDataInfo.getMetaDataInfo();
			
		for(int x=0;x<mdSBPA.length;x++)	{	// // For Each File and For Each Block
				
				int k;
				String tempRack,rack1,rack2=null,rack3=null,rack4=null;
				String loc1="",loc2="",loc3="",loc4="";
				
				int i = x / 1000 + 1;	// file index
				int j = x % 1000 + 1; 	// block index
				
				String fileBlock = "F"+i+" "+"B"+j;
				
				loc1 = metaDataInfo.getLoc1(fileBlock);
								
				double[][] fileBlockLS = getAllLocalSupport(dns,i,j);	// check if its null or not
				if(fileBlockLS==null)	{
				
					loc2 = metaDataInfo.getLoc2(fileBlock);
				}
					
				else	{
					//for(int q=0;q<fileBlockLS.length;q++)
						//System.out.println("fileBlock : "+fileBlock+" Datanode : "+fileBlockLS[q][0]+" Support : "+fileBlockLS[q][1]);
					
					String[] tok = loc1.split("D");	//	
					rack1 = tok[0];
					dn1 = "D"+tok[1];
					
					k=0;								// 2nd Copy @ DataNode with Highest Supp Value other than WDN Group.
					while(k < fileBlockLS.length)	{	
						tempRack = "R"+((((int)fileBlockLS[k][0])-1) / systemConfig.getClusterSize() + 1);	// finds Rack of datanode.
						
						if(!rack1.equals(rack2))	{
							rack2 = tempRack;
							break;
						}
						k++;
					}
					if(rack2!=null)	{
					
						loc2 = rack2+"D"+((int)fileBlockLS[k][0]);
						
					}
					else	{
						
						loc2 = metaDataInfo.getLoc2(fileBlock);	// Keep the old location, unable to find the other rack.
					}
										
					if(cat2.contains(fileBlock) || cat1.contains(fileBlock))	{	// 3rd Copy
						
						k=0;								// 3rd Copy @ DataNode with Highest Supp Value in WDN Group (but in datanode other than WDN).
						while(k < fileBlockLS.length)	{	
							tempRack = "R"+((((int)fileBlockLS[k][0])-1) / systemConfig.getClusterSize() + 1);	// finds Rack of datanode.
							dn3 = "D"+(int)(fileBlockLS[k][0]);
							//System.out.println("dn1 : "+dn1+" dn3 : "+dn3);
							//System.out.println(rack1+".equals("+tempRack+") : "+rack1.equals(tempRack)+" (!"+dn1+".equals("+dn3+")) : "+(!dn1.equals(dn3)));
							if(rack1.equals(tempRack) && (!dn1.equals(dn3)))	{	// need to also check datanode simultaneously.
								rack3 = tempRack;
								break;
							}
							k++;
						}
						if(rack3!=null)	{
							loc3 = rack3 + dn3;//rack3+"D"+((int)fileBlockLS[k][0]);
						}
						else	{								// Should not happen
						//	System.out.println("No Loc found for third copy...");
							loc3 = metaDataInfo.getLoc3(fileBlock);;	// Keep the old location.
						}
					}
					
					if(cat1.contains(fileBlock))	{	// 4th Copy
						
						k=0;								// 4th Copy @ DataNode with Highest Supp Value other than WDN Group and rack2.
						while(k<fileBlockLS.length)	{	
							tempRack = "R"+((((int)fileBlockLS[k][0])-1) / systemConfig.getClusterSize() + 1);	// finds Rack of datanode.
							if((!tempRack.equals(rack1)) && (!(tempRack.equals(rack2))))	{
								rack4 = tempRack;
								break;
							}
							k++;
						}
						if(rack4!=null)	{
							loc4 = rack4+"D"+((int)fileBlockLS[k][0]);
						}
						else	{
							loc4 = "Loc-N/A";
							//System.out.println("No Loc found for fourth copy...");
						}
					}
				}
				mdSBPA[x][0] = fileBlock;
				mdSBPA[x][1] = loc1;
				mdSBPA[x][2] = loc2;
				mdSBPA[x][3] = loc3;
				mdSBPA[x][4] = loc4;
				//if(x==26699)//26699)
				//	break;
			}
		return mdSBPA;
	}
	

	private static double[][] getAllLocalSupport(DataNode[] dns,int i,int j) {
		int k;
		double tempDN,tempSupp,supp;
		Map<Integer,Double> blockSupport = new HashMap<Integer,Double>();
		double[][] dnSupp = null;
		for(k = 1; k < dns.length; k++)	{
			supp = dns[k].getBlockSupp(i, j);
			if(supp!=0)	{
				blockSupport.put(dns[k].getDnNum(), supp);
			}
		}
		Set<Map.Entry<Integer, Double>> blockSet= blockSupport.entrySet();
		if(!blockSet.isEmpty())	{
			dnSupp = new double[blockSet.size()][2];
			Iterator<Map.Entry<Integer, Double>> blockItr = blockSet.iterator();
			k=0;
			while(blockItr.hasNext())	{
				Map.Entry<Integer, Double> blockEntry = blockItr.next();
				dnSupp[k][0] = blockEntry.getKey();
				dnSupp[k][1] = blockEntry.getValue();	// dnSupp stores dnNum and its file block support.
				k++;
			}
			
			for(int pass=1;pass<dnSupp.length;pass++)	{	//	Applying bubble sort to arrange support in decreasing order.
				for(k=0;k<dnSupp.length-1;k++)	{
					if(dnSupp[k][1]< dnSupp[k+1][1])	{
						
						tempDN = dnSupp[k][0];
						tempSupp = dnSupp[k][1];
						
						dnSupp[k][0] = dnSupp[k+1][0];
						dnSupp[k][1] = dnSupp[k+1][1];
						
						dnSupp[k+1][0] = tempDN;
						dnSupp[k+1][1] = tempSupp;
					}
				}
			}
		}
		return dnSupp;
	}

	private static CategorizeBean getCategorization(DataNode[] dns, GlobalSupport gs) {
		
		List<String> FBAP_List = getFBAPList(dns);
		
		double[][] bSuppG = gs.getBlockSuppG();
		Set<String> setFBAP = new HashSet<String>();
		Iterator<String> fbapItr = FBAP_List.iterator();
		while(fbapItr.hasNext())	{
			String pattern = fbapItr.next();
			String[] patTokens = pattern.split(" ");
			String fileName = patTokens[1];	// file name
			for(int i=2;i<patTokens.length;i++)	{
				String block = fileName+" "+patTokens[i];
				setFBAP.add(block);
			}
		}
		
		Set<String> cat1 = new TreeSet<String>();
		Set<String> cat2 = new TreeSet<String>();
		Set<String> cat3 = new TreeSet<String>();
		
		//System.out.println("Cat-1 List: \n"+FBAP_List); 
		
		SystemConfig systemConfig = SystemConfig.getInstance();
		for(int i=1;i<=systemConfig.getFiles();i++)	{
			for(int j=1;j<=systemConfig.getBlocks();j++)	{
				
				String block = "F"+i+" "+"B"+j;
				if(setFBAP.contains(block))	{
					cat1.add(block);
				}
				else if(bSuppG[i][j]>=Cat.minSup1 && bSuppG[i][j]<Cat.minSup)	{
					//System.out.println("Cat-2 block "+block);
					cat2.add(block);
				}
				else	if(bSuppG[i][j]<Cat.minSup1){
					cat3.add(block);
				}
			}
		}
		CategorizeBean catBean = new CategorizeBean();
		catBean.setFBAPList(FBAP_List);
		catBean.setCat1(cat1);
		catBean.setCat2(cat2);
		catBean.setCat3(cat3);
		return catBean;
	}

	private static List<String> getFBAPList(DataNode[] dns) {
		
		List<String> FBAP_List = new LinkedList<String>();
		
		for(int k=1;k<dns.length;k++)	{
			Map<String,Double> pMap = dns[k].getpMap();
			Set<Map.Entry<String, Double>> pMapEntrySet = pMap.entrySet(); 
			Iterator<Map.Entry<String, Double>> pMapEntrySetItr = pMapEntrySet.iterator();
			while(pMapEntrySetItr.hasNext())	{
				Map.Entry<String, Double> entry= pMapEntrySetItr.next();
				if(entry.getValue() >= Cat.minSup)	{
					FBAP_List.add("D"+k+" "+entry.getKey());
				}
			}
		}
		return FBAP_List;
	}

	private static void writeLFFile(DataNode dataNode, int dnNum) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/freq/file/FileFreqDN-"+dnNum+".csv"))){
			
			int[] fFreq = dataNode.getfFreq();
			for(int i=1;i<fFreq.length;i++)	{
				
				writer.write("F"+i+" , "+fFreq[i]);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	private static void writeLFBlock(DataNode dataNode, int dnNum) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/freq/block/BlockFreqDN-"+dnNum+".csv"))){
			
			int[][] bFreq = dataNode.getbFreq();
			for(int i=1;i<bFreq.length;i++)	{
				for(int j=1;j<bFreq[i].length;j++)	{
				//	if(bFreq[i][j]!=0)	{
						writer.write("F"+i+",B"+j+" , "+bFreq[i][j]);
						writer.newLine();
				//	}
				}
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	private static void writeLFPattern(DataNode dataNode, int dnNum) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/freq/pattern/PatternFreqDN-"+dnNum+".csv"))){
			
			Map<String,Integer> pBaseFreqMap = dataNode.getpMapBaseFreq();
			Map<String,Integer> pFreqMap = dataNode.getpFreqMap();
			Map<String,Double> pMap = dataNode.getpMap();
		
			Set<String> pSet = pMap.keySet();
			Iterator<String> pItr = pSet.iterator();
			writer.write("Pattern"+" , "+"Freq"+" ,"+"Base-Freq");
			writer.newLine();
			while(pItr.hasNext())	{
				String key = pItr.next();
				int freq = pFreqMap.get(key);
				int baseFreq = pBaseFreqMap.get(key);
				writer.write(key+" , "+freq+" ,"+baseFreq);
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	private static void writeGFPattern(GlobalSupport gs) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/freq/GlobalFreq-Pattern.csv"))){
			
			Map<String,Integer> pFreqG = gs.getpFreqMapG();
			Map<String,Integer> pBaseFreqG = gs.getpBaseFreqMapG();
			Iterator<String> patItr = pFreqG.keySet().iterator();
			while(patItr.hasNext())	{
				String key = patItr.next();
				int freq = pFreqG.get(key);
				int baseFreq = pBaseFreqG.get(key);
				writer.write(key+" , "+freq+", "+baseFreq);
				writer.newLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void writeGFBlock(GlobalSupport gs) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/freq/GlobalFreq-Block.csv"))){
			
			int[][] blockSuppG = gs.getBlockFreqG();
			for(int i=1;i<blockSuppG.length;i++)	{
				for(int j=1;j<blockSuppG[i].length;j++)	{
					writer.write("F"+i+", B"+j+" , "+blockSuppG[i][j]);
					writer.newLine();
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void writeGFFile(GlobalSupport gs) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/freq/GlobalFreq-File.csv"))){
			
			int[] fileFreqG = gs.getFileFreqG();
			for(int i=1;i<fileFreqG.length;i++)	{
				
				writer.write("F"+i+","+fileFreqG[i]);
				writer.newLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private static void writeGSFile(GlobalSupport gs) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/supp/GlobalSupp-File.csv"))){
			
			double[] fileSuppG = gs.getFileSuppG();
			for(int i=1;i<fileSuppG.length;i++)	{
				
				writer.write("F"+i+","+fileSuppG[i]);
				writer.newLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void writeGSBlock(GlobalSupport gs) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/supp/GlobalSupp-Block.csv"))){
			
			double[][] blockSuppG = gs.getBlockSuppG();
			for(int i=1;i<blockSuppG.length;i++)	{
				for(int j=1;j<blockSuppG[i].length;j++)	{
					writer.write("F"+i+", B"+j+" , "+blockSuppG[i][j]);
					writer.newLine();
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void writeGSPattern(GlobalSupport gs) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/supp/GlobalSupp-Pattern.csv"))){
			
			Map<String,Double> pMapG = gs.getpMapG();
			Iterator<String> patItr = pMapG.keySet().iterator();
			while(patItr.hasNext())	{
				String key = patItr.next();
				double value = pMapG.get(key);
				writer.write(key+" , "+value);
				writer.newLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void writeLSFile(DataNode dataNode,int dnNum) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/supp/file/LSFile_DN-"+dnNum+".csv"))){
			
			Map<String,Double> fmap = dataNode.getfMap();			//  Writing File Map.
			Set<Map.Entry<String,Double>> fSet = fmap.entrySet();
			Iterator<Map.Entry<String,Double>> fItr = fSet.iterator();
			
			while(fItr.hasNext())	{
				Map.Entry<String, Double> me = fItr.next();
				writer.write(me.getKey()+" , "+me.getValue());
				writer.newLine();
			}
			
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	private static void writeLSBlock(DataNode dataNode,int dnNum) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/supp/block/LSBlock_DN-"+dnNum+".csv"))){
			
			Map<String,Double> bmap = dataNode.getbMap();			//  Writing Block Map.
			Set<Map.Entry<String,Double>> bSet = bmap.entrySet();
			Iterator<Map.Entry<String,Double>> bItr = bSet.iterator();
			
			while(bItr.hasNext())	{
				Map.Entry<String, Double> me = bItr.next();
				String[] keyTokens = me.getKey().split(" ");
				for(int i=0;i<keyTokens.length;i++)
					writer.write(keyTokens[i]+",");
				writer.write(""+me.getValue());
				writer.newLine();
			}
			
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	private static void writeLSPattern(DataNode dataNode, int dnNum) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/HP/Desktop/DvS/rs/output/supp/pattern/LSPattern_DN-"+dnNum+".csv"))){
			
			Map<String,Double> pMap = dataNode.getpMap();			//  Writing Pattern Map.
			Set<Map.Entry<String,Double>> pSet = pMap.entrySet();
			Iterator<Map.Entry<String,Double>> pItr = pSet.iterator();
			
			writer.write("Pattern Support : ");
			writer.newLine();
			while(pItr.hasNext())	{
				Map.Entry<String,Double> me = pItr.next();
				writer.write(me.getKey()+" , "+me.getValue());
				writer.newLine();
			}
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	
	public static void initSystem()	{
		File file = new File("C:\\Users\\Arish\\Desktop\\eclipse-workspace\\rs\\input\\System-Config.properties");
		SystemConfig systemConfig = LoadSystemProperties.loadProperties(file);
		TestDisplay.displaySystemConfig(systemConfig);	
	}
	private static void generateMetaDataHDFS()	{
		MetaDataHDFS md = new MetaDataHDFS();	// Metadata generation
		md.transferMDToFile("C:/Users/HP/Desktop/DvS/rs/output/Metadata_HDFS.txt");
		
	}
	private static void generateAccessLog()	{
		//GenerateAccessLog gal = new GenerateAccessLog();
		NewGAL gal = new NewGAL();
		//List<String> list = gal.generateLog();
		//gal.
		//transferALToFile("C:/Users/HP/Desktop/DvS/rs/output/AccessLog.csv",list);	
	}
	public static void transferALToFile(String fileName,List<String> list) {
		
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
	private static DataNode[] readLog()	{
		int dnNum;
		String entry,session;
		DataNode[] dns = new DataNode[SystemConfig.getInstance().getDataNodes()+1];
		for(int i=0;i<dns.length;i++)
			dns[i] = new DataNode(i);
	try (BufferedReader reader = new BufferedReader(new FileReader("C:/Users/HP/Desktop/DvS/rs/output/Log.csv"))){
		while((entry=reader.readLine())!=null){
			dnNum = getDataNodeNumber(entry); 
			session = getSession(entry);	// separator char (,) is set in session.
			dns[dnNum].addLog(session);
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return dns;
	}
	private static String getSession(String entry) {
		
		String session="";
		String[] st = entry.split(",");
		for(int i=2;i<st.length;i++)	
			session +=st[i]+",";	// separator char (,) is set in session.
		return session.substring(0, session.length()-1);
	}
	private static int getDataNodeNumber(String entry) {
		int dnNum=0;
		String[] st = entry.split(",");
		dnNum = Integer.parseInt(st[1].substring(1));
		return dnNum;
	}
}

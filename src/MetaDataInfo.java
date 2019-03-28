

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MetaDataInfo {
	
	HashMap<String,String>[] hm_MetaData;
	private static MetaDataInfo metaDataInfo = null;
	
	public static MetaDataInfo getMetaDataInfo()	{
		if(metaDataInfo==null)
			metaDataInfo = new MetaDataInfo();
		return metaDataInfo;
	}
	private MetaDataInfo()	{	//private constructor
		
		init();
		readMetaData();
	}
	
	@SuppressWarnings("unchecked")
	public void init()	{
		
		hm_MetaData = new HashMap[3];
		for(int i=0;i<3;i++)	{
			hm_MetaData[i] = new HashMap<String,String>();
		}
	}
	
	public void readMetaData()	{
		
		FileReader fr_MDB = null;
		BufferedReader br_MDB = null;
		String key,value1,value2,value3;
		try {
			
			fr_MDB = new FileReader("C:/Users/HP/Desktop/DvS/rs/output/Metadata_HDFS.txt");
			br_MDB = new BufferedReader(fr_MDB);
			
			 String entry;
			  while((entry = br_MDB.readLine())!= null )	{
				// System.out.println("Meta Data entry : "+entry);
				  StringTokenizer st = new StringTokenizer(entry);
				  key = st.nextToken() +" "+st.nextToken();
				  
				  value1 = st.nextToken();
				  value2 = st.nextToken();
				  value3 = st.nextToken();
				  
				  hm_MetaData[0].put(key, value1);
				  hm_MetaData[1].put(key, value2);
				  hm_MetaData[2].put(key, value3);
			  }
			  
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		finally	{
			try	{
				if(fr_MDB!=null)
					fr_MDB.close();
				if(br_MDB!=null)
					br_MDB.close();
			}
			catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
	public HashMap<String, String>[] getMD_HashMap()	{
		return hm_MetaData;
	}
	public String getLoc1(String block)	{
		return hm_MetaData[0].get(block);
	}
	public String getLoc2(String block)	{
		return hm_MetaData[1].get(block);
	}
	public String getLoc3(String block)	{
		return hm_MetaData[2].get(block);
	}
	public static void main(String[] args) {
		
		MetaDataInfo md = new MetaDataInfo();
		System.out.println(md.hm_MetaData[0]);

	}

}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MetaDataHDFS {

	int[][] metaData;
	SystemConfig systemConfig;

	public MetaDataHDFS()	{
		
		systemConfig = SystemConfig.getInstance();
		metaData = new int[systemConfig.getFiles() * systemConfig.getBlocks()][8]; // Total blocks with 3-replicas -(Fno,Bno,R1,DN1,R2,DN2,R3,DN3)
		generateMD();
	}
	public void generateMD()	{
		
		int rand,temp;
		int r1,r2,r3;
		int d1,d2,d3;
		
		for(int fno = 0;fno<systemConfig.getFiles();fno++)	{
			
			for(int bno = 0; bno<systemConfig.getBlocks();bno++)	{
				
				metaData[fno*systemConfig.getBlocks()+bno][0] = fno+1;
				metaData[fno*systemConfig.getBlocks()+bno][1] = bno+1;
				
				//rand = (int)((Math.random()+.001)*1000)%100;	//0 -99
				rand = MyRandom.getRandomNum(1, systemConfig.getRacks());
				
				//r1 = rand / 10 + 1;	// 1st copy of file placement in HDFS.
				r1 = rand;
				d1 = rand % 10 + 1;
				
				do{													// 2nd copy of file placement in HDFS(Different Rack).
					//rand = (int)((Math.random()+.001)*1000)%100;
					rand = MyRandom.getRandomNum(1, systemConfig.getRacks());
					//temp = rand / 10 + 1;			//temp denotes a rack value.
					
				}while(rand==r1);
				r2 = rand;
				d2 = rand % 10 + 1;
				
				r3 = r1;			// 3rd copy of file placement in HDFS (Same Rack but Different Datanode).
				do{
					//rand = (int)((Math.random()+.001)*1000)%100;
					rand = MyRandom.getRandomNum(1, systemConfig.getRacks());
					temp = rand % 10 + 1;			//temp denotes a Datanode value.
					
				}while(temp==d1);
				d3 = temp;
				
				metaData[fno*systemConfig.getBlocks()+bno][2] = r1;
				metaData[fno*systemConfig.getBlocks()+bno][3] = (r1-1)*10+d1;
				metaData[fno*systemConfig.getBlocks()+bno][4] = r2;
				metaData[fno*systemConfig.getBlocks()+bno][5] = (r2-1)*10+d2;
				metaData[fno*systemConfig.getBlocks()+bno][6] = r3;
				metaData[fno*systemConfig.getBlocks()+bno][7] = (r3-1)*10+d3;
			}
		}
	}
	
	public int[][] getMetaData()	{
		return metaData;
	}
	public void transferMDToFile(String fileName){
			
		try (FileWriter writerMD = new FileWriter(fileName);
			 BufferedWriter bufferedWriterMD = new BufferedWriter(writerMD))	{
			
				for(int i = 0;i<systemConfig.getFiles()*systemConfig.getBlocks();i++)	{
					
					String entry = "F"+metaData[i][0]+"\tB"+metaData[i][1]+"\tR"+metaData[i][2]+"D"+metaData[i][3]+
									"\tR"+metaData[i][4]+"D"+metaData[i][5]+"\tR"+metaData[i][6]+"D"+metaData[i][7];
					
					bufferedWriterMD.write(entry);
					bufferedWriterMD.newLine();
				}
				
				bufferedWriterMD.flush();
				writerMD.flush();
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

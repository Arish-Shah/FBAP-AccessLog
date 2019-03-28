import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class DummyTest {
	
	private void updatePatternMap(Map<String,Double> pSuppMap)	{	// cut out unnecessary patterns (or sub patterns).
		
		Set<String> pMapKeys= pSuppMap.keySet();
		Iterator<String> keysItr = pMapKeys.iterator();
		LinkedList<String> uniquePatList = new LinkedList<String>();
		while(keysItr.hasNext())	{
			String key = keysItr.next();
			int flag=1;
			if(!(uniquePatList.isEmpty()))	{
				//flag = 
						isPatternExist(uniquePatList,key);
				//if(flag==1)	{
				//	uniquePatList.add(key);
				//}	
			}
			else	{
				uniquePatList.add(key);
			}
		}
		System.out.println("uniquePatList : "+uniquePatList);
	}
	private int isPatternExist(LinkedList<String> uniquePatList, String key) {
		ListIterator<String> patListItr = uniquePatList.listIterator();
		String[] keyTokens = key.split(" ");
		while(patListItr.hasNext())	{
			String k = patListItr.next();
			String[] kTokens = k.split(" ");
			if(keyTokens[0].equals(kTokens[0]))	{ //&& keyTokens.length > kTokens.length
				int i=1,j;
				//System.out.println("key : "+key);
				//System.out.println("k : "+k);
				for(j=1;j<kTokens.length && i<keyTokens.length;)	{
					if(keyTokens[i].equals(kTokens[j]))	{
						i++;
						j++;
					}
					else
						j++;
				}
				if(j==kTokens.length && i<keyTokens.length)	{
					patListItr.remove();
					patListItr.add(key);
					//System.out.println("k : "+k+" key :"+key);
					break;
				}
				//System.out.println("Unique Pattern List : "+uniquePatList);
			}
			//else 
			//	return 0;
		}
		return 0;
	}
	public static void main(String[] args)	{
		DummyTest dt = new DummyTest();
		Map<String,Double> pMap = new LinkedHashMap<String,Double>();
		pMap.put("F1 B1 B2 B3", 0.92);
		pMap.put("F1 B2 B3", 0.95);
		pMap.put("F1 B1 B3", 0.95);
		pMap.put("F1 B1 B2 B3 B4", 0.91);
		pMap.put("F1 B2 B4", 0.95);
		pMap.put("F1 B1 B3", 0.94);
		System.out.println("pMap : "+pMap);
		dt.updatePatternMap(pMap);
		
	}
}

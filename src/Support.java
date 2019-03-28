import java.util.LinkedList;
import java.util.List;


public class Support {

	DataNode[] dns;
	GlobalSupport gs;
	List<String> FBAP_List;// dnNum,Filename, blockNumber, SPValue.
	
	Support()	{
		FBAP_List = new LinkedList();
	}
	public DataNode[] getDns() {
		return dns;
	}

	public void setDns(DataNode[] dns) {
		this.dns = dns;
	}

	public GlobalSupport getGs() {
		return gs;
	}

	public void setGs(GlobalSupport gs) {
		this.gs = gs;
	}

	public List<String> getFBAP_List() {
		return FBAP_List;
	}

	public void setFBAP_List(List<String> fBAP_List) {
		FBAP_List = fBAP_List;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

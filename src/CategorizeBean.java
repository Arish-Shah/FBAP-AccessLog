import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class CategorizeBean {

	Set<String> cat1;
	Set<String> cat2;
	Set<String> cat3;
	List<String> fBAP_List;
	
	public CategorizeBean() {
		
		cat1 = new TreeSet<String>();
		cat2 = new TreeSet<String>();
		cat3 = new TreeSet<String>();
	}

	public Set<String> getCat1() {
		return cat1;
	}

	public void setCat1(Set<String> cat1) {
		this.cat1 = cat1;
	}

	public Set<String> getCat2() {
		return cat2;
	}

	public void setCat2(Set<String> cat2) {
		this.cat2 = cat2;
	}

	public Set<String> getCat3() {
		return cat3;
	}

	public void setCat3(Set<String> cat3) {
		this.cat3 = cat3;
	}

	public void setFBAPList(List<String> fBAP_List) {
		this.fBAP_List = fBAP_List;		
	}
	public List<String> getFBAPList() {
		return fBAP_List;		
	}
}

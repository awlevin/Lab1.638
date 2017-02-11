import java.util.ArrayList;

public class Instance {
	public ArrayList<Double> features;
	public ArrayList<Integer> labels;

	public String name;

	public Instance(String name) {
		this.name = name;
		this.features = new ArrayList<Double>();
		this.labels = new ArrayList<Integer>();
	}
	
	// assumes only 1 label in array will be hot, rest will be 0
	public int getIndexOfHotLabel() {
		for (int i = 0; i < this.labels.size(); i++) {
			if(labels.get(i) == 1) return i;
		}
		return -1;
	}
}

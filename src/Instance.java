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
}

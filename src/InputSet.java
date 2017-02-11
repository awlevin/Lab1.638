import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class InputSet {

	// Declare instance variables
	public String fileName;
	public ArrayList<Instance> instances;
	public HashMap<String, ArrayList<String>> featuresDict;
	public int numFeatures = 0;
	public ArrayList<String> labels;
	public int numLabels = 0;

	private int currLineIndex = 0;
	private String[] inFileStringArray;

	public InputSet(String fileName) {
		this.fileName = fileName;
		this.instances = getData(fileName);
	}

	private ArrayList<Instance> getData(String fileName) {
		this.inFileStringArray = createStringArrayFromFile(fileName);
		getPossibleFeatures();
		getPossibleLabels();
		return getExamples();
	}

	private String[] createStringArrayFromFile(String fileName) {
		// Store data file line by line in 'inFileStringArray'
		String[] inFileStringArray = null;

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			// Read the file and stores each line as an array entry
			inFileStringArray = stream.toArray(size -> new String[size]);

		} catch (IOException e) {
			e.printStackTrace();
		}
		assert(inFileStringArray != null);
		return inFileStringArray;
	}

	private void getPossibleFeatures() {

		this.featuresDict = new HashMap<String, ArrayList<String>>();

		// Increment line index until we find number of features comment
		while(!inFileStringArray[currLineIndex].contains("number of features")) {
			currLineIndex++;
		}
		
		// Assert current line is what we expect
		assert(inFileStringArray[currLineIndex].contains("number of features")); 

		// Get number of features
		int numFeatures = 0;
		if (inFileStringArray[currLineIndex].contains("number of features")) {
			currLineIndex++;
			numFeatures = Integer.parseInt(inFileStringArray[currLineIndex]);
			currLineIndex++;
		}

		// If numFeatures is still 0, something went wrong
		assert(numFeatures != 0);
		this.numFeatures = numFeatures;

		// Move currLineIndex to next section: '// feature names and values'
		int featuresObtained = 0;
		while(featuresObtained < numFeatures) {
			if(inFileStringArray[currLineIndex].equals("") || 
					inFileStringArray[currLineIndex].contains("//")){
				currLineIndex++;
			} else{
				this.extractFeature();
				featuresObtained++;
				currLineIndex++;
			}
		}
	}
	
	private void extractFeature() {
		// Create a dictionary of features and potential values
		String featureName = new String();
		ArrayList<String> featurePotentialValues = new ArrayList<String>();

		// Feature Format: FX - T F

		// Contents of 'temp' should be:
		// index 0: FX
		// index 1: all the potential values of that feature name
		String[] temp = inFileStringArray[currLineIndex].split(" - ");

		// Each index of 'temp2' should be a different possible feature value.
		String[] temp2 = temp[1].split(" ");

		// Iterate through all potential values of this feature
		for(int i = 0; i < temp2.length; i++) {
			// temp[0] is the feature name
			// temp[i] is each possible value of the feature
			featureName = temp[0];
			featurePotentialValues.add(temp2[i]);
		}
		this.featuresDict.put(featureName, featurePotentialValues);
	}

	private void getPossibleLabels() {
		this.labels = new ArrayList<String>();
		
		int obtainedLabels = 0;
		while(obtainedLabels < 2) {
			if(inFileStringArray[currLineIndex].contains("//") || 
					inFileStringArray[currLineIndex].equals("")) {
				currLineIndex++;
			} else {
				this.extractLabel();
				obtainedLabels++;
				currLineIndex++;
			}
		}
	}
	
	private void extractLabel() {
		labels.add(inFileStringArray[currLineIndex]);
	}

	// Reads train/tune/test file and gets the list of instances, stored as an ArrayList
	// Asumes @param fileName is the file listed in the project root directory.
	private ArrayList<Instance> getExamples() {
		int numExamples = 0;
		while(inFileStringArray[currLineIndex].contains("//") || 
					inFileStringArray[currLineIndex].equals("")) {
			
			if(inFileStringArray[currLineIndex].contains("number of examples")) {
				currLineIndex++;
				numExamples = Integer.parseInt(inFileStringArray[currLineIndex]);
				currLineIndex++;
				break;
			}else {
				currLineIndex++;
			}
		}
		
		ArrayList<Instance> examples = new ArrayList<Instance>(numExamples);
		
		int examplesObtained = 0;
		while(examplesObtained < numExamples) {
			if(inFileStringArray[currLineIndex].contains("//") ||
					inFileStringArray[currLineIndex].equals("")) {
				currLineIndex++;
			} else {
				examples.add(this.extractExample());
				examplesObtained++;
				currLineIndex++;
			}
		}
		return examples;
	}
	
	private Instance extractExample() {
		/* Contents of 'exampleContents' should be:
		 * index 0: Example name
		 * index 1: Label value
		 * index 2 through N: Feature values
		 */
		String[] exampleContents = inFileStringArray[currLineIndex].replaceAll("\\s+", " ").split(" ");
		
		
		// now we have to go through and remove array spaces that are empty
		
		String exampleName = exampleContents[0];
		String labelValue = exampleContents[1];
		
		Instance newInstance = new Instance(exampleName);
		
		// Set Instance's Feature Values
		for(int j = 2; j < exampleContents.length; j++) {
			String featureValue = exampleContents[j];
			double valueToAdd = (featureValue.charAt(0) == 'T') ? 1 : 0;
			newInstance.features.add(j-2, valueToAdd);
		}
		
		numLabels = 2;
		String label1 = this.labels.get(0);
		String label2 = this.labels.get(1);
		
		// Set Instance's Label/Classification Values
		if(labelValue.equals(label1)){
			newInstance.labels.add(0, 1);
			newInstance.labels.add(1, 0);
		} else if(labelValue.equals(label2)) {
			newInstance.labels.add(0, 0);
			newInstance.labels.add(1, 1);
		} else {
			System.out.println("ERROR GETTING LABELS IN INPUTSET");
			System.exit(-1);
		}
		
		return newInstance;
	}
}

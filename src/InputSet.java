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
	public ArrayList<String> labels;

	private int currLineIndex = 0;
	private String[] inFileStringArray;

	public InputSet(String fileName) {
		this.fileName = fileName;
		this.instances = getData(fileName);
	}

	private ArrayList<Instance> getData(String fileName) {
		this.inFileStringArray = createStringArrayFromFile(fileName);
		this.featuresDict = getPossibleFeatures();
		this.labels = getPossibleLabels();
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

	private HashMap<String,ArrayList<String>> getPossibleFeatures() {

		//================================================================================
		// This is the '// number of features' section
		//================================================================================

		// Assert first line of file is what we expect
		assert(currLineIndex == 0);
		assert(inFileStringArray[currLineIndex].contains("// number of features")); 

		// Get number of features
		int numFeatures = 0;
		if (inFileStringArray[currLineIndex].contains("// number of features")) {
			currLineIndex++;
			numFeatures = Integer.parseInt(inFileStringArray[currLineIndex]);
		}

		// If numFeatures is still 0, something went wrong
		assert(numFeatures != 0);

		// Move currLineIndex to next section: '// feature names and values'
		currLineIndex += 2;
		assert(currLineIndex == 3);

		//================================================================================
		// This is the '// feature names and values' section
		//================================================================================

		// Assert currentLineIndex file contents are what we expect.
		assert(inFileStringArray[currLineIndex].contains("// feature names and values"));

		// Create a dictionary of features and potential values
		HashMap<String, ArrayList<String>> featureDict = new HashMap<String, ArrayList<String>>(numFeatures);
		String[] featureNames = new String[numFeatures];
		ArrayList<String> featurePotentialValues;

		int featureNumIndex = 0;

		assert(inFileStringArray[currLineIndex].contains("// feature names and values"));

		// Start by incrementing currLineIndex (so it starts on the first feature)
		// Parse until reaching a line that doesn't start with a feature name
		for(currLineIndex++; !inFileStringArray[currLineIndex].equals(""); currLineIndex++ ) {

			featurePotentialValues = new ArrayList<String>();

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
				featureNames[featureNumIndex] = temp[0];
				featurePotentialValues.add(temp2[i]);
			}
			featureDict.put(featureNames[featureNumIndex], featurePotentialValues);
			featureNumIndex++;
		}
		assert(inFileStringArray[currLineIndex].equals(""));
		this.currLineIndex++; // now points to header of next section

		return featureDict;
	}

	private ArrayList<String> getPossibleLabels() {

		assert(inFileStringArray[currLineIndex].contains("// labels"));

		ArrayList<String> labels = new ArrayList<String>();

		// first increment 'currLineIndex' so it points to first label
		// condition: line is not empty (end of section indicator)
		for(currLineIndex++; !inFileStringArray[currLineIndex].equals(""); currLineIndex++) {
			labels.add(inFileStringArray[currLineIndex]);
		}
		
		currLineIndex++; // now points to start of next section
		return labels;
	}

	// Reads train/tune/test file and gets the list of instances, stored as an ArrayList
	// Asumes @param fileName is the file listed in the project root directory.
	private ArrayList<Instance> getExamples() {
		assert(inFileStringArray[currLineIndex].contains("// number of examples"));
		
		int numExamples = Integer.parseInt(inFileStringArray[++currLineIndex]);
		ArrayList<Instance> examples = new ArrayList<Instance>(numExamples);
		
		currLineIndex += 2;
		assert(inFileStringArray[currLineIndex].contains("// examples"));
		currLineIndex++; // now points to first example
		
		// Iterate through all examples
		for(int i = 0; i < numExamples; i++) {
			
			/* Contents of 'exampleContents' should be:
			 * index 0: Example name
			 * index 1: Label value
			 * index 2 through N: Feature values
			 */
			String[] exampleContents = inFileStringArray[currLineIndex].split(" ");
			
			String exampleName = exampleContents[0];
			String labelValue = exampleContents[1];
			
			Instance newInstance = new Instance(exampleName);
			
			// Set Instance's Feature Values
			for(int j = 2; j < exampleContents.length; j++) {
				String featureValue = exampleContents[j];
				double valueToAdd = (featureValue.charAt(0) == 'T') ? 1 : 0;
				newInstance.features.add(j-2, valueToAdd);
			}
			
			// Set Instance's Label/Classification Values
			switch (labelValue) {
			case "pos":
				newInstance.labels.add(0, 1);
				newInstance.labels.add(1, 0);
				break;
			case "neg":
				newInstance.labels.add(0, 0);
				newInstance.labels.add(1, 1);
				break;
			}
			
			examples.add(newInstance);
			
			currLineIndex++;
		}

		assert(inFileStringArray[currLineIndex].equals(""));

		return examples;
	}
}

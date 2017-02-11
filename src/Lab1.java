import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Lab1 {
	protected final static boolean debugFlag = true;

	static int numHiddenNodes;
	static double learningRate = .25;
	static int maxEpoch = 1;

	static int numLabels;
	static int numFeatures;

	public static void main(String[] args) {

		// Check correct number of arguments
		if (args.length < 3)  {
			System.out.println("usage: java Lab1 <fileNameOfTrain> " +
					"<fileNameOfTune> <fileNameOfTest>");
			System.exit(-1);
		}

		//Reading the training set 	
		InputSet trainingSet = new InputSet(args[0]);
		InputSet tuningSet = new InputSet(args[1]);
		InputSet testingSet = new InputSet(args[2]);

		assert(inputSetsAreValid(trainingSet, tuningSet, testingSet));
		numLabels = trainingSet.numLabels;
		numFeatures = trainingSet.numFeatures;

		// Set Random Weights
		Double[][] weights = new Double[numLabels][];

		for(int i = 0; i < weights.length; i++) {
			weights[i] = new Double[numFeatures+1];
		}

		setRandomWeights(weights);

		NeuralNet neuralNet = new NeuralNet(trainingSet, learningRate, maxEpoch, weights);

		neuralNet.train();

		//Reading the testing set 	
		ArrayList<Instance> testSet = testingSet.instances;

		Integer[] outputs = new Integer[testSet.size()];

		int correct=0;
		for(int i = 0; i < testSet.size(); i++) {

			// Get output from network
			outputs[i] = neuralNet.calculateOutputForInstance(testSet.get(i));
			int actualLabelIndex = -1;
			for (int j = 0; j < testSet.get(i).labels.size(); j++) {
				if (testSet.get(i).labels.get(j) == 1)
					actualLabelIndex = j;
			}
			
			if(outputs[i] == actualLabelIndex) {
				correct++;
			} else {
				if(debugFlag) System.out.println(trainingSet.instances.get(i).name + " was misclassified, expected: "
						+ trainingSet.labels.get(actualLabelIndex) + ". But actual:" + trainingSet.labels.get(outputs[i]));
			}
			
			if(!debugFlag) System.out.println(testingSet.labels.get(outputs[i]));
			
		}

		if(debugFlag) System.out.println("Total instances: " + testSet.size());
		if(debugFlag) System.out.println("Correctly classified: " + correct);
		if(debugFlag) System.out.println("Percentage correct: " + (double)correct/testSet.size()*100 + "%");

	}

	private static boolean inputSetsAreValid(InputSet train, InputSet tune, InputSet test) {
		boolean inputsAreValid = false; // initial state

		if(train.featuresDict.equals(tune.featuresDict) && train.featuresDict.equals(test.featuresDict) 
				&& tune.featuresDict.equals(test.featuresDict) && train.labels.equals(tune.labels) 
				&& train.labels.equals(test.labels)) {
			inputsAreValid = true;
		}

		return inputsAreValid;
	}

	public static void setRandomWeights(Double[][] weights) {
		Random r = new Random();

		for(int i = 0; i < weights.length; i++) {

			for(int j = 0; j < weights[i].length; j++) {
				weights[i][j] = r.nextDouble()*0.01;
			}
		}
	}
}

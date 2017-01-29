import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

public class Lab1 {

	public static void main(String[] args) {

		String trainFile = null, tuneFile = null, testFile = null;
		InputSet trainingSet, tuningSet, testingSet;
		ArrayList<Instance> trainingInstances, tuningInstances, testingInstances;

		// Set variables for each file name/path
		if (args.length < 3) {
			System.out.println("usage: java Lab1 <trainFile> <tuneFile> <testFile>");
			System.exit(-1);
		} else {
			trainFile = args[0];
			tuneFile = args[1];
			testFile = args[2];
		}

		// Read in the training set
		trainingSet = new InputSet(trainFile);
		tuningSet = new InputSet(tuneFile);
		testingSet = new InputSet(testFile);
		
		assert(inputSetsAreValid(trainingSet, tuningSet, testingSet));
		
		trainingInstances = trainingSet.instances;
		tuningInstances = tuningSet.instances;
		testingInstances = testingSet.instances;
		
		
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
}

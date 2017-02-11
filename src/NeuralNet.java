import java.util.*;

public class NeuralNet{
	public ArrayList<Node> inputNodes = null;
	public ArrayList<Node> outputNodes = null;
	
	public ArrayList<Instance> trainingInstances = null;
	
	Double learningRate;
	int maxEpoch;
	
	// The last node of the input layer will be a bias node
	public NeuralNet(InputSet trainingSet, double learningRate, int maxEpoch, Double[][] weights) {
		this.trainingInstances = trainingSet.instances;
		this.learningRate = learningRate;
		this.maxEpoch = maxEpoch;
		
		// Input layer nodes
		inputNodes = new ArrayList<Node>();
		int inputNodeCount = trainingSet.numFeatures;
		int outputNodeCount = trainingSet.numLabels;
		
		for(int i = 0; i < inputNodeCount; i++) {
			Node node = new Node(NodeType.Input);
			inputNodes.add(node);
		}
		
		// Bias node from input layer to output layer
		Node biasToOutput = new Node(NodeType.BiasToOutput);
		inputNodes.add(biasToOutput);
			
		// Output layer nodes
		outputNodes = new ArrayList<Node> ();
		for(int i = 0; i < outputNodeCount; i++) {
			Node node = new Node(NodeType.Output);
			
			//Connecting output layer nodes to input layer nodes
			for(int j = 0; j < inputNodes.size(); j++) {
				NodeWeightPair nodePair=new NodeWeightPair(inputNodes.get(j), weights[i][j]);
				node.parents.add(nodePair);
			}	
			outputNodes.add(node);
		}	
	}
	
	/**
	 * Get the output from the neural net for a single instance
	 * Return the idx with highest output values. For example if the outputs
	 * of the outputNodes are [0.1, 0.5, 0.2], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.5], it should return 2. 
	 * The parameter is a single instance. 
	 */
	public int calculateOutputForInstance(Instance inst) {
		
		// Initialize all inputNode input values (from instance features)
		for(int i = 0; i < inst.features.size(); i++) {
			Node inputNode = this.inputNodes.get(i);
			inputNode.setInput(inst.features.get(i));
		}
		
		// Calculate outputs at each output node
		for(Node outputNode : this.outputNodes) {
			outputNode.calculateOutput();
		}
		
		// Find index of max valued output node
		int indexOfMaxValue = 0; // initial state
		double valueOfBestIndex = Double.MIN_VALUE; // Initialize to negative infinity
		for(int i = 0; i < this.outputNodes.size(); i++) {
			
			Node currentNode = this.outputNodes.get(i);
			
			// Check if current node's output is greater or equal to current best
			if(currentNode.getOutput() >= valueOfBestIndex) {
				indexOfMaxValue = i;
				valueOfBestIndex = currentNode.getOutput();
			}
		}
		return indexOfMaxValue;
	}
	
	public void train() {
		int currentEpoch = 0;
		while(currentEpoch <= this.maxEpoch) {
			
			for(Instance example : this.trainingInstances) {
				
				// Forward propagate (calculate all node outputs in network)
				this.calculateOutputForInstance(example);
				
				ArrayList<Double> errorVector = new ArrayList<Double>();
				
				// Calculate error vector for each output node versus expected output at each node
				for(int i = 0; i < example.labels.size(); i++) {
					errorVector.add(i, example.labels.get(i) - this.outputNodes.get(i).getOutput());
				}
				
				// Declare 2D array to store input to output node ∆W
				double[][] inputToOutputWeights = new double[this.inputNodes.size()][this.outputNodes.size()];
				
				// compute WJK
				// iterate through output nodes
				for(int k = 0; k < this.outputNodes.size(); k++) {
					Node outputNode = this.outputNodes.get(k);
										
					// iterate through input nodes
					for(int j = 0; j < outputNode.parents.size(); j++) {
						NodeWeightPair inOutNodePair = outputNode.parents.get(j);
						Node inputNode = inOutNodePair.node;
						
						double deltaWJK = this.learningRate * inputNode.getOutput() * errorVector.get(k)
								* this.derivativeOfSigmoid(outputNode.getOutput());
						
						inputToOutputWeights[j][k] = deltaWJK;
					}
				}
				
				// update input to output node weights...
				for(int k = 0; k < this.outputNodes.size(); k++) {
					Node outputNode = this.outputNodes.get(k);
					
					for(int j = 0; j < outputNode.parents.size(); j++) {
						NodeWeightPair inOutNodePair = outputNode.parents.get(j);
						inOutNodePair.weight += inputToOutputWeights[j][k];
					}
				}
			}
			currentEpoch++;
		}
	}
	
	public double derivativeOfSigmoid(double input) {
		return (input) * (1 - input);
	}
	
}

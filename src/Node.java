/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details
 * 
 * Do not modify. 
 */


import java.util.*;

enum NodeType {
	Input, BiasToOutput, Output;
}

public class Node{
	private NodeType type = NodeType.Input; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
	public ArrayList<NodeWeightPair> parents = null; //Array List that will contain the parents (including the bias node) with weights if applicable
		 
	private Double inputValue = 0.0;
	private Double outputValue = 0.0;
	private Double sum = 0.0; // sum of wi*xi
	
	public Node(NodeType type) {
		this.type = type;
		
		if (type.equals(NodeType.Output)) {
			parents = new ArrayList<NodeWeightPair>();
		}
	}
	
	// For an input node: set input value which will be the value of a specific feature
	public void setInput(Double inputValue) {
		if(type.equals(NodeType.Input)) {
			this.inputValue=inputValue;
		}
	}
	
	/**
	 * Calculates the output of a ReLU node.
	 * Assumes parent outputs have already been calculated
	 */
	public void calculateOutput() {
		
		if(type.equals(NodeType.Output)) {
			sum = 0.0;
			NodeWeightPair parentNodeWeightPair = null;
			
			// Calculate the sum of (inputValues * weights)
			for(int i = 0; i < this.parents.size(); i++) {
				parentNodeWeightPair = this.parents.get(i);
				double parentWeight = parentNodeWeightPair.weight;
				double outputOfParent = parentNodeWeightPair.node.getOutput();
				
//				System.out.println("parentWeight: " + parentWeight + "\tparentOutput: " + outputOfParent); 
				
				this.sum += outputOfParent * parentWeight;
			}
			
			// Set outputValue to g(x) 'activation function' (here we use Sigmoid)
			this.outputValue = 1/(1 + Math.exp(-sum));
		}
	}

	public double getSum() {
		return sum;
	}
	
	//Gets the output value
	public double getOutput() {
		
		if(type.equals(NodeType.Input)) {
			return inputValue;
		}
		else if(type.equals(NodeType.BiasToOutput)) {
			return 1.00;
		}
		else {
			return outputValue;
		}
	}
}



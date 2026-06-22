package AUSC;

import java.util.HashSet;
import java.util.Set;

public class SamplingNode {
	int id;
	double weight;

	SamplingNode leftChild;
	SamplingNode rightChild;
	SamplingNode parent;
	Set<Integer> hashvertices;

	SamplingNode() {
		id = -1;
		weight = -1;
		leftChild = rightChild = parent = null;
	}

	SamplingNode(int nodeId, double nodeWeight) {
		this.id = nodeId;
		this.weight = nodeWeight;
		this.leftChild = null;
		this.rightChild = null;
		this.parent = null;
		this.hashvertices = new HashSet<Integer>();
	}
	SamplingNode(int nodeId, double nodeWeight, Set<Integer> hashvertices) {
		this.id = nodeId;
		this.weight = nodeWeight;
		this.leftChild = null;
		this.rightChild = null;
		this.parent = null;
		this.hashvertices =hashvertices;
	}

}

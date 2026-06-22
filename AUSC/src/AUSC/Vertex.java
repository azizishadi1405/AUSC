package AUSC;

import java.util.HashSet;
import java.util.Set;

public class Vertex {
	private int id;
	//Neighbor adjacencyListHead;
	//Neighbor adjacencyListTail;
	int degree,sketchDepth,sketchWidth;
	int hash,visited=0;
	Set<Integer> otherhashes;
	double S_x;
	double e_xi;
	double e_x;
	double n_x;
	double sqrt_n_x;
	private Set<Integer> setOfVertexIds;
	double weight;
	double nodeIS;
	double[][] countMinBucket;
	double[][] countMinBucketADJ;
	double[][] countMinBucketUtility;
	double[][] countMinBucketNNeigh;
	int[] attribute_v;
	double n_neis;
	double maxnodeIsneigh;
        //double [] attrValDist;
	SamplingNode nodeRef;
	int selectedpair;


	double UD_x;
	double U_xi;
	double U_x;

	Vertex(int id) {
		this.sketchDepth= AUSC.sketchDepth;
		this.sketchWidth= AUSC.sketchWidth;
		countMinBucket = new double[sketchDepth][sketchWidth];
		countMinBucketNNeigh = new double[sketchDepth][sketchWidth];
		countMinBucketADJ = new double[sketchDepth][sketchWidth];
		countMinBucketUtility = new double[sketchDepth][sketchWidth];
		this.id = id;
		degree = 0;
		this.e_x = 0;
		this.n_x = 1;
		this.sqrt_n_x = 1;
		setOfVertexIds = new HashSet<Integer>();
		setOfVertexIds.add(id);
		this.n_neis=0;
		this.maxnodeIsneigh=0.0;
		this.otherhashes= new HashSet<Integer>();
		this.visited=0;

	}

	Vertex(int id, double nodeIS) {
		this( id, nodeIS,1);
		/*
		this.sketchDepth=Efficient_score.sketchDepth;
		this.sketchWidth=Efficient_score.sketchWidth;
		countMinBucket = new double[sketchDepth][sketchWidth];
		countMinBucketNNeigh = new double[sketchDepth][sketchWidth];
		countMinBucketADJ = new double[sketchDepth][sketchWidth];
		countMinBucketUtility = new double[sketchDepth][sketchWidth];
		this.id = id;
		degree = 0;
		this.e_x = 0;
		this.n_x = 1;
		this.sqrt_n_x = 1;
		this.nodeIS = nodeIS;
		this.maxnodeIsneigh=0.0;
		this.hash=0;
		this.otherhashes= new HashSet<Integer>();
		setOfVertexIds = new HashSet<Integer>();
		setOfVertexIds.add(id);
		U_x=nodeIS;
		U_xi=0;

		 */

	}

	Vertex(int id, double nodeIS, int attr_len) {
		this.sketchDepth= AUSC.sketchDepth;
		this.sketchWidth= AUSC.sketchWidth;
		countMinBucket = new double[sketchDepth][sketchWidth];
		countMinBucketNNeigh = new double[sketchDepth][sketchWidth];
		countMinBucketADJ = new double[sketchDepth][sketchWidth];
		countMinBucketUtility = new double[sketchDepth][sketchWidth];
		this.id = id;
		degree = 0;
		this.e_x = 0;
		this.n_x = 1;
		this.sqrt_n_x = 1;
		this.nodeIS = nodeIS;
		this.maxnodeIsneigh=0.0;
		this.hash=0;
		this.otherhashes= new HashSet<Integer>();
		setOfVertexIds = new HashSet<Integer>();
		setOfVertexIds.add(id);
		U_x=nodeIS;
		U_xi=0;
        this.attribute_v=new int[attr_len];
		this.visited=0;
	}

	public Set<Integer> getVertexIds() {
		return setOfVertexIds;
	}

	public void setAttribute_v(int [] attr){
		this.attribute_v=attr;
	}
	public int[] getAttribute_v(){
		return this.attribute_v;
	}
	public void setInternalVertices(Set<Integer> vertices) {
		this.setOfVertexIds = new HashSet<Integer>(vertices);
		this.n_x = setOfVertexIds.size();
		this.sqrt_n_x = Math.sqrt(setOfVertexIds.size());

	}

	public double getMergedEdgeCount() {
		return e_x;
	}

	public void setMergedEdgeCount(double count) {
		this.e_x = count;
	}

	public double vertexCount() {
		return setOfVertexIds.size();
		// return internalVertexCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void fillsketches(int neighid, double neighnodeIS){
		this.U_xi=this.U_xi+neighnodeIS;
		for (int i = 0; i < AUSC.sketchDepth; i++) {
			// System.out.println(this.id + " neighbor: "+ n.id);
			int index = AUSC.sketch.hash((neighid), i);
			// System.out.println("Id :"+n.id+"  "+"index for HashFunction: " +i
			// +" Hashed at: "+ index);

			// buckets[i][index] = buckets[i][index].add(new BigDecimal(1));
			countMinBucket[i][index] = countMinBucket[i][index] + 1;
			countMinBucketADJ[i][index] =countMinBucketADJ[i][index] +1;
			countMinBucketNNeigh[i][index] = countMinBucketNNeigh[i][index] + 1;
			countMinBucketUtility[i][index] = countMinBucketUtility[i][index] + neighnodeIS;
		}
	}

	/*
	public Neighbor getNeighbourHead() {

		return adjacencyListHead;
	}

	public void addAtEnd(Neighbor n) {
		if (degree == 0) {
			adjacencyListHead = n;
			adjacencyListTail = n;
			degree++;

		} else {
			adjacencyListTail.next = n;
			n.previous = adjacencyListTail;
			adjacencyListTail = n;
			degree++;
		}
		// if(n.next==null)
		// adjTail = n;

	}

	public void addNewNodeAtEnd(Neighbor n) {
		if (degree == 0) {
			adjacencyListHead = n;
			adjacencyListTail = n;
			degree++;

		} else {
			adjacencyListTail.next = n;
			n.previous = adjacencyListTail;
			adjacencyListTail = n;
			degree++;
		}
		// if(n.next==null)
		// adjTail = n;

		S_x = degree;
		e_xi = degree;
		weight = -1 * degree;

		for (int i = 0; i < Efficient_score.sketchDepth; i++) {
			// System.out.println(this.id + " neighbor: "+ n.id);
			int index = Efficient_score.sketch.hash((n.id), i);
			// System.out.println("Id :"+n.id+"  "+"index for HashFunction: " +i
			// +" Hashed at: "+ index);

			// buckets[i][index] = buckets[i][index].add(new BigDecimal(1));
			countMinBucket[i][index] = countMinBucket[i][index] + 1;
			countMinBucketADJ[i][index] =countMinBucketADJ[i][index] +1;
			countMinBucketNNeigh[i][index] = countMinBucketNNeigh[i][index] + n.n_x;
			countMinBucketUtility[i][index] = countMinBucketUtility[i][index] + n.nodeIS;
		}
	}

	public void remove(Neighbor n) {
		if (degree == 0)
			return;
		if (degree == 1) {
			// n= null;
			adjacencyListTail = null;
			adjacencyListHead = null;
		}

		else if (n.previous == null) {
			adjacencyListHead = n.next;
			adjacencyListHead.previous = null;
		}

		else if (n.next == null) {
			adjacencyListTail = n.previous;
			adjacencyListTail.next = null;
		} else {
			Neighbor previous = n.previous;
			Neighbor next = n.next;

			previous.next = n.next;
			next.previous = n.previous;
		}
		n = null;
		degree--;
	}
	*/

	public boolean isEmpty() {
		return degree == 0;
	}

	public int size() {
		return degree;
	}
        /*
        public void print()
        {
            System.out.println("Id : "+id+", S_x:"+S_x+", e_xi : "+e_xi+", e_x"+e_x);
        }*/
        
        
        
        
        

}
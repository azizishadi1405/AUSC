package AUSC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
//import org.apache.commons.math3.util.MathArrays;



// Aim to remove the supernode object. name can be obtained from unionFind(), size can be
// obtained from unionFind() and the superEdges need to be thinked about. Nodes are not required.
// As a start point, the superEdges can be stored as an adjacency list using array.
// Append the node name in the double value, it would save some space
public class SuperNode{

    // name of the supernode (between 0 and n-1). Actually the root node in the supernode's tree representation
    private int name;
    // number of nodes in the supernode
    private int  size;

    // Aiming to convert this hashmap to just an array. That array will just contain seCost()-nseCost()
    // number and thus significant reduction in space.


    // keys are the names of the superNeighbours and the values are the superedges linked to them
    // As a starting point, let's use the standard adjacency list approach.
    private HashMap<Integer, SuperEdge> superNeighbors;
    // list of all the nodes in the supernode
    private List<Integer> nodes;

    //khodam
    public SuperNode(int node) {
        this.superNeighbors=new HashMap<Integer,SuperEdge>();
        this.name=node;
        this.size=1;
        this.nodes=new LinkedList<Integer>();
        this.nodes.add(node);
        // Node node = new Node(nodeName);
        // this.first=node;
        // this.last=node;
        //this.superNeighbors=new HashMap<Integer,Useful_Summary.SuperEdge>();
    }


    public int getName(){
        return this.name;
    }

    public int getSize(){
        return this.size;
    }

    public HashMap<Integer,SuperEdge> getSuperNeighbors(){
        return this.superNeighbors;
    }

    public List<Integer> getNodes(){
        return this.nodes;
    }

    public void setName(int name){
        this.name=name;
    }

    public void setSize(int size){
        this.size=size;
    }

    public void setNodes(List<Integer> nodes){
        this.nodes=nodes;
    }

    public void setSuperNeighbors(HashMap<Integer,SuperEdge> superNeighbors){
        this.superNeighbors=superNeighbors;
    }

    public SuperNode(){
        this.superNeighbors=new HashMap<Integer,SuperEdge>();
    }

    //khodam
    public void merge(SuperNode superNode) {
        this.size+=superNode.size;
        this.nodes.addAll(superNode.getNodes());
        this.superNeighbors.putAll(superNode.getSuperNeighbors());
    }
    public void addnodes(String[] newnodes) {
        this.size+=newnodes.length;
        for (String newnode : newnodes){
            this.nodes.add(Integer.parseInt(newnode));
        }
    }
    public void removefirstnode() {
        this.size-=1;
        this.nodes.remove(0);

    }

    //khodam oon parantezam hast
    public class Node{
        int name;
        Node next;
        Node(int name){
            this.name=name;
            this.next=null;
        }
    }
}

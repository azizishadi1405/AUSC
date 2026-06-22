package AUSC;

import java.util.List;
import java.lang.Cloneable;

public class UnionFind implements Cloneable{

    // id[i] contains the parent of the ith node
    private int[] id;
    // size[i] contains the size of the subtree rooted at ith node
    private int[] size;

    // newName given to each supernode
    private int[] tempName;

    private int[] originalName;

    private int numSuperNodes;


    private SuperNode[] superNode;
    // nodes are numbered from 0 to n-1
    // each supernode is a tree and the root of that tree is the name of the
    // supernode

    public SuperNode[] getSuperNodes(){
        return superNode;
    }

    public UnionFind(int n) {
        id = new int[n];
        size = new int[n];
        tempName=new int[n];
        originalName=new int[n];
        superNode=new SuperNode[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
            size[i] = 1;
            superNode[i] = new SuperNode(i);
            //System.out.println("id[i] "+id[i] );
            //Khodam
            originalName[i] = i;
            //System.out.println(" unionfind originalName[i] "+originalName[i] );
        }
    }

    //find(i) will return the supernode containing node i
    public int find(int i) {
        while (i != id[i]) {
            // path compression
            id[i] = id[id[i]];
            i = id[i];
           // System.out.println("find id[i] "+id[i] );
        }
        //System.out.println("find return "+i);
        return i;
    }

    public void reset(){
        for (int i = 0; i < id.length; i++) {
            id[i] = i;
            size[i] = 1;
            superNode[i] = new SuperNode(i);

        }
    }
    //khodam
    public List<Integer> getnodesofsupernode(SuperNode s){
        return s.getNodes();
    }
    public void addnodestosupernode(SuperNode s,String[] newnodes){
         s.addnodes(newnodes);
    }
    public void removefirstnodeofsupernode(SuperNode s){
         s.removefirstnode();
    }
    public int renameSuperNodes(){
        int count=0;
        for(int i=0;i<id.length;i++){
            if(id[i]==i){
                //Value i is the name of the supernode
                tempName[i]=count;
                originalName[count]=i;
                //System.out.println("originalName[count]"+originalName[count]);
                //System.out.println("count"+count);
                //System.out.println("rename i"+i);
                count++;
            }
        }
        return count;
    }

    public int getOriginalName(int i){
       // System.out.println("originalName["+i+"]"+originalName[i]);
        return originalName[i];
    }

    public int getTempName(int i){
        return tempName[i];
    }

    public int getSize(int i){
        return size[i];
    }


    public boolean connected(int u,int v){
        if(find(u)==find(v))
            return true;
        return false;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // union (p,q) would merge the supernode containing node p and the supernode
    // containing node q
    public void union(int p, int q) {
        // i is the supernode containing node p
        int i = find(p);
        //System.out.println("find("+p+")"+i);
        // j is the supernode containing node q
        int j = find(q);
        //System.out.println("find("+q+")"+j);

        // if p and q are in the same supernode then don't do anything
        if (i == j)
            return;

        numSuperNodes--;

        if (size[i] < size[j]) {
            id[i] = j;
            size[j] += size[i];
            superNode[j].merge(superNode[i]);
            superNode[i]=null;
        } else {
            id[j] = i;
            size[i] += size[j];
            superNode[i].merge(superNode[j]);
            superNode[j]=null;
        }
        //System.out.println("union id["+i+"] "+id[i] );
        //System.out.println("union id["+j+"] "+id[j] );
    }
}


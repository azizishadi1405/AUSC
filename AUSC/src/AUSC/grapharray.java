package AUSC;

import java.io.*;
import java.util.*;

public class grapharray {
    int adjVertices[],allVertices[][],nodes[],supernodesweights[];
    grapharray G2hop;
    private static UnionFind uf;
    public TreeMap<Integer, Integer> nodedegree;
    int maxid=0,numedges=0;
    static SuperNode[] superNodes ;
    List<int[]> nodeVectors;
    HashMap<Integer, int[]> nodeVectorsarr;
    List<Map<Integer, String>> inverseCategoryMaps;
    int attr_len=1;
    HashMap<Integer,Vertex> vertexes;
    HashMap<Integer, Set<Integer>> Vertexhashes;
    public grapharray(){

    }



    public grapharray readgraph(String GRAPH_BASENAME) throws Exception {
        Scanner scannernumedges = new Scanner(new File(GRAPH_BASENAME));
       // List<Integer> list1 = new ArrayList();
       // List<Integer> list2 = new ArrayList();
        Scanner scanner = new Scanner(new File(GRAPH_BASENAME));
        nodedegree = new TreeMap<Integer, Integer>();
        //Scanner scanner = new Scanner(new File("6.txt"));
        //System.out.println("1111");
        int numline1=0,numline2=0;
        List<Integer> nodeslist = new ArrayList<Integer>();
        while (scanner.hasNextLine()) {
            //System.out.println("22222");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] pair;
            if (line.indexOf('\t') >= 0) {
                pair = line.split("\t", -1);
            } else {
                // در غیر این صورت با فضاهای سفید جدا کن
                pair = line.split("\\s+", -1);
            }
            //String[] pair = scanner.nextLine().split("\t");
            if (pair[0].contains("#")|| pair[0].contains("*")) continue;
            int node1id = Integer.parseInt(pair[0]);
            int node2id = Integer.parseInt(pair[1]);
            if (node1id > maxid) maxid = node1id;
            if (node2id > maxid) maxid = node2id;
            numline1++;
            if (node1id != node2id) {
                //list1.add(node1id);
                //list2.add(node2id);
                //if (0!=1){
                numedges += 1;
                //if (node1id == 101383 || node2id == 101383) {
                 //   System.out.println("numline1" + numline1);
                 //   System.out.println("node1id" + node1id);
                  //  System.out.println("node2id" + node2id);
               // }
                // System.out.println("node1id"+node1id);
                // System.out.println("node2id"+node2id);
                if (nodedegree.containsKey(node1id)) {
                    nodedegree.put(node1id, nodedegree.get(node1id) + 1);
                    //if (node1id == 101383) {
                     //   System.out.println("node2id" + node2id);
                     //   System.out.println("nodedegree.get(node1id)" + nodedegree.get(node1id));
                   // }
                } else {
                    nodedegree.put(node1id, 1);
                    nodeslist.add(node1id);
                    //if (node1id == 101383) {
                     //   System.out.println("node2id" + node2id);
                     //   System.out.println("nodedegree.get(node1id)" + nodedegree.get(node1id));
                   // }
                }
                if (nodedegree.containsKey(node2id)) {
                    nodedegree.put(node2id, nodedegree.get(node2id) + 1);
                    //if (node2id == 101383) {
                    //    System.out.println("node1id" + node1id);
                     //   System.out.println("nodedegree.get(node2id)" + nodedegree.get(node2id));
                    //}
                } else {
                    nodedegree.put(node2id, 1);
                    //if (node2id == 101383) {
                     //   System.out.println("node1id" + node1id);
                     //  System.out.println("nodedegree.get(node2id)" + nodedegree.get(node2id));
                   // }
                    nodeslist.add(node2id);
                }

                //System.out.println("doing add edge");
            }
        }scanner.reset();
        scanner.close();
        System.out.println("maxid" + maxid);
        allVertices = new int[maxid + 1][3];
        for (int[] row : allVertices)
            Arrays.fill(row, -1);
        //Arrays.fill(allVertices, -1);
        System.out.println("fill");
        adjVertices = new int[2 * numedges];
        //System.out.println("nodeslist.size()" + nodeslist.size());
        nodes = new int[nodeslist.size()];
        int i = 0;
        for (Integer e : nodeslist) {
            nodes[i++] = e;
            //System.out.println("e"+e);
            // System.out.println("i"+i);
        }
        nodeslist.clear();
        Arrays.sort(nodes);
        // for (Integer e : nodes) {
        //    System.out.println("e"+e);

        // }

        //Scanner scanner = new Scanner(new File("6.txt"));
       // System.out.println("1111");
        int nl = nodes.length;
        int nowindex = 0;
        for (int j = 0; j < nl; j++) {
            allVertices[nodes[j]][0] = nowindex;
            allVertices[nodes[j]][2] = nowindex;
            nowindex += nodedegree.get(nodes[j]);
            allVertices[nodes[j]][1] = nowindex;
           // if (nodes[j] == 101383) {
           //     // if (1==1){
             //   System.out.println("nodes[" + j + "]" + nodes[j]);
            //    System.out.println("allVertices[nodes[j]][0]" + allVertices[nodes[j]][0]);
            //    System.out.println("allVertices[nodes[j]][1]" + allVertices[nodes[j]][1]);
             //   System.out.println("allVertices[nodes[j]][2]" + allVertices[nodes[j]][2]);

          //  }

        }

        Scanner scanner2 = new Scanner(new File(GRAPH_BASENAME));
        while (scanner2.hasNextLine()) {
          //  System.out.println("22222");
            // int lnum=0;
             // for (int l :list1){
            String line = scanner2.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] pair;
            if (line.indexOf('\t') >= 0) {
                pair = line.split("\t", -1);
            } else {
                // در غیر این صورت با فضاهای سفید جدا کن
                pair = line.split("\\s+", -1);
            }
            //String[] pair = scanner.nextLine().split("\t");
            if (pair[0].contains("#")|| pair[0].contains("*")) continue;
            //String[] pair = scanner2.nextLine().split("\t");
            //String[] pair = l;
            //if (pair[0].contains("#")) continue;
            int node1id = Integer.parseInt(pair[0]);
            int node2id = Integer.parseInt(pair[1]);
           // int node1id = l;
           // int node2id = list2.get(lnum);
          //  lnum++;
            if (node1id > maxid) maxid = node1id;
            if (node2id > maxid) maxid = node2id;
            numline2++;
            if (node1id!=node2id){
/*
            if (node1id == 101383 || node2id == 101383) {
                System.out.println("numline2" + numline2);
                System.out.println("node1id" + node1id);
                System.out.println("node2id" + node2id);
                System.out.println(" allVertices[node1id][2]" + allVertices[node1id][2]);
                System.out.println("allVertices[node2id][2]" + allVertices[node2id][2]);
            }

 */
            adjVertices[allVertices[node1id][2]] = node2id;
            adjVertices[allVertices[node2id][2]] = node1id;
            /*
            if (node1id == 101383 || node2id == 101383) {
                System.out.println("adjVertices[allVertices[node1id][2]]" + adjVertices[allVertices[node1id][2]]);
                System.out.println("adjVertices[allVertices[node2id][2]]" + adjVertices[allVertices[node2id][2]]);
            }

             */
            allVertices[node1id][2] += 1;
            allVertices[node2id][2] += 1;
        }
    }
      //  scanner2.close();

        //System.out.println("numline1" + numline1);
       // System.out.println("numline2" + numline2);
        //nodedegree.clear();

        return this;
    }
    public HashMap<Integer,Vertex> fillvertexes(HashMap<Integer, Double>  nodeIS){
      vertexes=new HashMap<Integer,Vertex>() ;
        Vertexhashes=new HashMap<Integer, Set<Integer>>();
      for (int i=0; i<nodes.length;i++){
          //System.out.println("nodes["+i+"]"+nodes[i]);
          Vertex v=new Vertex(nodes[i],nodeIS.get(nodes[i]));
          Set<Integer> neighs=getneighbors(nodes[i]);
          if (!neighs.isEmpty()) {
              Iterator<Integer> it = neighs.iterator();
              int neigh = -1;
              while (it.hasNext()) {
                  neigh = it.next();
                  v.fillsketches(neigh, nodeIS.get(neigh));
                  if(neigh!=nodes[i] && nodeIS.get(neigh)>v.maxnodeIsneigh){
                     // System.out.println("neigh"+neigh+"nodes[i]"+nodes[i]);
                      v.hash=neigh;
                      v.maxnodeIsneigh=nodeIS.get(neigh);

                  }
              }
          }
          if (Vertexhashes.containsKey(v.hash)){
              Vertexhashes.get(v.hash).add(nodes[i]);
          }else{
              Set<Integer> s=new HashSet<Integer>();
              s.add(nodes[i]);
              Vertexhashes.put(v.hash,s);
          }
          vertexes.put(v.getId(),v);

      }
      /*
        for (int i=0; i<nodes.length;i++){
            Vertex v=vertexes.get(nodes[i]);
            Set<Integer> neighs=getneighbors(nodes[i]);
            if (!neighs.isEmpty()) {
                Iterator<Integer> it = neighs.iterator();
                int neigh=-1;
                while (it.hasNext()){
                    neigh = it.next();
                    v.fillsketches(neigh,nodeIS.get(neigh));

                }
            }

        }
        */

        return vertexes;
        }

    public HashMap<Integer,Vertex> fillvertexes_withattr(HashMap<Integer, Double>  nodeIS){
        vertexes=new HashMap<Integer,Vertex>() ;
        Vertexhashes=new HashMap<Integer, Set<Integer>>();
        for (int i=0; i<nodes.length;i++){
           // System.out.println("nodes["+i+"]"+nodes[i]);
            Vertex v=new Vertex(nodes[i],nodeIS.get(nodes[i]),attr_len);
            if (this.nodeVectorsarr.containsKey(nodes[i])){
                v.setAttribute_v(this.nodeVectorsarr.get(nodes[i]));
            }
            Set<Integer> neighs=getneighbors(nodes[i]);
            if (!neighs.isEmpty()) {
                Iterator<Integer> it = neighs.iterator();
                int neigh = -1;
                while (it.hasNext()) {
                    neigh = it.next();
                    v.fillsketches(neigh, nodeIS.get(neigh));
                    if(nodeIS.get(neigh)>v.maxnodeIsneigh){
                        v.hash=neigh;
                        v.maxnodeIsneigh=nodeIS.get(neigh);
                    }
                }
            }
            if (Vertexhashes.containsKey(v.hash)){
                Vertexhashes.get(v.hash).add(nodes[i]);
            }else{
                Set<Integer> s=new HashSet<Integer>();
                s.add(nodes[i]);
                Vertexhashes.put(v.hash,s);
            }
            vertexes.put(v.getId(),v);

        }
      /*
        for (int i=0; i<nodes.length;i++){
            Vertex v=vertexes.get(nodes[i]);
            Set<Integer> neighs=getneighbors(nodes[i]);
            if (!neighs.isEmpty()) {
                Iterator<Integer> it = neighs.iterator();
                int neigh=-1;
                while (it.hasNext()){
                    neigh = it.next();
                    v.fillsketches(neigh,nodeIS.get(neigh));

                }
            }

        }
        */

        return vertexes;
    }

        public void fillNodeAttributes(String inputPath){

            List<String[]> rows = new ArrayList<>();
            int featureCount = -1;

            try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\t");
                    // id attr1 attr2 ...
                    if (featureCount == -1) {
                        featureCount = parts.length - 1;
                    } else if (parts.length - 1 != featureCount) {
                         System.err.println("Line with unexpected feature count: " + line);
                    }
                    rows.add(parts);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.attr_len=featureCount;
            // map each feature values
            // Map: featureIndex -> (valueString -> categoryIndex)
            List<Map<String, Integer>> categoryMaps = new ArrayList<>();
            for (int f = 0; f < featureCount; f++) {
                categoryMaps.add(new LinkedHashMap<>());
            }

            // map each category of each attribute to a value
            for (String[] row : rows) {
                for (int f = 0; f < featureCount; f++) {
                    String val = row[f + 1];
                    Map<String, Integer> m = categoryMaps.get(f);
                    if (!m.containsKey(val)) {
                        int idx = m.size()+1;
                        m.put(val, idx);
                    }
                }
            }


            inverseCategoryMaps = new ArrayList<>();
            for (int f = 0; f < featureCount; f++) {
                Map<Integer, String> inv = new LinkedHashMap<>();
                Map<String, Integer> m = categoryMaps.get(f);
                 for (Map.Entry<String, Integer> e : m.entrySet()) {
                    inv.put(e.getValue(), e.getKey());
                }
                inverseCategoryMaps.add(inv);
            }

            // turn values of attribute of each node in int array
            this.nodeVectorsarr=new HashMap<Integer, int[]>();
            //this.nodeVectors = new ArrayList<>();
            for (String[] row : rows) {
                int[] vec = new int[featureCount];
                for (int f = 0; f < featureCount; f++) {
                    String val = row[f + 1];
                    Map<String, Integer> m = categoryMaps.get(f);
                    vec[f] = m.get(val); // int value of category of attribute
                }
                //this.nodeVectors.add(vec);
                this.nodeVectorsarr.put(Integer.valueOf(row[0]),vec);
            }

            // sample print
           // for (int i = 0; i < Math.min(5, this.nodeVectorsarr.size()); i++) {
                //System.out.println("Node " + rows.get(i)[0] + ": " + Arrays.toString(this.nodeVectors.get(i)));
             //   System.out.println("Node " + rows.get(i)[0] + ": " + Arrays.toString(this.nodeVectorsarr.get(i)));
           // }
        }

    public int[] getNodes() {
        return nodes;
    }
    Set<Integer> getneighbors(int id) {
        //node v= new node(id);
        Set<Integer> neighs = new HashSet<Integer>();
        // Iterate through the array
        int s=allVertices[id][0];
        int e=allVertices[id][1];
       // if (id==101383) {
        //     System.out.println("id"+id);
      //      System.out.println("s"+s);
       //      System.out.println("e"+e);
       // }
        for (int index=s;index<e;index++) {
            // Add each element into the set
            //if (id==101383) {
           //     System.out.println("index" + index);
          //      System.out.println("adjVertices[index]" + adjVertices[index]);
           // }
            neighs.add( adjVertices[index]);
        }
        return neighs;
    }

    int[] getneighborsarry(int id) {
        //node v= new node(id);
        int [] neighsarr;
        Set<Integer> neighs = new HashSet<Integer>();
        // Iterate through the array
        int s=allVertices[id][0];
        int e=allVertices[id][1];
        // if (id==101383) {
        //     System.out.println("id"+id);
        //      System.out.println("s"+s);
        //      System.out.println("e"+e);
        // }
        for (int index=s;index<e;index++) {
            // Add each element into the set
            //if (id==101383) {
            //     System.out.println("index" + index);
            //      System.out.println("adjVertices[index]" + adjVertices[index]);
            // }
            neighs.add( adjVertices[index]);
        }
        neighsarr=new int[neighs.size()];
        int i=0;
        for(int n:neighs){
            neighsarr[i++]=n;
        }
        return neighsarr;
    }

    Set<Integer> getsuccessor(int id) {
        //node v= new node(id);
        Set<Integer> neighs = new HashSet<Integer>();
        // Iterate through the array
        int s=allVertices[id][0];
        int e=allVertices[id][1];
        for (int index=s;index<e;index++) {
            // Add each element into the set
            if (id< adjVertices[index])
                 neighs.add( adjVertices[index]);
        }
        return neighs;
    }
    public int[] getsuccessorarray(int id) {
        //node v= new node(id);
        //Set<Integer> neighs = new HashSet<Integer>();
        // Iterate through the array
        int s=allVertices[id][0];
        int e=allVertices[id][1];
        int[] neighs=new int[e-s];
        int i=0;
        for (int index=s;index<e;index++) {
            // Add each element into the set
            if (id< adjVertices[index])
                neighs[i]= adjVertices[index];
               // neighs.add( adjVertices[index]);
        }
        return neighs;
    }

    public int numNodes() {
        return nodes.length;
    }

    public grapharray creat2hopgraph() throws Exception{
        G2hop= new grapharray();
        G2hop.nodes=nodes;
        G2hop.allVertices=new int[maxid+1][3];
        for (int[] row: G2hop.allVertices)
            Arrays.fill(row, -1);
        List<Integer> adjVerticesG2list = new ArrayList<Integer>();
        //HashMap<Integer, Set<Integer>> checkededges= new HashMap<Integer, Set<Integer>>();
        int numedge2hop=0;
        int indexnow=0;
        int selectednode,selectnode=0,numneighnode,mindistance;
        //ps2hop= new PrintStream(new File("files/"  + filename.substring(0,filename.length()-4) + "2hop.txt"));
        for (int i = 0; i<nodes.length; i++) {
            selectednode=-1;selectnode=0;
            mindistance=Integer.MAX_VALUE;
            numneighnode=getneighbors(nodes[i]).size();
            Set<Integer> neighneighnode = new HashSet<>();
            //System.out.println("333332hop");
            int s=allVertices[nodes[i]][0];
            int e=allVertices[nodes[i]][1];
            //System.out.println("nodes["+i+"]"+nodes[i]);
            for (int index=s;index<e;index++) {
               // System.out.println("index"+index);
               // System.out.println("adjVertices[index]"+adjVertices[index]);

                if (getneighbors(adjVertices[index]).size()>9) {
                    for (Integer k : getneighbors(adjVertices[index])) {
                        if (!(getneighbors(k).size() > getneighbors(nodes[i]).size() - 1
                                & getneighbors(k).size() < getneighbors(nodes[i]).size() + 1)){
                       if (Math.abs((getneighbors(k).size()-numneighnode))<mindistance){
                           mindistance=Math.abs((getneighbors(k).size()-numneighnode));
                           selectednode=k;
                       }
                            continue;
                        }

                        neighneighnode.add(k);
                        selectnode=1;
                    }
                }else {
                    neighneighnode.addAll(getneighbors(adjVertices[index]));
                    selectnode=1;
                }
                if (selectnode!=-1)neighneighnode.add(selectednode);
            }
            neighneighnode.remove(nodes[i]);
            G2hop.allVertices[nodes[i]][0] =indexnow;
            indexnow+=neighneighnode.size();
            G2hop.allVertices[nodes[i]][1] =indexnow;
            //for (Integer n : neighneighnode)
            //   adjVerticesG2list.add(n);
            adjVerticesG2list.addAll(neighneighnode);
            System.out.println("adjVerticesG2list.size()"+adjVerticesG2list.size());


        }
        G2hop.adjVertices = new int[adjVerticesG2list.size()];
        int i = 0;
        for (int e : adjVerticesG2list)
            G2hop.adjVertices[i++] = e;
        adjVerticesG2list.clear();
        G2hop.numedges=(G2hop.adjVertices.length/2)+1;
       // System.out.println("adjVertices.length"+adjVertices.length);
        return G2hop;
    }

    public grapharray creat2hopgraphsuccessor(String GRAPH_BASENAME) throws Exception{
        PrintStream ps2hop=new PrintStream(new File(GRAPH_BASENAME.substring(0,GRAPH_BASENAME.length()-4)+"2hop.txt" ));
        G2hop= new grapharray();
        G2hop.nodes=nodes;
        G2hop.allVertices=new int[maxid+1][3];
        for (int[] row: G2hop.allVertices)
            Arrays.fill(row, -1);
        List<Integer> adjVerticesG2list = new ArrayList<Integer>();
        //HashMap<Integer, Set<Integer>> checkededges= new HashMap<Integer, Set<Integer>>();
        int numedge2hop=0;
        int indexnow=0;
        int selectednode,selectnode=0,numneighnode,mindistance;
        //ps2hop= new PrintStream(new File("files/"  + filename.substring(0,filename.length()-4) + "2hop.txt"));
        for (int i = 0; i<nodes.length; i++) {
            selectednode=-1;selectnode=0;
            mindistance=Integer.MAX_VALUE;
            numneighnode=getneighbors(nodes[i]).size();
            Set<Integer> neighneighnode = new HashSet<>();
            //System.out.println("333332hop");
            int s=allVertices[nodes[i]][0];
            int e=allVertices[nodes[i]][1];
            //System.out.println("nodes["+i+"]"+nodes[i]);
            for (int index=s;index<e;index++) {
                // System.out.println("index"+index);
                // System.out.println("adjVertices[index]"+adjVertices[index]);
                //if (getneighbors(adjVertices[index]).size()>30 &
                 //       !(getneighbors(adjVertices[index]).size()>getneighbors(nodes[i]).size()-2
                 //       & getneighbors(adjVertices[index]).size()<getneighbors(nodes[i]).size()+2))continue;
               // Set<Integer> neigh=getneighbors(adjVertices[index]);
                //if (getneighbors(adjVertices[index]).size()>30)continue;
                if (getneighbors(adjVertices[index]).size()>9) {
                    for (Integer k : getneighbors(adjVertices[index])) {
                        if (!(getneighbors(k).size() > getneighbors(nodes[i]).size() - 1
                                & getneighbors(k).size() < getneighbors(nodes[i]).size() + 1)){
                            if (Math.abs((getneighbors(k).size()-numneighnode))<mindistance){
                                mindistance=Math.abs((getneighbors(k).size()-numneighnode));
                                if (k>nodes[i]) selectednode=k;
                            }
                            continue;
                        }
                     if (k>nodes[i]){
                         neighneighnode.add(k);
                         selectnode=1;
                     }

                    }
                }else {
                    neighneighnode.addAll(getneighbors(adjVertices[index]));
                    selectnode=1;
                }
                if (selectednode!=-1 & selectnode==0)neighneighnode.add(selectednode);
            }
            neighneighnode.remove(nodes[i]);
            //for (int e2 : neighneighnode) {
             //   if (e2<= nodes[i]) {
              //      neighneighnode.remove(e2);
              //  }
            //}
            G2hop.allVertices[nodes[i]][0] =indexnow;
            indexnow+=neighneighnode.size();
            G2hop.allVertices[nodes[i]][1] =indexnow;
            for (int p:neighneighnode){
                ps2hop.print( nodes[i]+"\t" + p);
                ps2hop.println();
            }
            //for (Integer n : neighneighnode)
            //   adjVerticesG2list.add(n);
            adjVerticesG2list.addAll(neighneighnode);
            //System.out.println("adjVerticesG2list.size()"+adjVerticesG2list.size());


        }
        G2hop.adjVertices = new int[adjVerticesG2list.size()];
        int i = 0;
        for (int e : adjVerticesG2list)
            G2hop.adjVertices[i++] = e;
        adjVerticesG2list.clear();
        G2hop.numedges=(G2hop.adjVertices.length);
        ps2hop.close();
        ps2hop.close();
        //System.out.println("adjVertices.length"+adjVertices.length);
        return G2hop;
    }


    public void creatRECgraph(String superrnodes,String superedges) throws Exception{
       int numsupernodes=0;
        nodedegree=new TreeMap<Integer, Integer>();
        List<Integer> nodeslist = new ArrayList<Integer>();
        //psrecnodes=new PrintStream(new File(directory  + filenameSe.substring(0,filenameSe.length()-4) + "_RECgraph.txt"));
        Scanner scannerSU = new Scanner(new File(superrnodes));
      //  System.out.println("333333");
        while (scannerSU.hasNextLine()) {
            scannerSU.nextLine();
            numsupernodes++;
            //System.out.println("numsupernodes"+numsupernodes);
             }
        scannerSU.close();
        //scanner=null;
        scannerSU = new Scanner(new File(superrnodes));
        //System.out.println("4444444");
        uf=new UnionFind(numsupernodes);
        supernodesweights=new int[numsupernodes];
        //System.out.println("55555555555");
        superNodes = uf.getSuperNodes();
      //  System.out.println("66666666666666");
        int count=0;
        while (scannerSU.hasNextLine()) {
            //System.out.println("22222");
            String[] newnodes = scannerSU.nextLine().split("\t");
            supernodesweights[count]=newnodes.length-1;
            //System.out.println("777777777");
            uf.removefirstnodeofsupernode(superNodes[count]);
            //System.out.println("888888888888");
            uf.addnodestosupernode(superNodes[count],Arrays.copyOfRange(newnodes, 1, newnodes.length));
          //  System.out.println("99999999999999");
            for (int i = 1; i < newnodes.length; i++) {
                if (Integer.parseInt(newnodes[i]) > maxid) {

                    maxid = Integer.parseInt(newnodes[i]);
                }
                //System.out.println(uf.getnodesofsupernode(superNodes[count])+" count "+count+" supernodesweights[count] "+supernodesweights[count]);
                }
            //List<Integer> listnodesSU = uf.getnodesofsupernode(superNodes[count]);
            //System.out.println("listnodesSU"+listnodesSU);
            /////chera gozashtam  count++;
            //System.out.println("doing add edge");
        count++;
        }
        scannerSU.close();
        Scanner scannerSE = new Scanner(new File(superedges));
       // System.out.println("10101010");
        while (scannerSE.hasNextLine()) {
            String[] pair = scannerSE.nextLine().split("\t");
            int node1 = Integer.parseInt(pair[0]);
           // System.out.println("node1"+node1);
            int node2 = Integer.parseInt(pair[1]);
           // System.out.println("node2"+node2);
           // System.out.println("numedges "+numedges);
            if (node1==node2)numedges+=(supernodesweights[node1]*(supernodesweights[node2]-1));
            else numedges+=(supernodesweights[node1]*(supernodesweights[node2]));
           // System.out.println("node1"+node1+" supernodesweights[node1] "+supernodesweights[node1]);
           // System.out.println("node2"+node2+" supernodesweights[node2] "+supernodesweights[node2]);
          //  System.out.println("numedges "+numedges);
            //chera gozashtam  G.addEdge(node1,node2);
            List<Integer> listnodesSU = uf.getnodesofsupernode(superNodes[node1]);
           // System.out.println("listnodesSU"+listnodesSU);
            List<Integer> listnodesSv = uf.getnodesofsupernode(superNodes[node2]);
            //System.out.println("listnodesSv"+listnodesSv);
            for (int nodeu:listnodesSU){
                for(int nodev:listnodesSv){
                   // GREC.addNode(nodeu);
                    //System.out.println("nodeu"+nodeu);
                   // System.out.println("nodev"+nodev);
                   // GREC.addNode(nodev);
                   if (nodeu<nodev){

                    if(nodedegree.containsKey(nodeu)){
                        nodedegree.put(nodeu,nodedegree.get(nodeu)+ 1);
                       // System.out.println(nodeu+"nodedegree.get(nodeu)"+nodedegree.get(nodeu));
                    }
                    else{
                        nodedegree.put(nodeu,1);
                        nodeslist.add(nodeu);
                       // System.out.println(nodeu+"nodedegree.get(nodeu)"+nodedegree.get(nodeu));
                    }
                    if(nodedegree.containsKey(nodev)){
                        nodedegree.put(nodev,nodedegree.get(nodev)+ 1);
                        //System.out.println(nodev+"nodedegree.get(nodev)"+nodedegree.get(nodev));
                    }
                    else{
                        nodedegree.put(nodev,1);
                        nodeslist.add(nodev);
                        //System.out.println(nodev+"nodedegree.get(nodev)"+nodedegree.get(nodev));
                    }
                   }
                    // System.out.println("4444");
                   //// if (nodeu!=nodev){GREC.addEdge(nodeu,nodev);}
                }
            }
        }
        scannerSE.close();

        System.out.println("maxid"+maxid);
        System.out.println("numedges "+numedges);
        allVertices= new int[maxid+1][3];
        for (int[] row: allVertices)
            Arrays.fill(row, -1);
        //Arrays.fill(allVertices, -1);
        //System.out.println("fill");
        adjVertices= new int[2*numedges];
       // System.out.println("nodeslist.size()"+nodeslist.size());
        nodes = new int[nodeslist.size()];
        int i = 0;
        for (Integer e : nodeslist) {
            nodes[i++] = e;
            //System.out.println("e"+e);
            //System.out.println("i"+i);
        }
        nodeslist.clear();
        Arrays.sort(nodes);
       // for (Integer e : nodes) {
       //     System.out.println("e"+e);

       // }

      //  System.out.println("1111");
        int nl=nodes.length;
        int nowindex=0;
        for (int j=0;j<nl;j++){
            allVertices[nodes[j]][0]=nowindex;
            allVertices[nodes[j]][2]=nowindex;
            nowindex+=nodedegree.get(nodes[j]);
            allVertices[nodes[j]][1]=nowindex;
           // System.out.println("nodes["+j+"]"+nodes[j]);
           // System.out.println("allVertices[nodes[j]][0]"+allVertices[nodes[j]][0]);
            //System.out.println("allVertices[nodes[j]][1]"+allVertices[nodes[j]][1]);
            //System.out.println("allVertices[nodes[j]][2]"+allVertices[nodes[j]][2]);
            //
        }
        scannerSE = new Scanner(new File(superedges));
        while (scannerSE.hasNextLine()) {
            //System.out.println("22222");
            String[] pair = scannerSE.nextLine().split("\t");
            int node1id =Integer.parseInt(pair[0]);
            int node2id =Integer.parseInt(pair[1]);
            List<Integer> listnodesSU = uf.getnodesofsupernode(superNodes[node1id]);
           // System.out.println("listnodesSU"+listnodesSU);
            List<Integer> listnodesSv = uf.getnodesofsupernode(superNodes[node2id]);
            //System.out.println("listnodesSv"+listnodesSv);
            for (int nodeu:listnodesSU) {
                for (int nodev : listnodesSv) {
                    if (pair[0].contains("#")) continue;
                   //System.out.println("nodeu"+nodeu+"nodev"+nodev);
                   if (nodeu<nodev) {
                       adjVertices[allVertices[nodeu][2]] = nodev;
                       adjVertices[allVertices[nodev][2]] = nodeu;
                       //System.out.println("adjVertices[allVertices[nodeu][2]]" + adjVertices[allVertices[nodeu][2]]);
                      // System.out.println("adjVertices[allVertices[nodev][2]]" + adjVertices[allVertices[nodev][2]]);
                       //System.out.println("allVertices[nodeu][2]" + allVertices[nodeu][2] + "allVertices[nodev][2]" + allVertices[nodev][2]);

                       allVertices[nodeu][2] += 1;
                       allVertices[nodev][2] += 1;
                   }

                }
            }
        }
                scannerSE.close();


        nodedegree.clear();

       // System.out.println("nodes Graph");

        System.out.println("completed reading Graph");
        System.out.println (superrnodes);
        //return G;
    }


}



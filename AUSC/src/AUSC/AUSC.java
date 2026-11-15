package AUSC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class AUSC {
    static CountMinSketch sketch;
    static int sketchWidth;
    static int sketchDepth;
    static SamplingNode SamplingTreeRoot;
    static double weightspurious;
    private static UnionFind uf,ufmerge;
    static String outputDirectory = "output/";
    static String filename, filename_attr;
    static String directory = "files/";
    private static String parameterType,typcentrality;
    private static String GRAPH_BASENAME;
    private static HashMap<Integer, Double>  nodeIS;
    private static final Random RANDOM = new Random();
    private static HashMap<Integer, Integer>  Degree;
    private static HashMap<Integer, Boolean> vistnodes;
    public static HashMap<Integer, Set<Integer>> checkededges;
    static HashMap<Integer,Integer> histogramsizsuprnodes ;
    private static TreeMap<Integer,Double> map;
    public static String NODE_BC_BASENAME,MSTfile_BASENAME;
    private static double threshold;
    private static double VC2,reductionEdges,sumnoralizedError=0,sumnsecost=0.0,sumsecost=0.0,finalpurityattrs=1.0;
    private static int maxid,numedge,numsuperedge, realnumSuperNodes=0, numedgesinmst;
    //static Graph G,G2hop;
    //static GraphNodeObj G,G2hop;
    static grapharray G,G2hop;
    static long t1;
    static int attrebuted_g=0,attrbutelen=1;
    private static PrintStream ps2hop,psMST2hop,psSU,psSUattrs,psSE,psresult,pshistsupernode;
    private static double[][] twoHopEdgeList,MSTEdgelist,selectedpairs;
    static double[] purityattrs;
    static long seed=30;

    private static void normalizeNodeIS(){
        double sumNodeIS=0;
        Iterator<Integer> keySetIterator = nodeIS.keySet().iterator();
        while(keySetIterator.hasNext()){

            Integer key= keySetIterator.next();
            sumNodeIS+=nodeIS.get(key);}

        System.out.println("sumNodeIS"+sumNodeIS);
        Iterator<Integer> keySetIterator2 = nodeIS.keySet().iterator();
        while(keySetIterator2.hasNext()){

            Integer key= keySetIterator2.next();
            nodeIS.put(key,(Double) nodeIS.get(key)/(Double) sumNodeIS);
            //System.out.println("nodeIS.get("+key+")"+nodeIS.get(key));
        }
    }
    private static void readNodeIS() throws Exception{
        nodeIS = new HashMap<Integer,Double>();
        Scanner scanner = new Scanner(new File(NODE_BC_BASENAME));
        //System.out.println("scanner");
        while (scanner.hasNextLine()) {
            String[] pair = scanner.nextLine().split("\t");
            int node = Integer.parseInt(pair[0]);
            double value = Double.parseDouble(pair[1]);
            //System.out.println("node"+node);
            //System.out.println("value"+value);
            nodeIS.put(node, value);
        }
        scanner.close();
        normalizeNodeIS();
        System.out.println("normalizeNodeIS");
        System.out.println("completed reading nodeIS");
        Iterator<Integer> keySetIterator = nodeIS.keySet().iterator();
        while(keySetIterator.hasNext()){

            Integer key= keySetIterator.next();
            if (nodeIS.get(key)< 0 ) System.out.println(" Negative Centrality");
        }

    }

    public static SamplingNode buildSamplingTree(int st, int end) {
        if (end - st == 0) {

            double treeWeight = 1.0/nodeIS.get(G.nodes[st]);
            //double treeWeight = nodeIS.get(G.nodes[st]);
            if (treeWeight == 0) {
                System.out.println("Weight is zero");
                treeWeight = -1 / Double.MIN_VALUE;
                System.out.println("Value in tree = " + treeWeight);
            }

            else
                //treeWeight = -1 / treeWeight;
            treeWeight = 1 / treeWeight;
            //SamplingNode crntNode = new SamplingNode(listOfVertices.get(
            //		vertexids[st]).getId(), treeWeight);
            Set<Integer> c = new HashSet<Integer>();
            //System.out.println("G.vertexes.get(G.nodes["+st+"])"+G.vertexes.get(G.nodes[st]));
            //System.out.println("G.vertexes.get(G.nodes[st]).hash"+G.vertexes.get(G.nodes[st]).hash);
            c.add(G.vertexes.get(G.nodes[st]).hash);
            SamplingNode crntNode = new SamplingNode(G.vertexes.get(
                    G.nodes[st]).getId(), treeWeight,c);
            G.vertexes.get(G.nodes[st]).nodeRef = crntNode;
            return crntNode;
        } else {
            int mid = (end + st) / 2;
            SamplingNode left = buildSamplingTree(st, mid);
            SamplingNode right = buildSamplingTree(mid + 1, end);

            //SamplingNode crntNode = new SamplingNode(-1, left.weight
            //		+ right.weight);
            Set<Integer> c = new HashSet<>(left.hashvertices); // کپی از a
            c.addAll(right.hashvertices);
            SamplingNode crntNode = new SamplingNode(-1, left.weight
                    + right.weight,c);
            crntNode.leftChild = left;
            crntNode.rightChild = right;
            left.parent = crntNode;
            right.parent = crntNode;
            return crntNode;
        }
    }
    public static void updateSamplingTree(SamplingNode crntNode) {
        try {
            double x, y;

            while (crntNode != null) {
                x = 0.0;
                y = 0.0;
                if (crntNode.leftChild.weight != -1)
                    x = crntNode.leftChild.weight;
                if (crntNode.rightChild.weight != -1)
                    y = crntNode.rightChild.weight;
                crntNode.weight = x + y;

                crntNode = crntNode.parent;
            }
        } catch (Exception exp) {

            System.out.println("updateSamplingTree exception");
        }

    }

    public static void deleteSamplingNode(SamplingNode crntNode) {
        //System.out.println("crntNode.weight"+crntNode.weight);
        crntNode.weight = 0.0;
        //System.out.println("SamplingTreeRoot.weight"+SamplingTreeRoot.weight);

        updateSamplingTree(crntNode.parent);
        crntNode.id = -1;
        crntNode.weight = -1;
        //System.out.println("SamplingTreeRoot.weight"+SamplingTreeRoot.weight);

    }

    public static ArrayList<Pair> samplePairsWeighted_Treebased(int size) {
        //int numsamnonzero=0;
        ArrayList<Pair> samplePairs = new ArrayList<Pair>();
        int presize=0,nochange=0;
        while (samplePairs.size() < size && SamplingTreeRoot.weight>0 && ((attrebuted_g ==1 && nochange<100)|| attrebuted_g ==0)) {
            //  while (samplePairs.size() < size && SamplingTreeRoot.weight>0 ) {
            //System.out.println("samplePairs.size()"+samplePairs.size()+"presize "+presize);
            if(samplePairs.size()==presize){
                nochange=nochange+1;
            }
            presize=samplePairs.size();
            // while (numsamnonzero < size) {
            //System.out.println("samplePairs.size()"+samplePairs.size());
            //System.out.println("SamplingTreeRoot.weight"+SamplingTreeRoot.weight);
            // e.g., 1..30
            Random random = new Random(seed);
            //double value = random.nextDouble() * SamplingTreeRoot.weight;
            double value = RANDOM.nextDouble() * SamplingTreeRoot.weight;
            //System.out.println("value"+value);
            int v1 = getLeaf(value, SamplingTreeRoot);
            //System.out.println("v1"+v1);
            // value = random.nextDouble() * SamplingTreeRoot.weight;
            int hash=G.vertexes.get(v1).hash;
            //int v2 = getLeaf(value, SamplingTreeRoot);
            //int v2 = getLeafwithhash(hash, SamplingTreeRoot);
            int [] neigharr=G.getneighborsarry(hash);

            Set<Integer> candidates = G.getneighbors(hash);
            double maxScore = -1 * Double.MAX_VALUE;
            Pair bestScoringPair = null;
            value = random.nextInt(candidates.size());
            int f = -1;
            if (Degree.get(hash)==1){
                Pair p = new Pair(v1, hash);
                double score_ut=computeScore_utility(p);
                double score_final=score_ut;
                if (attrebuted_g==1){
                    score_final=0.5*score_final+0.5*compute_score_attribute(p);
                }
                p.setScore(score_final);
                samplePairs.add(p);
                nochange=0;
                G.vertexes.get(v1).visited=G.vertexes.get(v1).visited+1;
                deleteSamplingNode(G.vertexes.get(v1).nodeRef);
                continue;
            }
            for (int v2 : candidates) {
                // f++;
                if (v2 == v1) continue;
                Vertex vb = G.vertexes.get(v2);
                if (vb == null) continue;
                Pair p = new Pair(v1, v2);
                double score_ut=computeScore_utility(p);
                double score_att=0;
                double score_final=score_ut;
                if (attrebuted_g==1){
                    score_att=compute_score_attribute(p);
                    //  System.out.println("score_ut "+score_ut+ "score_att "+score_att);
                    score_final=1*score_final+0*score_att;
                    //  System.out.println("score_final "+score_final);
                }
                //double score =0.5*approxDotProductJaccard(p) + 0.5*approxDotProductGOterm(p);

                p.setScore(score_final);
                if (score_final > maxScore && ufmerge.find(v1) != ufmerge.find(v2) ) {
                    if(attrebuted_g==1){
                        //System.out.println("G.Vertexhashes.get(hash).size()"+G.Vertexhashes.get(hash).size()+" value "+value+" v2 "+v2);
                        if (check_have_same_attribute(G.vertexes.get(v1),G.vertexes.get(v2))>0){
                            bestScoringPair = p;
                            maxScore = score_final;
                        }}}
                // if (score>=0.6){
                if (score_ut>100){
                    if (ufmerge.find(v1)!= ufmerge.find(v2)){
                        if(attrebuted_g==1){
                            //System.out.println("G.Vertexhashes.get(hash).size()"+G.Vertexhashes.get(hash).size()+" value "+value+" v2 "+v2);
                            if (check_have_same_attribute(G.vertexes.get(v1),G.vertexes.get(v2))>0){
                                G.vertexes.get(v1).selectedpair=v2;
                                ufmerge.union(v1,v2);
                                // System.out.println("v1"+v1+" v2 "+v2+"ufmerge.find(v1)"+ufmerge.find(v1)+"ufmerge.find(v2)"+ufmerge.find(v2));
                                samplePairs.add(p);
                                nochange=0;
                                G.vertexes.get(v1).visited=G.vertexes.get(v1).visited+1;
                                G.vertexes.get(v2).visited=G.vertexes.get(v2).visited+1;
                            }}}
                }
                if (f>=value) break;
            }
            if ( bestScoringPair!=null) {
                int v2 = bestScoringPair.getNode2();
                if (ufmerge.find(v1) != ufmerge.find(v2)) {
                    ufmerge.union(v1, v2);
                    samplePairs.add(bestScoringPair);
                    nochange = 0;
                    G.vertexes.get(v1).visited = G.vertexes.get(v1).visited + 1;
                    G.vertexes.get(v2).visited = G.vertexes.get(v2).visited + 1;
                }
            }
            if (G.vertexes.get(v1).visited> 1) {
                deleteSamplingNode(G.vertexes.get(v1).nodeRef);
            }
        }
        return samplePairs;
    }

    public static ArrayList<Pair> samplePairsWeighted_Treebased2(int size) {
        ArrayList<Pair> samplePairs = new ArrayList<Pair>();
        int presize=0,nochange=0;
        while (samplePairs.size() < size && SamplingTreeRoot.weight>0 && ((attrebuted_g==1 && nochange<100)|| attrebuted_g==0)) {
            //System.out.println("samplePairs.size()"+samplePairs.size()+"presize "+presize);
            if(samplePairs.size()==presize){
                nochange=nochange+1;
            }
            presize=samplePairs.size();
            Random random = new Random();
            double value = RANDOM.nextDouble() * SamplingTreeRoot.weight;
            //System.out.println("value"+value);
            int v1 = getLeaf(value, SamplingTreeRoot);
            //System.out.println("v1"+v1);
            int hash=G.vertexes.get(v1).hash;
            int v2=-1;
            G.Vertexhashes.get(hash);
            Pair bestScoringPair = null;
            if (!G.Vertexhashes.get(hash).isEmpty() && G.Vertexhashes.get(hash).size()>1) {
                Iterator<Integer> it = G.Vertexhashes.get(hash).iterator();
                int f = -1;
                value = random.nextInt(G.Vertexhashes.get(hash).size());
                double maxScore = -1 * Double.MAX_VALUE;
                int selectedpair=G.vertexes.get(v1).selectedpair;
                // while (f < G.Vertexhashes.get(hash).size()-1 ) {
                while (f < value) {
                    v2 = it.next();
                    Pair p = new Pair(v1, v2);
                    double score_ut=computeScore_utility(p);
                    double score_final=score_ut;
                    if (attrebuted_g==1){
                        score_final=score_final+compute_score_attribute(p);
                    }
                    p.setScore(score_final);
                    if (score_ut > maxScore && selectedpair!=v2) {
                        bestScoringPair = p;
                        maxScore = score_ut;
                    }
                    f++;
                }

                if(attrebuted_g==1){
                    //System.out.println("G.Vertexhashes.get(hash).size()"+G.Vertexhashes.get(hash).size()+" value "+value+" v2 "+v2);
                    if (check_have_same_attribute(G.vertexes.get(v1),G.vertexes.get(v2))==0){
                        v2=-1;
                    }
                }
            }
            if (v1 != v2 && v2!=-1 && bestScoringPair!=null) {

                if (ufmerge.find(v1)!= ufmerge.find(v2)){

                    Pair p=bestScoringPair;
                    G.vertexes.get(v1).selectedpair=v2;
                    ufmerge.union(v1,v2);
                    samplePairs.add(p);
                    nochange=0;

                    G.vertexes.get(v1).visited=G.vertexes.get(v1).visited+1;
                    G.vertexes.get(v2).visited=G.vertexes.get(v2).visited+1;

                }

                if (G.vertexes.get(v1).visited>= 1) {
                    deleteSamplingNode(G.vertexes.get(v1).nodeRef);
                }
            }
            if (G.vertexes.get(v1).visited < 1 && G.Vertexhashes.get(hash).size()<=1){
                if (Degree.get(v1)<=2){
                    if(attrebuted_g==0) {
                        v2 = G.getneighborsarry(v1)[0];
                        Pair p = new Pair(v1, v2);
                        double score_final=computeScore_utility(p);
                        p.setScore(score_final);
                        ufmerge.union(v1, v2);
                        samplePairs.add(p);
                        nochange = 0;
                        G.vertexes.get(v2).visited=G.vertexes.get(v2).visited+1;
                        if (G.Vertexhashes.get(G.vertexes.get(v2).hash).size()<=1)
                            deleteSamplingNode(G.vertexes.get(v2).nodeRef);
                    }
                    if(attrebuted_g==1){
                        v2 = G.getneighborsarry(v1)[0];
                        if (check_have_same_attribute(G.vertexes.get(v1),G.vertexes.get(v2))>0){
                            Pair p = new Pair(v1, v2);
                            ufmerge.union(v1, v2);
                            double score_ut=computeScore_utility(p);
                            double score_final=score_ut+compute_score_attribute(p);

                            p.setScore(score_final);
                            samplePairs.add(p);
                            nochange = 0;
                            G.vertexes.get(v2).visited=G.vertexes.get(v2).visited+1;
                        }
                    }
                }
                deleteSamplingNode(G.vertexes.get(v1).nodeRef);
            }
        }
        return samplePairs;
    }

    public static int getLeaf(double value, SamplingNode crnt) {

        if (crnt.leftChild == null && crnt.rightChild == null && crnt.id != -1)
            return crnt.id;
        else {
            if (value < crnt.leftChild.weight)
                return getLeaf(value, crnt.leftChild);
            else
                return getLeaf(value - crnt.leftChild.weight, crnt.rightChild);
        }
    }

    public static double approxDotProductUtility(Pair pair) {
        int a = pair.getNode1();
        int b = pair.getNode2();
        Vertex v_a = G.vertexes.get(a);
        Vertex v_b = G.vertexes.get(b);
        double minDotProduct= Integer.MAX_VALUE;
        double minDotProductab = Integer.MAX_VALUE;
        double minDotProductba = Integer.MAX_VALUE;
        for (int i = 0; i < sketchDepth; i++) {
            double dotProductab = 0;
            for (int j = 0; j < sketchWidth; j++) {
                dotProductab = dotProductab + v_a.countMinBucketUtility[i][j]
                        * v_b.countMinBucketADJ[i][j];
            }
            if (dotProductab < minDotProductab)
                minDotProductab = dotProductab;
        }
        for (int i = 0; i < sketchDepth; i++) {
            double dotProductba = 0;
            for (int j = 0; j < sketchWidth; j++) {
                dotProductba = dotProductba + v_a.countMinBucketADJ[i][j]
                        * v_b.countMinBucketUtility[i][j];
            }
            if (dotProductba < minDotProductba)
                minDotProductba = dotProductba;
        }
        if (minDotProductab < minDotProductba)
            minDotProduct = minDotProductab;
        else minDotProduct = minDotProductba;
        return minDotProduct;
    }

    public static double compute_score_attribute(Pair pair) {
        int a = pair.getNode1();
        int b = pair.getNode2();
        Vertex v_a = G.vertexes.get(a);
        Vertex v_b = G.vertexes.get(b);
        int[] a_attr=v_a.getAttribute_v();
        int[] b_attr=v_b.getAttribute_v();
        double zeroCount=0;
        for (int i = 0; i < a_attr.length; i++) {
            int ai = a_attr[i];
            int bi = b_attr[i];
            //if (ai == bi) {
            if (Math.abs(ai - bi) <= 0) {
                zeroCount++;
            }
        }

        return zeroCount/a_attr.length;
    }
    public static double check_have_same_attribute(Vertex a,Vertex b) {
        double zeroCount=0;
        //System.out.println("zeroCount"+zeroCount+" id "+a.getId()+" id "+b.getId());
        int[] a_attr=a.getAttribute_v();
        int[] b_attr=b.getAttribute_v();

        for (int i = 0; i < a_attr.length; i++) {
            int ai = a_attr[i];
            int bi = b_attr[i];
            //if (ai == bi) {
            if (Math.abs(ai - bi) <= 0) {
                zeroCount++;
            }
        }
        //System.out.println("zeroCount"+zeroCount+" id "+a.getId()+" id "+b.getId());
        return zeroCount/a_attr.length;
    }
    public static double approxDotProductJaccard(Pair pair) {
        int a = pair.getNode1();
        int b = pair.getNode2();
        Vertex v_a = G.vertexes.get(a);
        Vertex v_b = G.vertexes.get(b);
        double minDotProduct= Integer.MAX_VALUE;
        double minDotProductab = Integer.MAX_VALUE;
        double minDotProductba = Integer.MAX_VALUE;
        for (int i = 0; i < sketchDepth; i++) {
            double dotProductab = 0;
            for (int j = 0; j < sketchWidth; j++) {
                dotProductab = dotProductab + v_a.countMinBucketADJ[i][j]
                        * v_b.countMinBucketADJ[i][j];
            }
            if (dotProductab < minDotProductab)
                minDotProductab = dotProductab;
        }
        for (int i = 0; i < sketchDepth; i++) {
            double dotProductba = 0;
            for (int j = 0; j < sketchWidth; j++) {
                dotProductba = dotProductba + v_a.countMinBucketADJ[i][j]
                        * v_b.countMinBucketADJ[i][j];
            }
            if (dotProductba < minDotProductba)
                minDotProductba = dotProductba;
        }
        if (minDotProductab < minDotProductba)
            minDotProduct = minDotProductab;
        else minDotProduct = minDotProductba;
        return (2*minDotProduct)/(Degree.get(a)+Degree.get(b));
    }
    public static double approxDotProductNumneighs(Pair pair) {
        int a = pair.getNode1();
        int b = pair.getNode2();
        Vertex v_a = G.vertexes.get(a);
        Vertex v_b = G.vertexes.get(b);
        double minDotProduct= Integer.MAX_VALUE;
        double minDotProductab = Integer.MAX_VALUE;
        double minDotProductba = Integer.MAX_VALUE;
        for (int i = 0; i < sketchDepth; i++) {
            double dotProductab = 0;
            for (int j = 0; j < sketchWidth; j++) {
                dotProductab = dotProductab + v_a.countMinBucketNNeigh[i][j]
                        * v_b.countMinBucketADJ[i][j];
            }
            if (dotProductab < minDotProductab)
                minDotProductab = dotProductab;
        }
        for (int i = 0; i < sketchDepth; i++) {
            double dotProductba = 0;
            for (int j = 0; j < sketchWidth; j++) {
                dotProductba = dotProductba + v_a.countMinBucketNNeigh[i][j]
                        * v_b.countMinBucketUtility[i][j];
            }
            if (dotProductba < minDotProductba)
                minDotProductba = dotProductba;
        }
        if (minDotProductab < minDotProductba)
            minDotProduct = minDotProductab;
        else minDotProduct = minDotProductba;
        return minDotProduct;
    }
    public static double computeScore_utility(Pair p) {

        Vertex vertexa = G.vertexes.get(p.getNode1());
        Vertex vertexb = G.vertexes.get(p.getNode2());

        double  approx_sameneighs=0,approx_sameutility=0;
        double score_U=0, score_att=0;

        approx_sameutility=approxDotProductUtility(p);
        score_att=compute_score_attribute(p);
        approx_sameneighs=approxDotProductNumneighs(p);
        score_U=2*approx_sameutility/(vertexa.U_xi+vertexb.U_xi);
        //System.out.println("approx_sameutility"+approx_sameutility+"vertexa.U_xi"+vertexa.U_xi+"vertexb.U_xi"+vertexb.U_xi);
        return score_U+score_att;
    }

    public static void computenodedegree(){
        int[] n=G.getNodes();
        for (int i = 0; i<n.length; i++) {
            Degree.put(n[i],G.getneighbors(n[i]).size());

        }
    }


    public static void sortByColumn(double arr[][], int col){

        Arrays.sort(arr, new Comparator<double[]>() {
            @Override
            // Compare values according to columns
            public int compare(final double[] entry1,
                               final double[] entry2) {
                // To sort in ascending order revert
                // the '<' Operator
                if (entry1[2] > entry2[2])
                    return -1;
                else if(entry1[2] < entry2[2])
                    return 1;
                else
                    return 0;
            }
        });  // End of function call sort().


    }
    public static void main(String [] args){
        vistnodes=new HashMap<Integer,Boolean>();
        try {
            //inputs
            maxid=0;
            parameterType = "u";
            /// Graph that is edge list in txt format
            //typcentrality="pr";
            // parameterType = "rn";
            filename = args[0];
            //filename="email.txt";
            //filename="com-youtube.ungraph.txt";
            //filename="cnr-2000.txt";
            //filename="hyves.txt";
            //filename="com-lj.ungraph.txt";
            //filename = "pblog.txt";
            //filename="coauthor.txt";
           //filename="CA-HepTh.txt";
           // filename_attr="CA-HepTh.txt";
            //NODE_BC_BASENAME = directory +args[1];
            //NODE_BC_BASENAME = directory +"8_prcentrality.txt";
            //filename="CA-AstroPh.txt";
            //filename="CA-GrQc.txt";
           //filename="com-amazon.ungraph.txt";
            //filename="Facebook.txt";

            ///Centrality of nodes of Graph in txt format contains: node centrality_value
            NODE_BC_BASENAME = directory +args[1];
            //NODE_BC_BASENAME = directory +"email_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"com-youtube.ungraph_prcentrality.txt";
            // NODE_BC_BASENAME = directory +"cnr-2000_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"hyves_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"com-lj.ungraph_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"pblog_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"coauthor_prcentrality.txt";
           // NODE_BC_BASENAME = directory +"CA-HepTh_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"CA-AstroPh_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"CA-GrQc_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"com-amazon.ungraph_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"Facebook_prcentrality.txt";
            //NODE_BC_BASENAME = directory +"CA-AstroPh_prcentrality.txt";

            ///Threshold of utility
            threshold = Double.parseDouble(args[2]);
            //threshold = 0.8;
            ///type of centrality measure
            typcentrality=args[3];
            //typcentrality="pr";
            /// File in txt format contains node attributes: node   attr1   attr2   ...
            //filename_attr="Facebook_attribute.txt";
            //filename_attr="email_attribute.txt";
            //filename_attr="pblog_attribute.txt";
            //filename_attr="coauthor_attribute.txt";
           // attrebuted_g=1;
            if (args.length > 4 && args[4] != null && !args[4].isEmpty()) {

                filename_attr=args[4];
                attrebuted_g=1;
            }


            GRAPH_BASENAME=directory + filename ;

            System.out.println(new File(".").getAbsolutePath());
            G= new grapharray();
            G.readgraph(AUSC.GRAPH_BASENAME);
            Degree=new HashMap<Integer, Integer>();
            t1 = System.currentTimeMillis();
            ///dimension of CountMinSketch matrix for computing similarity approximately
            sketchWidth=3;
            sketchDepth=2;
            sketch = new CountMinSketch(sketchWidth, sketchDepth);
            computenodedegree();
            ///Reading centrality of nodes
            readNodeIS();
            if (attrebuted_g==1){
               G.fillNodeAttributes(directory + filename_attr );
               G.fillvertexes_withattr(nodeIS);
               attrbutelen=G.attr_len;
                purityattrs=new double[attrbutelen];
            }
            else{
                G.fillvertexes(nodeIS);
            }
            weightspurious = 1.0 / (G.numNodes() * (G.numNodes()-1) - G.numedges);

            ///Build Tree for sampling
            SamplingTreeRoot = buildSamplingTree(0, G.numNodes()- 1);
            ///Max num of samples is |V|-1
            int numsamples=(int)(G.numNodes()-1);
            ufmerge=new UnionFind(G.maxid+1);
            System.out.println("numsamples"+numsamples);
            /// Sampling: Find pairs
            ArrayList<Pair> candidatepairs = samplePairsWeighted_Treebased(numsamples);
            numsamples=candidatepairs.size();
            System.out.println("numsamples"+numsamples);

            selectedpairs=new double[numsamples][3];
            Iterator sampleItr = candidatepairs.iterator();
            int currentpair=0;
            while (sampleItr.hasNext()) {
                Pair p = (Pair) sampleItr.next();
                if (p.getNode1()==-1 || p.getNode2()==-1) {

                    continue;
                }
                double score_ut=computeScore_utility(p);
                double score_final=score_ut;
                if (attrebuted_g==1){
                    score_final=0.5*score_final+0.5*compute_score_attribute(p);
                }
                selectedpairs[currentpair][0]=p.getNode1();
                selectedpairs[currentpair][1]=p.getNode2();
                selectedpairs[currentpair++][2]=score_final;

            }
            /// Sort selected pairs
            sortByColumn(selectedpairs, 2);

            VC2 =G.numNodes()*1.0*(G.numNodes()-1)/2;
            //summarizing

            uf=new UnionFind(G.maxid+1);

            System.out.println("initialized union find");


            int left,right,mid;
            double utility = 1;

            if(parameterType.equals("rn")){
                left = (int)(threshold * G.numNodes());
                utility=Summarizesamevalue(left);

            }
            else{
                /// Find number of merges to achieve the threshold utility
                right= currentpair;
               // System.out.println("right : "+right);
                left=0;

                while(left<right){
                    mid=(left+right)/2;

                    utility=Summarizesamevalue(mid);

                    if(utility<threshold){
                        right=mid-1;

                    }
                    else{
                        left=mid+1;

                    }
                }
            }

            System.out.print("utility : "+utility);
            System.out.print("middle : "+ left);
            System.out.println("rn : " + left*1.0/G.numNodes());
            System.out.println("Time elapsed for summarization is: " + (System.currentTimeMillis() - t1)/1000);
            System.out.println("Saving into file: Be Patient Please:");
            /// Save the graph
            if (attrebuted_g==1){
                mySaveinMemsamevaluewithattr(left);
            }
            else{
                mySaveinMemsamevalue(left);
            }
            reductionEdges=1-((double) numsuperedge/((double)G.numedges));
            System.out.print("numsuperedge : "+ numsuperedge);
            System.out.println("((double)G.twicenumedges/2) : "+ ((double)G.numedges));
            System.out.print("reductionEdges : "+ reductionEdges);
            System.out.println(" G.numNodes() : "+ G.numNodes());
            System.out.println("reductionnodes : " + (1-((double) realnumSuperNodes/(double)G.numNodes())));
            System.out.println("finalpurityattrs :" + finalpurityattrs);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private static double Summarizesamevalue(int index){
        uf.reset();
        int nseCount = 0 , seCount = 0;
        double sum = 0,nseError = 0 , seError = 0;
        int n = G.numNodes();

        for(int i=0;i<index;i++){

            int u=(int)selectedpairs[i][0];
            int v=(int)selectedpairs[i][1];
            uf.union(u,v);
        }
        int numSuperNodes=uf.renameSuperNodes();

        SuperNode[] superNode = uf.getSuperNodes();
        map = new TreeMap<Integer,Double>();
        int u,SuName,SvName,SuTempName,Auv;

        double utility=1;
        //sumnoralizedError=0;
        double value;
        double seCost,nseCost;

        for(SuTempName=0;SuTempName<numSuperNodes;SuTempName++){
            SuName=uf.getOriginalName(SuTempName);

            if (G.allVertices[(int)SuName][0]==(-1)){ continue;}
            List<Integer> listnodesSU = uf.getnodesofsupernode(superNode[SuName]);
            //System.out.println("listnodesSU"+listnodesSU);
            if (listnodesSU.size()==0){ continue;}
            //khodam
            Iterator<Integer> current=superNode[SuName].getNodes().listIterator();
            //System.out.println("current"+current);
            while(current.hasNext()){
                u=current.next();

                for(int v:G.getneighbors(u)){

                    SvName = uf.find(v);
                    if(map.containsKey(SvName)){
                        map.put(SvName,map.get(SvName)+(nodeIS.get(u)/Degree.get(u) + nodeIS.get(v)/Degree.get(v))+ 1);
                      }
                    else{
                         map.put(SvName,nodeIS.get(u)/Degree.get(u) + nodeIS.get(v)/Degree.get(v)+ 1);
                       }
                    //System.out.println("map.get(SvName) "+map.get(SvName)+" SuName "+SuName+" SvName "+SvName);
                }

            }

            for(Map.Entry<Integer,Double> entry : map.entrySet()) {
                SvName = entry.getKey();
                value = entry.getValue();
                Auv=(int)value;
                nseCost=value-Auv;
                nseError=Auv/( VC2);
                // System.out.println("SvName "+SvName+" SuName "+SuName+" value "+value+" Auv "+Auv);
                if(SuName==SvName){
                    nseCost/=2;

                    seCost=  Math.abs(((1.0*uf.getSize(SuName)*(uf.getSize(SuName)-1)/2) - Auv/2)/( VC2-G.numedges /2));
                    seError=  Math.abs(((1.0*uf.getSize(SuName)*(uf.getSize(SuName)-1)/2) - Auv/2)/( VC2));

                    if((seCost+seError)<=(nseCost+nseError)){
                        utility-=seCost;

                        seCount++;

                    }else if ((seCost+seError)>=(nseCost+nseError)){
                        utility-=nseCost;

                        nseCount++;

                    }

                }
                else{


                    seCost=  Math.abs((1.0*uf.getSize(SuName)*uf.getSize(SvName)-Auv)/(VC2-G.numedges /4));
                    seError=  Math.abs((1.0*uf.getSize(SuName)*uf.getSize(SvName)-Auv)/(VC2));
                    if((seCost+seError)<(nseCost+nseError)){
                        seCount++;
                        //System.out.println("(seCost+seError)<(nseCost+nseError) sp  not same: "+utility);
                        utility-=seCost;

                    }else{
                        utility-=nseCost;
                        //System.out.println("(seCost+seError)>=(nseCost+nseError) : "+utility);
                        nseCount++;
                     }
                }
           }
            map.clear();

        }

        return utility;
    }
    private static void mySaveinMemsamevalue(int index ) throws Exception{

        histogramsizsuprnodes = new HashMap<Integer,Integer>();
        psSU=new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_supernodes-"+parameterType+"-"+threshold+ ".txt"));
        psSE = new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_superneighbors-"+ parameterType+"-"+threshold+".txt"));
        PrintStream ps2_t = new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_superneighbors-"+ parameterType+"-"+threshold + "-t"+".txt"));
        psresult = new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_result-"+parameterType+"-"+threshold+".txt"));
        numsuperedge=0;
        uf.reset();
        int n = G.numNodes();
        System.out.println("index"+index);
        for(int i=0;i<index;i++){

            int u=(int)selectedpairs[i][0];
            int v=(int)selectedpairs[i][1];
            uf.union(u,v);
       }
        int numSuperNodes=uf.renameSuperNodes();

        SuperNode[] superNode = uf.getSuperNodes();
        //System.out.println("save superNode.length "+superNode.length);
        map = new TreeMap<Integer,Double>();
        int u,SuName,SvName,SuTempName,SvTempName,Auv;
        double utility=1;
        double value;
        sumnoralizedError=0;
        double nseError = 0 , seError = 0;
        double seCost,nseCost;
        HashMap<Integer,Integer> newName = new HashMap<Integer,Integer>();
        int count = 0;
        for(SuTempName=0;SuTempName<numSuperNodes;SuTempName++){
            SuName=uf.getOriginalName(SuTempName);

            if (G.allVertices[(int)SuName][0]==(-1)){ continue;}
            newName.put(SuName,count);
            count++;
            if(histogramsizsuprnodes.containsKey(superNode[SuName].getNodes().size()))
            {
                //System.out.println("contain superNode[SuName].getNodes().size() "+superNode[SuName].getNodes().size());
                histogramsizsuprnodes.put(superNode[SuName].getNodes().size(),histogramsizsuprnodes.get(superNode[SuName].getNodes().size())+1);
            }
            else
            { //System.out.println("no contain superNode[SuName].getNodes().size() "+superNode[SuName].getNodes().size());
                histogramsizsuprnodes.put(superNode[SuName].getNodes().size(), 1);
            }

        }

        for(SuTempName=0;SuTempName<numSuperNodes;SuTempName++){
            SuName=uf.getOriginalName(SuTempName);
            if (G.allVertices[(int)SuName][0]==(-1)){continue;}

            Iterator<Integer> current=superNode[SuName].getNodes().listIterator();

            psSU.print(newName.get(SuName));
            realnumSuperNodes++;

            while(current.hasNext()){

                u=current.next();

                psSU.print( "\t" + u);
                //System.out.println("save G.getsuccessor(u) "+G.getsuccessor(u));
                for(int v:G.getneighbors(u)){

                    SvName = uf.find(v);
                    if(map.containsKey(SvName)){
                        map.put(SvName,map.get(SvName)+(nodeIS.get(u)/Degree.get(u) + nodeIS.get(v)/Degree.get(v))+1);

                    }
                    else{
                        map.put(SvName,nodeIS.get(u)/Degree.get(u) + nodeIS.get(v)/Degree.get(v) + 1);

                    }
                }

            }
            psSU.println();

            for(Map.Entry<Integer,Double> entry : map.entrySet()) {
                SvName = entry.getKey();
                value = entry.getValue();
                Auv=(int)value;
                nseCost=value-Auv;
                nseError=Auv/( VC2);

                int sumvaluealledges = 0;
                if(SuName==SvName){
                    nseCost/=2;

                    seError=  Math.abs(((1.0*uf.getSize(SuName)*(uf.getSize(SuName)-1)/2) - Auv/2)/( VC2));
                    seCost=  Math.abs(((1.0*uf.getSize(SuName)*(uf.getSize(SuName)-1)/2)-Auv/2)/(VC2-G.numedges /2));

                    if((seCost+seError)<(nseCost+nseError)){
                        utility-=seCost;
                        sumnoralizedError+=seError;
                        if (newName.get(SuName)<= newName.get(SvName)){
                            psSE.println(newName.get(SuName) + "\t" + newName.get(SvName));
                            numsuperedge++;
                       }
                        else if (newName.get(SuName)>= newName.get(SvName))
                            ps2_t.println(newName.get(SuName) + "\t" + newName.get(SvName));
                    }else{
                        utility-=nseCost;
                        sumnoralizedError+=nseError;
                       }

                }
                else{

                    seCost= (1.0*uf.getSize(SuName)*uf.getSize(SvName)-Auv)/(VC2-G.numedges/2);
                    seError=  Math.abs((1.0*uf.getSize(SuName)*uf.getSize(SvName)-Auv)/(VC2));

                    if((seCost+seError)<(nseCost+nseError)){
                        utility-=seCost;
                        sumnoralizedError+=seError;
                       if (newName.get(SuName)<= newName.get(SvName)){
                            psSE.println(newName.get(SuName) + "\t" + newName.get(SvName));
                            numsuperedge++;
                          }
                        else if (newName.get(SuName)>= newName.get(SvName))
                            ps2_t.println(newName.get(SuName) + "\t" + newName.get(SvName));

                    }else{
                        utility-=nseCost;
                        sumnoralizedError+=nseError;

                    }
                }
                sumsecost+=seCost;
                sumnsecost+=nseCost;

            }
            map.clear();
            //System.out.println(" sumnoralizedError after suname "+sumnoralizedError+" utility "+utility+" suname"+SuName+"index"+index);
        }

        savehistogram();
        psresult.println("rn : " + index*1.0/G.numNodes());
        psresult.println("reductionnodes : " + (1-((double) realnumSuperNodes/(double)G.numNodes())));
        psresult.println("utility :" + utility);
        System.out.println("utility"+utility);
        psresult.println("sumnoralizedError :" + sumnoralizedError);
        reductionEdges=1-((double) numsuperedge/((double)G.numedges));
        psresult.println("reductionEdges :" + reductionEdges);
        psresult.println("numsuperedge :" + numsuperedge);
        psresult.println("numSuperNodes :" + realnumSuperNodes);
        psresult.println("finalpurityattrs :" + finalpurityattrs);
        psresult.println("Time elapsed for summarization is: " + (System.currentTimeMillis() - t1)/1000);

        psSU.flush();
        psSE.flush();
        ps2_t.flush();
        psresult.flush();
        psSU.close();
        psSE.close();
        ps2_t.close();
        psresult.close();
    }

    private static void mySaveinMemsamevaluewithattr(int index ) throws Exception{

        histogramsizsuprnodes = new HashMap<Integer,Integer>();
        psSU=new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_supernodes-"+parameterType+"-"+threshold+ ".txt"));
        psSUattrs=new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_supernodesattrs-"+parameterType+"-"+threshold+ ".txt"));
        psSE = new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_superneighbors-"+ parameterType+"-"+threshold+".txt"));
        PrintStream ps2_t = new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_superneighbors-"+ parameterType+"-"+threshold + "-t"+".txt"));
        psresult = new PrintStream(new File("files/EfficientScore/"  + filename.substring(0,filename.length()-4) + typcentrality+"_result-"+parameterType+"-"+threshold+".txt"));
        numsuperedge=0;
        uf.reset();
        int n = G.numNodes();
        System.out.println("index"+index);
        for(int i=0;i<index;i++){

            int u=(int)selectedpairs[i][0];
            int v=(int)selectedpairs[i][1];
            uf.union(u,v);
        }
        int numSuperNodes=uf.renameSuperNodes();

        SuperNode[] superNode = uf.getSuperNodes();
        map = new TreeMap<Integer,Double>();
        int u,SuName,SvName,SuTempName,SvTempName,Auv;
        double utility=1;
        double value;
        sumnoralizedError=0;
        double nseError = 0 , seError = 0;
        double seCost,nseCost;
        HashMap<Integer,Integer> newName = new HashMap<Integer,Integer>();
        int count = 0;
        HashMap<Integer, int[]> SupernodeVectorsarr=new HashMap<Integer, int[]>();;
        HashMap<Integer, double[]> SupernodeVectorsarrPurity=new HashMap<Integer, double[]>();
        HashMap<Integer, String[]> SupernodeVectorsDominantAttr=new HashMap<Integer, String[]>();
        Map<String, Map<Integer, Map<Integer, Integer>>> resultattr =
                new HashMap<>();
        for(SuTempName=0;SuTempName<numSuperNodes;SuTempName++){
            SuName=uf.getOriginalName(SuTempName);

            if (G.allVertices[(int)SuName][0]==(-1)){ continue;}
            newName.put(SuName,count);
            List<Integer> listnodesSU = uf.getnodesofsupernode(superNode[SuName]);
            Map<Integer, Map<Integer, Integer>> featureMap = new HashMap<>();
            // initialize
            for (int f = 0; f < G.attr_len; f++) {
                featureMap.put(f, new HashMap<>());
            }

            for (int current_node : listnodesSU) {
                int[] attr_current_node=G.vertexes.get(current_node).getAttribute_v();
                for (int f = 0; f < G.attr_len; f++) {
                    int cat = attr_current_node[f];
                    Map<Integer, Integer> catCounts = featureMap.get(f);
                    catCounts.put(cat, catCounts.getOrDefault(cat, 0) + 1);
                }
            }
            resultattr.put(String.valueOf(SuName), featureMap);

            int[] superNodeFeatureVector = new int[G.attr_len];

            String[] superNodedominantCategoryPerFeature = new String[G.attr_len];
            double[] superNodeFeatureVectorPurity = new double[G.attr_len];
            for (int f = 0; f < G.attr_len; f++) {
                Map<Integer, Integer> countsForF = featureMap.get(f);
                int maxCat = -1;
                int maxCount = 0;
                for (Map.Entry<Integer, Integer> e : countsForF.entrySet()) {
                    int cat = e.getKey();
                    int cnt = e.getValue();
                    //max frequency
                    if (cnt > maxCount) {
                        maxCount = cnt;
                        maxCat = cat;
                    }
                }
                //max value for feature of supernode
                superNodeFeatureVector[f] = maxCat;
                superNodeFeatureVectorPurity[f] = (listnodesSU.size() > 0) ? ((double) maxCount / listnodesSU.size()) : 0.0;
                purityattrs[f]=purityattrs[f]+maxCount ;
                if (maxCount > 0 && maxCat != -1) {
                    superNodedominantCategoryPerFeature[f] = G.inverseCategoryMaps.get(f).get(maxCat);
                } else {
                    superNodedominantCategoryPerFeature[f] = null; // or "NONE"
                }

            }

            SupernodeVectorsarr.put((SuName), superNodeFeatureVector);
            SupernodeVectorsarrPurity.put((SuName), superNodeFeatureVectorPurity);
            SupernodeVectorsDominantAttr.put((SuName), superNodedominantCategoryPerFeature);

            //System.out.println("Supernode " + SuName + " vector: " + Arrays.toString(superNodeFeatureVector));
           // System.out.println("Supernode " + SuName + " purity: " + Arrays.toString(superNodeFeatureVectorPurity));
           // System.out.println("Supernode " + SuName + " domincat: " + Arrays.toString(superNodedominantCategoryPerFeature));

            psSUattrs.println("Supernode " + count + " vector: " + Arrays.toString(superNodeFeatureVector));
            psSUattrs.println("Supernode " + count + " purity: " + Arrays.toString(superNodeFeatureVectorPurity));
            psSUattrs.println("Supernode " + count + " domincat: " + Arrays.toString(superNodedominantCategoryPerFeature));
            count++;
            if(histogramsizsuprnodes.containsKey(superNode[SuName].getNodes().size()))
            {
                //System.out.println("contain superNode[SuName].getNodes().size() "+superNode[SuName].getNodes().size());
                histogramsizsuprnodes.put(superNode[SuName].getNodes().size(),histogramsizsuprnodes.get(superNode[SuName].getNodes().size())+1);
            }
            else
            { //System.out.println("no contain superNode[SuName].getNodes().size() "+superNode[SuName].getNodes().size());
                histogramsizsuprnodes.put(superNode[SuName].getNodes().size(), 1);
            }

        }
        for(SuTempName=0;SuTempName<numSuperNodes;SuTempName++){
            SuName=uf.getOriginalName(SuTempName);

            if (G.allVertices[(int)SuName][0]==(-1)){continue;}

            Iterator<Integer> current=superNode[SuName].getNodes().listIterator();

            psSU.print(newName.get(SuName));
            realnumSuperNodes++;

            while(current.hasNext()){
                u=current.next();

                psSU.print( "\t" + u);

                for(int v:G.getneighbors(u)){

                    SvName = uf.find(v);
                    if(map.containsKey(SvName)){
                        map.put(SvName,map.get(SvName)+(nodeIS.get(u)/Degree.get(u) + nodeIS.get(v)/Degree.get(v))+1);

                    }
                    else{
                        map.put(SvName,nodeIS.get(u)/Degree.get(u) + nodeIS.get(v)/Degree.get(v) + 1);
                    }
                }

            }
            psSU.println();

            for(Map.Entry<Integer,Double> entry : map.entrySet()) {
                SvName = entry.getKey();
                value = entry.getValue();
                Auv=(int)value;
                nseCost=value-Auv;
                nseError=Auv/( VC2);

                if(SuName==SvName){
                    nseCost/=2;

                    seError=  Math.abs(((1.0*uf.getSize(SuName)*(uf.getSize(SuName)-1)/2) - Auv/2)/( VC2));
                    seCost=  Math.abs(((1.0*uf.getSize(SuName)*(uf.getSize(SuName)-1)/2)-Auv/2)/(VC2-G.numedges /2));

                    if((seCost+seError)<(nseCost+nseError)){
                        utility-=seCost;
                        sumnoralizedError+=seError;
                        if (newName.get(SuName)<= newName.get(SvName)){
                            psSE.println(newName.get(SuName) + "\t" + newName.get(SvName));
                            numsuperedge++;
                          }
                        else if (newName.get(SuName)>= newName.get(SvName))
                            ps2_t.println(newName.get(SuName) + "\t" + newName.get(SvName));
                    }else{
                        utility-=nseCost;
                        sumnoralizedError+=nseError;
                        }

                }
                else{

                    seCost= (1.0*uf.getSize(SuName)*uf.getSize(SvName)-Auv)/(VC2-G.numedges/2);
                    // seCost = (uf.getSize(SvName) * sp_total[SuName] + uf.getSize(SuTempName) * sp_total[SvName] - sp_count.get(SvName));
                    seError=  Math.abs((1.0*uf.getSize(SuName)*uf.getSize(SvName)-Auv)/(VC2));
                    //System.out.println("seError"+seError);
                    if((seCost+seError)<(nseCost+nseError)){
                        utility-=seCost;
                        sumnoralizedError+=seError;
                        if (newName.get(SuName)<= newName.get(SvName)){
                            psSE.println(newName.get(SuName) + "\t" + newName.get(SvName));
                            numsuperedge++;
                        }
                        else if (newName.get(SuName)>= newName.get(SvName))
                            ps2_t.println(newName.get(SuName) + "\t" + newName.get(SvName));

                    }else{
                        utility-=nseCost;
                        sumnoralizedError+=nseError;
                      }
                }
                sumsecost+=seCost;
                sumnsecost+=nseCost;

            }
            map.clear();
            //System.out.println(" sumnoralizedError after suname "+sumnoralizedError+" utility "+utility+" suname"+SuName+"index"+index);
        }

        finalpurityattrs=0;
        for (int i = 0; i < purityattrs.length; i++) {
            purityattrs[i] = purityattrs[i] / (double) G.numNodes();
            finalpurityattrs=finalpurityattrs+purityattrs[i];

        }
        finalpurityattrs=finalpurityattrs/attrbutelen;
        savehistogram();
        psresult.println("rn : " + index*1.0/G.numNodes());
        psresult.println("reductionnodes : " + (1-((double) realnumSuperNodes/(double)G.numNodes())));
        psresult.println("utility :" + utility);
        System.out.println("utility"+utility);
        psresult.println("sumnoralizedError :" + sumnoralizedError);
        reductionEdges=1-((double) numsuperedge/((double)G.numedges));
        psresult.println("reductionEdges :" + reductionEdges);
        psresult.println("numsuperedge :" + numsuperedge);
        psresult.println("numSuperNodes :" + realnumSuperNodes);
        psresult.println("Time elapsed for summarization is: " + (System.currentTimeMillis() - t1)/1000);
        psresult.println("finalpurityattrs :" + finalpurityattrs);
        psSU.flush();
        psSE.flush();
        ps2_t.flush();
        psresult.flush();
        psSU.close();
        psSE.close();
        ps2_t.close();
        psresult.close();
    }
    public static void savehistogram() throws FileNotFoundException {
        Iterator<Integer> keySetIterator = histogramsizsuprnodes.keySet().iterator();
        pshistsupernode = new PrintStream(new File("files/output/"  + filename + "_histogram-"+typcentrality+parameterType+"-"+threshold+".txt"));

        while(keySetIterator.hasNext()){

            Integer key= keySetIterator.next();
            pshistsupernode.println( key +  "\t"  + histogramsizsuprnodes.get(key));
        }
        pshistsupernode.flush();
        pshistsupernode.close();
    }
}

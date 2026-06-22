# AUSC: Utility‑Driven Graph Summarization

AUSC is a utility‑driven graph summarization method designed to compress large networks while preserving the usefulness of the original graph. The method constructs a compact summary by merging nodes into supernodes while maintaining important structural relationships.

AUSC can operate on both **attributed graphs** and **purely structural graphs**. When node attributes are available, they are incorporated into the merging process to improve attribute consistency within supernodes. If no attribute information is provided, AUSC performs summarization using only structural information.

The algorithm uses a sampling strategy together with an efficient candidate selection mechanism to identify promising node pairs for merging. This design avoids exhaustive comparisons and allows the method to scale to large graphs while maintaining the utility of the summarized representation.

---

# Input

The algorithm requires the following inputs:

- **Input graph**: an edge list file representing the original graph  
- **Node centrality file**: a file containing the centrality score of each node  
- **Utility threshold**: a value controlling the stopping condition of the summarization process  
- **Centrality type**: the type of centrality used (e.g., PageRank)  
- **Attribute file (optional)**: a file containing node attribute values  

If the graph does not contain node attributes, the attribute file can be omitted.

---

# Running AUSC

There are two ways to run the project.

## 1. Running with an IDE

The project can be imported into any Java IDE such as **IntelliJ IDEA**, **Eclipse**, or **NetBeans**. After importing the project, run the **AUSC main class** from the IDE.

## 2. Running from Command Line

The compiled classes are located in:

AUSC/out/production/AUSC/AUSC

Run the program using:

java AUSC inputgraph centrality_of_nodes threshold centrality_type attribute_file
---

# Program Arguments

The program reads the following arguments:

filename = args[0]

centrality_file = args[1]

threshold = Double.parseDouble(args[2])

centrality_type = args[3]

attribute_file = args[4]

Meaning of parameters:

- **inputgraph**: edge list file of the graph  
- **centrality_of_nodes**: centrality score of each node  
- **threshold**: utility threshold (a value between 0 and 1)  
- **centrality_type**: type of centrality used (e.g., pagerank_centrality)  
- **attribute_file**: node attribute file (optional)

---

# Examples

### Graph with attributes

email.txt email_prcentrality.txt 0.8 pagerank_centrality email_attribute.txt

### Graph without attributes

email.txt email_prcentrality.txt 0.8 pagerank_centrality


If the graph does not contain node attributes, simply omit the last parameter.

---

# Output

The algorithm generates a summarized graph consisting of **supernodes** and **superedges**, providing a compact representation of the original network while preserving important structural properties.

---

# Citation

If you use this code in your research, please cite the corresponding paper describing the AUSC method.


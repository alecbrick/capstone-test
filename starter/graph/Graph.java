package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Graph {
    /* Creates a vertex with the given number. */
    public void addVertex(int num);
    
    /* Creates an edge from the first vertex to the second. */
    public void addEdge(int from, int to);

    /* Finds the egonet centered at a given node. */
    public Graph getEgonet(int center);

    /* Returns all SCCs in a directed graph. Don't worry about handling
     * this for undirected graphs. */
    public List<Graph> getSCCs();
    
    /* Return the graph's connections in a readable format. */
    public HashMap<Integer, HashSet<Integer>> exportGraph();
} 

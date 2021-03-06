/**
 * @author UCSD MOOC development team
 * 
 * Interface for a basic Graph class that uses integers 
 * to represent vertices.
 *
 */
package graph;

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
    public List<Set<Integer>> getSCCs();
    
    /* Return the graph's connections in a printable format. */
    public String printGraph();
} 

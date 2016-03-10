package graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import util.GraphLoader;

public class CapGraph implements Graph {
    private Map<Integer, Vertex> vertices;
    
    public CapGraph() {
        vertices = new HashMap<Integer, Vertex>();
    }

    /** Given a list of vertices from an old graph,
     *  creates a new graph containing only those vertices.
     */
    public CapGraph(List<Vertex> allowed) {
        vertices = new HashMap<Integer, Vertex>();
        for (Vertex v : allowed) {
            vertices.put(v.getVal(), new Vertex(v, allowed));
        }
    }

    public boolean contains(int i) {
        return vertices.containsKey(i);
    }

    public boolean insert(int i) {
        if (this.contains(i)) {
            return false;
        }
        Vertex v = new Vertex(i);
        vertices.put(i, v);
        return true;
    }

    public void addVertex(int i) {
        insert(i);
    }


    public void addEdge(int from, int to) {
        this.getVertex(from).addEdge(this.getVertex(to));
    }

    public Vertex getVertex(int i) {
        return vertices.get(i);
    }

    public static void printGraph(CapGraph graph) {
        Map<Integer, Vertex> vertices = graph.getVertices();
        
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            System.out.print("\n"+v.getVal() + " : ");
            
            for(Edge edge : v.getEdges()) {
                System.out.print(edge.getOtherVertex(v).getVal() + " ");
            }
            System.out.print("\n");
        }
    }

    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        HashMap<Integer, HashSet<Integer>> ret = new HashMap<Integer, HashSet<Integer>>();
        for (Map.Entry<Integer, Vertex> pair : vertices.entrySet()) {
            HashSet<Integer> val = new HashSet<Integer>();
            List<Edge> edges = pair.getValue().getEdges();
            for (Edge e : edges) {
                val.add(e.getV2().getVal());
            }
            ret.put(pair.getKey(), val);
        }
        return ret;
    }
    
    public static void printList(List<Vertex> lst) {
        System.out.print("[");
        for (Vertex v : lst) {
            System.out.print(v.getVal() + ", ");
        }
        System.out.println("]");
    }


    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

    // NETWORK PARTITIONING =======================================

    // BFS through the graph, keeping track of path count and layer.
    public List<Vertex> bfs(Vertex start) {
        ArrayList<Vertex> ret = new ArrayList<Vertex>();

        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(start);
        start.pathCount = 1;

        while (!q.isEmpty()) {
            Vertex curr = q.remove();
            if (!curr.visited) {
                ret.add(curr);
                curr.visited = true;
                for (Edge e : curr.getEdges()) {
                    Vertex neighbor = e.getOtherVertex(curr);
                    if (!neighbor.visited) {
                        // The layer is the shortest distance to the
                        // starting vertex.
                        if (neighbor.layer == 0) {
                            neighbor.layer = curr.layer + 1;
                        }
                        if (neighbor.layer > curr.layer) {
                            neighbor.parents.add(curr);
                            neighbor.pathCount += curr.pathCount;
                        }
                        q.add(neighbor);
                    }
                }
            }
        }
        return ret;
    }

    // Find the flow for each vertex and edge for this iteration.
    public void findFlow() {
        // so in-place sort doesn't mess up old order
        List<Vertex> vertexCopy = new ArrayList<Vertex>(((HashMap<Integer, Vertex>)vertices).values());
        Collections.sort(vertexCopy);
        for (Vertex v : vertexCopy) {
            for (Vertex p : v.parents) {
                float flowAdd = v.flow * (float)(p.pathCount / v.pathCount);
                p.flow += flowAdd;
                Edge e = v.getEdge(p);
                e.flow += flowAdd;
            }
        }
    }

    // Find the most-travelled edges on the graph, and remove them.
    public void findPartitions() {
        // Calculate flow from each vertex
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            resetVertices();
            bfs(v);
            findFlow();
        }

        float max = 0;
        Edge maxEdge = null;

        // Find the edge with the highest flow
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            for (Edge e : v.getEdges()) {
                if (e.flow > max) {
                    maxEdge = e;
                    max = e.flow;
                }
            }
        }

        // partition
        Edge e = maxEdge;
        e.getV1().removeEdge(e.getV2());
        e.getV2().removeEdge(e.getV1());
    }

    // Clears vertex information such as visited, layer, etc.
    public void resetVertices() {
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            v.reset();
        }
    }

    // Clears flow information on edges
    public void resetEdges() {
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            for (Edge e : v.getEdges()) {
                e.setFlow(0);
            }
        }
    }

    // Remove edges from the graph in order to separate it into
    // "amount" subcommunities
    public void partition(int amount) {
        int count = countPartitions();
        while (count < amount) {
            count = 0;
            // Find and remove the highest-travelled edge
            findPartitions();
            resetEdges();
            count = countPartitions();
        }

        count = 0;
        resetVertices();
        // Print out connected components
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            if (!v.visited) {
                System.out.println("Vertex: " + v.getVal());
                count++;
                List<Vertex> ret = bfs(v);
                System.out.println("Partition #" + count + ": " + printListString(ret));
            }
        }
    } 

    // Count number of connected components in the graph
    public int countPartitions() {
        int count = 0;
        resetVertices();
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            // Do BFS from non-visited nodes as they are found
            if (!v.visited) {
                count++;
                bfs(v);
            }
        }
        return count;
    }

    public static void main(String[] args) {
        String filename = "facebook_1000.txt";
        int amount = 2;
        if (args.length > 0) {
            if (args[0].equals("--help")) {
                System.out.println("Usage:\n\tjava graph.CapGraph [[filename=test.txt] [partitions=2]]");
                return;
            }
            filename = args[0];
            if (args.length > 1) {
                amount = Integer.parseInt(args[1]);
            }
        }
        CapGraph g = new CapGraph();
        GraphLoader.loadGraph(g, "data/" + filename);
        g.partition(amount);
    }

    // EASY QUESTION ======================================
    public List<Vertex> findPossibleFriends (int vertex) {
        Vertex start = getVertex(vertex);
        List<Vertex> ret = new ArrayList<Vertex>();
        List<Vertex> neighbors = start.getNeighbors();
        resetVertices();
        // Loop through all the neighbors, finding neighbors of
        // neighbors who aren't friends with the starting vertex
        for (Vertex v : neighbors) {
            v.visited = true;
        }
        for (Vertex v : neighbors) {
            for (Vertex v2 : v.getNeighbors()) {
                if (!v2.visited && (v2 != start)) {
                    ret.add(v2);
                    v2.visited = true;
                }
            }
        }
        System.out.println("Neighbors size: " + neighbors.size());
        System.out.println("Ret size: " + ret.size());
        return ret;
    }
    // ===========================================================


    public static String printListString (List<Vertex> lst) {
        String ret = "";
        for (int i = 0; i < lst.size(); i++) {
            ret += lst.get(i).getVal();
            if (i < lst.size() - 1) {
                ret += ", ";
            }
        }
        return ret;
    }

    public static Vertex getLargestDegree(List<Vertex> lst, int start) {
        int max = 0;
        Vertex curr = null;
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).degree() > max && lst.get(i).getVal() != start) {
                curr = lst.get(i);
                max = curr.degree();
            }
        }
        return curr;
    }

    // EGONET FINDING ===============================================
    public Graph getEgonet(int center) {
        Vertex start = getVertex(center);
        List<Vertex> friends = new ArrayList<Vertex>();
        for (Edge e : start.getEdges()) {
            friends.add(e.getV2());
        }
        return new CapGraph(friends);
    }


    // SCC FINDING ===================================================

    public List<Set<Integer>> getSCCs() {
        List<Set<Integer>> sccs = new ArrayList<Set<Integer>>();
        CapGraph gReverse = CapGraph.getReverseGraph(this);
        List<Vertex> postList = new ArrayList<Vertex>();

        gReverse.dfs(postList);

        this.resetVertices();

        for(Vertex vertex : postList) {
            // maybe use getVertex
            if(!getVertex(vertex.getVal()).visited) {
                sccs.add(findSCC(vertex.getVal()));
            }
        }

        return sccs;
    }

    public static Comparator<Vertex> valComparator = new Comparator<Vertex>() {
        public int compare(Vertex v1, Vertex v2) {
            return v1.getVal() - v2.getVal();
        }
    };

    // finds SCC given a valid vertex
    private Set<Integer> findSCC(int val) {
        

        Set<Integer> scc = new TreeSet<Integer>();
        Stack<Vertex> stack = new Stack<Vertex>();
        Vertex working = null;
        
        stack.push(getVertex(val));
        while(!stack.empty()) {
            working = stack.pop();

            if(!working.visited) {
                working.visited = true;
                
                scc.add(working.getVal());


                for(Edge edge : working.getEdges()) {
                    if(!edge.getOtherVertex(working).visited) {
                        stack.push(edge.getOtherVertex(working));
                    }
                }
            }
        }


        return scc;
    }

    public static CapGraph getReverseGraph(CapGraph g) {
        CapGraph gReverse = new CapGraph();
        Vertex other  = null;

        for (Map.Entry<Integer, Vertex> entry : g.getVertices().entrySet()) {
            Vertex v = entry.getValue();
            gReverse.insert(v.getVal());
            
            for(Edge edge : v.getEdges()) {
                other = edge.getOtherVertex(v);
                gReverse.insert(other.getVal());

                gReverse.getVertex(other.getVal())
                    .addEdge(gReverse.getVertex(v.getVal()));

            }       
        }

        return gReverse;
    }
        
    public void dfs(List<Vertex> postList) {
        resetVertices();
        Integer clk = 0;
        
        for (Map.Entry<Integer, Vertex> entry : getVertices().entrySet()) {
            Vertex v = entry.getValue();
            if(!v.visited) {
                explore(v, postList, clk);
            }
        }
    }

    /**
     * performs dfs explore from start Vertex
     * fills postList with vertices in order of decreasing post number
     *
     */
    public void explore(Vertex start, List<Vertex> postList, Integer clk) {
        Stack<Vertex> vStack = new Stack<Vertex>();


        Vertex working;

        // add start vertex to stack
        vStack.push(start);
        while(!vStack.empty()) {
            working = vStack.pop();

            if(!working.visited) {
                // push back on stack so post number will get set
                vStack.push(working);
                working.visited = true;
                working.pre = clk;
            }
            else if(working.post == 0) {

                working.post = clk;
                postList.add(0, working);
                
            }

            clk++;

            for(Edge edge : working.edges) {
                if(!edge.getOtherVertex(working).visited) {
                    vStack.push(edge.getOtherVertex(working));
                }
            }
        }       
    }





}

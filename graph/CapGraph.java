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
    
    public void readEdges(String file) {
        Scanner sc;
        try {
            sc = new Scanner(new File(file));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (sc.hasNextLine()) {
            String line = sc.nextLine().replaceAll(":", "");
            Scanner numSc = new Scanner(line);
            int i1 = numSc.nextInt();
            this.insert(i1);
            while (numSc.hasNextInt()) {
                int i2 = numSc.nextInt();
                this.insert(i2);
                this.getVertex(i1).addEdge(this.getVertex(i2));
            }
            numSc.close();
        }
        sc.close();
    }

    public double calculateDensity(List<Vertex> allowed) {
        int edges = 0;
        for (Vertex v : allowed) {
            edges += v.getEdgeCount(allowed);
        }
        return edges * 1.0 / allowed.size();
    }
            

    /**
     * Given a vertex, find the most dense subgraph containing it.
     * This measures the connectivity between vertices.
     * This is a greedy algorithm from On Finding Dense Subgraphs
     * by Khuller and Saha. Note that this is not necessarily the
     * largest subgraph, but it will come close.
     */
    public List<Vertex> largestDenseNetwork(int start) {
        return largestDenseNetwork(this.getVertex(start));   
    }

    public List<Vertex> largestDenseNetwork(Vertex start) {
        CapGraph g = new CapGraph(start.getNeighborsAndThis());
        List<Vertex> neighbors = new ArrayList<Vertex>(g.getVertices().values());
        Collections.sort(neighbors, Vertex.VertexComparator);
        List<Vertex> ret = new ArrayList<Vertex>();
        double density = 0;

        boolean first = false;
        for (Vertex v : neighbors) {
            ret.add(v);
            if (!first) {
                first = true;
                continue;
            }
            double currDensity = calculateDensity(ret);
            if (currDensity <= density) {
                ret.remove(v);
            } else {
                density = currDensity;
            }
        }

        for (int i = 0; i < ret.size(); i++) {
            ret.set(i, this.getVertex(ret.get(i).getVal()));
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

    public List<Vertex> findFriends(int start) {
        // Get dense network around start subgraph
        List<Vertex> startSubgraph = largestDenseNetwork(start);
        printList(startSubgraph);

        // Highest degree of start subgraph
        Vertex highest = getLargestDegree(startSubgraph, start);
        // Dense network around highest degree node
        List<Vertex> highSubgraph = largestDenseNetwork(highest);
        printList(highSubgraph);

        // Find the nodes that the start network doesn't have
        Set<Vertex> startSet = new HashSet<Vertex>(startSubgraph);
        Set<Vertex> highSet = new HashSet<Vertex>(highSubgraph);
        System.out.println(highSet.removeAll(startSet));
        return new ArrayList<Vertex>(highSet);
    }

    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

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
        System.out.println(e.getV1().getVal() + " to " + e.getV2().getVal() + ": " + e.flow);
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

    public void resetEdges() {
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            for (Edge e : v.getEdges()) {
                e.setFlow(0);
            }
        }
    }

    public void partition(int amount) {
        int count = countPartitions();
        while (count < amount) {
            count = 0;
            findPartitions();
            resetVertices();
            resetEdges();
            for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
                Vertex v = entry.getValue();
                if (!v.visited) {
                    count++;
                    bfs(v);
                }
            }
        }

        count = 0;
        resetVertices();
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

    public int countPartitions() {
        int count = 0;
        resetVertices();
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Vertex v = entry.getValue();
            if (!v.visited) {
                count++;
                bfs(v);
            }
        }
        return count;
    }

    public static void main(String[] args) {
        String filename = "facebook_ucsd.txt";
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
        //g.partition(amount);

        List<Set<Integer>> sccs = g.getSCCs();
        

        for(int i = 0; i < sccs.size(); i++) {
            Set<Integer> scc = sccs.get(i);
            System.out.print("scc " + (i+1) + " : ");
            for(Integer val : scc) {
                System.out.print( val + "  ");
            }
            System.out.print("\n");
        }
        

        /*List<Vertex> ret = g.findPossibleFriends(1);
        for (Vertex v : ret) {
            System.out.println(v.getVal());
        }*/

            


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

    public String printGraph() {
        // add vertices to tree map sorted by integer key values
        TreeMap<Integer, Vertex> newVertices = 
                new TreeMap<Integer, Vertex>(getVertices());
        String ret = "";

        for (Map.Entry<Integer, Vertex> entry : newVertices.entrySet()) {
            Vertex v = entry.getValue();
            ret += v.getVal() + ": ";
            List<Edge> newEdges = new ArrayList<Edge>(v.getEdges());
            Collections.sort(newEdges, new Comparator<Edge>() {
                @Override
                public int compare(Edge e1, Edge e2) {
                    return e1.getV2().getVal() - e2.getV2().getVal();
                }
            });

            for (int i = 0; i < newEdges.size(); i++) {
                ret += newEdges.get(i).getV2().getVal();
                if (i < newEdges.size() - 1) {
                    ret += ", ";
                }
            }
            ret += "\n";
        }
        return ret;
    }

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

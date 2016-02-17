package graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import util.GraphLoader;

public class CapGraph implements Graph {
    private List<Vertex> vertices;
    
    public CapGraph() {
        vertices = new ArrayList<Vertex>();
    }

    /** Given a list of vertices from an old graph,
     *  creates a new graph containing only those vertices.
     */
    public CapGraph(List<Vertex> allowed) {
        vertices = new ArrayList<Vertex>();
        for (Vertex v : allowed) {
            vertices.add(new Vertex(v, allowed));
        }
    }

    public boolean contains(int i) {
        for (Vertex v : vertices) {
            if (v.getVal() == i) {
                return true;
            }
        }
        return false;
    }

    public boolean insert(int i) {
        if (this.contains(i)) {
            return false;
        }
        Vertex v = new Vertex(i);
        vertices.add(v);
        return true;
    }

    public void addVertex(int i) {
        insert(i);
    }

    public void addEdge(int from, int to) {
        this.getVertex(from).addEdge(this.getVertex(to));
    }

    public Vertex getVertex(int i) {
        for (Vertex v : vertices) {
            if (v.getVal() == i) {
                return v;
            }
        }
        return null;
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
        List<Vertex> neighbors = g.getVertices();
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

    public List<Vertex> getVertices() {
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
        List<Vertex> vertexCopy = new ArrayList<Vertex>(vertices);
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
        for (Vertex v : vertices) {
            resetVertices();
            bfs(v);
            findFlow();
        }

        float max = 0;
        Edge maxEdge = null;

        for (Vertex v : vertices) {
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
        for (Vertex v : vertices) {
            v.reset();
        }
    }

    public void resetEdges() {
        for (Vertex v : vertices) {
            for (Edge e : v.getEdges()) {
                e.setFlow(0);
            }
        }
    }

    public void petition(int amount) {
        int count = countPartitions();
        while (count < amount) {
            count = 0;
            findPartitions();
            resetVertices();
            resetEdges();
            for (Vertex v : vertices) {
                if (!v.visited) {
                    count++;
                    bfs(v);
                }
            }
        }

        count = 0;
        resetVertices();
        for (Vertex v : vertices) {
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
        for (Vertex v : vertices) {
            if (!v.visited) {
                count++;
                bfs(v);
            }
        }
        return count;
    }

    public static void main(String[] args) {
        String filename = "test.txt";
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
        g.petition(amount);
            
    }

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
        List<Vertex> newVertices = new ArrayList<Vertex>(vertices);
        String ret = "";
        Collections.sort(newVertices, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                return v1.getVal() - v2.getVal();
            }
        });

        for (Vertex v : newVertices) {
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

    public List<Graph> getSCCs() {
        return new ArrayList<Graph>();
    }

    public static Graph getReverseGraph(Graph g) {
        Graph gReverse = new CapGraph();
        
        for(Vertex vertex : g.vertices) {

        }

        return gReverse;
    }
        

    public void dfs(List<Vertex> postList) {
        resetVertices();
        Integer clk = 0;
        
        for(Vertex vertex : vertices) {
            if(!vertex.visited) {
                explore(vertex, postList, clk);
            }
        }
    }

    /**
     * performs dfs explore from start Vertex
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
            else {
                if(working.post == 0) {
                    working.post = clk;
                    postList.add(working);
                }
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

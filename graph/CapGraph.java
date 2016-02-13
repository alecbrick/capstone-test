package graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    public void bfs(Vertex start) {
        for (Vertex v : vertices) {
            v.reset();
        }

        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(start);
        start.pathCount = 1;

        while (!q.isEmpty()) {
            Vertex curr = q.remove();
            if (!curr.visited) {
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
            bfs(v);
            findFlow();
        }

        Queue<Edge> q = new PriorityQueue<Edge>(10, new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2) {
                return (int)(e1.flow - e2.flow);
            }
        });

        for (Vertex v : vertices) {
            for (Edge e : v.getEdges()) {
                if (q.size() < 10) {
                    q.add(e);
                } else if (e.flow > q.peek().flow) {
                    q.poll();
                    q.add(e);
                }
            }
        }

        for (Edge e : q) {
            System.out.println(e.getV1().getVal() + " to " + e.getV2().getVal() + ": " + e.flow);
        }
    }


    public static void main(String[] args) {
        CapGraph g = new CapGraph();
        GraphLoader.loadGraph(g, "data/facebook_1000.txt");
        g.findPartitions();
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

    public Graph getEgonet(int center) {
        return new CapGraph();
    }

    public List<Graph> getSCCs() {
        return new ArrayList<Graph>();
    }
}

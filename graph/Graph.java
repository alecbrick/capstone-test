package graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Graph {
    private List<Vertex> vertices;
    
    public Graph() {
        vertices = new ArrayList();
    }

    /** Given a list of vertices from an old graph,
     *  creates a new graph containing only those vertices.
     */
    public Graph(List<Vertex> allowed) {
        vertices = new ArrayList();
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
        Graph g = new Graph(start.getNeighborsAndThis());
        List<Vertex> neighbors = g.getVertices();
        Collections.sort(neighbors, Vertex.VertexComparator);
        List<Vertex> ret = new ArrayList();
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
        List<Vertex> startSubgraph = largestDenseNetwork(start);
        printList(startSubgraph);
        Vertex highest = getLargestDegree(startSubgraph, start);
        List<Vertex> highSubgraph = largestDenseNetwork(highest);
        printList(highSubgraph);
        Set<Vertex> startSet = new HashSet<Vertex>(startSubgraph);
        Set<Vertex> highSet = new HashSet<Vertex>(highSubgraph);
        System.out.println(highSet.removeAll(startSet));
        return new ArrayList<Vertex>(highSet);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.readEdges("graph/0.egonet");
        List<Vertex> friends = g.findFriends(1);
        for (Vertex v : friends) {
            System.out.println(v.getVal());
        }
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
}

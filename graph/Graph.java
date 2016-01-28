package graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Graph {
    private List<Vertex> vertices;
    
    public Graph() {
        vertices = new ArrayList();
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
        while (sc.hasNext()) {
            
            int i1 = sc.nextInt();
            int i2 = sc.nextInt();
            this.insert(i1);
            this.insert(i2);
            this.getVertex(i1).addEdge(this.getVertex(i2));
        }
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.readEdges("0.edges");
        int highDegree = 0;
        Vertex highVertex = null;
        for (Vertex v : g.getVertices()) {
            if (v.degree() > highDegree) {
                highDegree = v.degree();
                highVertex = v;
            }
        }
        System.out.println("Highest vertex is " + highVertex.getVal() + " with a degree of " + highVertex.degree() + ".");
    }
}

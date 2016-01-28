package graph;

import java.util.List;
import java.util.ArrayList;

public class Vertex {
    List<Edge> edges;
    int val;

    public Vertex(int val) {
        this.val = val;
        edges = new ArrayList();
    }

    public int getVal() {
        return val;
    }

    @Override
    public boolean equals(Object v) {
        return val == ((Vertex) v).getVal();
    }

    public void addEdge(Vertex v) {
        Edge e = new Edge(this, v);
        boolean x = edges.add(e);
        v.addEdge(e);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public int degree() {
        return edges.size();
    }
}

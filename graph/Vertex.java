package graph;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class Vertex implements Comparable<Vertex> {
    List<Edge> edges;
    int val;

    public Vertex(int val) {
        this.val = val;
        edges = new ArrayList();
    }

    public Vertex(Vertex v, List<Vertex> allowed) {
        this.val = v.getVal();
        edges = new ArrayList();
        for (Edge e : v.getEdges()) {
            if (allowed.contains(e.getOtherVertex(v))) {
                edges.add(e);
            }
        }
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
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public int degree() {
        return edges.size();
    }

    public int getEdgeCount(List<Vertex> allowed) {
        int ret = 0;
        for (Edge e : edges) {
            if (allowed.contains(e.getOtherVertex(this))) {
                ret += 1;
            }
        }
        return ret;
    }

    public List<Vertex> getNeighbors() {
        ArrayList<Vertex> ret = new ArrayList();
        for (Edge e : edges) {
            ret.add(e.getOtherVertex(this));
        }
        return ret;
    }

    public List<Vertex> getNeighborsAndThis() {
        List<Vertex> ret = getNeighbors();
        ret.add(this);
        return ret;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int compareTo(Vertex v) {
        return v.getEdges().size() - edges.size();
    }

    public static Comparator<Vertex> VertexComparator = new Comparator<Vertex>() {
        public int compare(Vertex v1, Vertex v2) {
            return v1.compareTo(v2);
        }
    };
}

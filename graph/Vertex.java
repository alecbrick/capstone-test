package graph;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class Vertex implements Comparable<Vertex> {
    List<Edge> edges;
    int val;
    public int layer = 0;
    public List<Vertex> parents = new ArrayList<Vertex>();
    public boolean visited = false;
    public float flow = 1;
    public int pathCount = 0;

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

    public void reset() {
        layer = 0;
        parents = new ArrayList<Vertex>();
        visited = false;
        flow = 1;
        pathCount = 0;
    }

    public void visit() {
        visited = true;
    }

    public int getVal() {
        return val;
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

    public Edge getEdge(Vertex v) {
        for (Edge e : edges) {
            if (e.getOtherVertex(this) == v) {
                return e;
            }
        }
        throw new IllegalArgumentException("Edge not found!");
    }

    public int compareTo(Vertex v) {
        return v.layer - layer;
    }

    @Override
    public boolean equals(Object o) {
        return val == ((Vertex)o).getVal();
    }

    public static Comparator<Vertex> VertexComparator = new Comparator<Vertex>() {
        public int compare(Vertex v1, Vertex v2) {
            return v1.compareTo(v2);
        }
    };
}

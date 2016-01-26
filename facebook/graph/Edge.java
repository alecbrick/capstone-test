package graph;

public class Edge {
    Vertex v1;
    Vertex v2;

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public Vertex getOtherVertex(Vertex v) {
        if (v.equals(v1)) {
            return v2;
        } else if (v.equals(v2)) {
            return v1;
        } else {
            throw new IllegalArgumentException();
        }
    }
}

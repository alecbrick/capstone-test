package graph;

public class Edge {
    Vertex v1;
    Vertex v2;
    float flow = 0;

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public void setFlow(float f) {
        flow = f;
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

    @Override
    public boolean equals(Object o) {
        Edge other = (Edge) o;
        return (v1.equals(other.getV1()) && v2.equals(other.getV2()));
    }
}

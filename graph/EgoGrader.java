package graph;

import util.GraphLoader;

public class EgoGrader {
    public static void main (String[] args) {
        Graph g = new CapGraph();
        GraphLoader.loadGraph(g, "data/facebook_2000.txt");
        Graph ego = g.getEgonet(0);
        System.out.println(ego.printGraph());
    }
}

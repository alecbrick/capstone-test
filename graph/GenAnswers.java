package graph;

import java.io.PrintWriter;
import util.GraphLoader;
import java.util.Set;
import java.util.List;


public class GenAnswers {

    public static void main (String[] args) {
        //g.petition(amount);


        for(int i = 0; i < 10; i++) {
            CapGraph g = new CapGraph();

            try {
                GraphLoader.loadGraph(g, "data/scc/T" + (i +1));
                PrintWriter pw = new PrintWriter("data/sccAnswers/answer" + (i + 1));
                List<Set<Integer>> sccs = g.getSCCs();

                // loop over SCCs
                for(int j = 0; j < sccs.size(); j++) {

                    Set<Integer> scc = sccs.get(j);
                    for(Integer val : scc) {
                        pw.print(val + " ");
                    }
                    pw.print("\n");
                }
                pw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

/**
 * @author UCSD MOOC development team
 * 
 * Grader for the egonet assignment. Runs implementation against
 * ten nodes from the Facebook dataset. 
 *
 */

package graph.grader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import util.GraphLoader;
import graph.CapGraph;
import graph.Graph;

public class EgoGrader extends Grader {
    private static final int TESTS = 10;

    public static void main(String[] args) {
        Grader grader = new EgoGrader();
        Thread thread = new Thread(grader);
        thread.start();

        // Safeguard against infinite loops
        long endTime = System.currentTimeMillis() + 30000;
        boolean infinite = false;
        while (thread.isAlive()) {
            if (System.currentTimeMillis() > endTime) {
                thread.stop();
                infinite = true;
                break;
            }
        }
        if (infinite) {
            grader.feedback += "Your program entered an infinite loop or took longer than 30 seconds to finish.";
        }
        grader.out.println(makeJson((double)grader.correct / TESTS, grader.feedback));
        grader.out.close();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter("output.out");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            Graph graph = new CapGraph();
            GraphLoader.loadGraph(graph, "data/facebook_ucsd.txt");
            feedback += "\\nGRAPH: facebook_ucsd.txt\\nFailed tests will display the first mismatched lines of the output.\\n";
            for (int i = 0; i < 10; i++) {
                feedback += appendFeedback(i + 1, "Starting from node " + i);
                // Run user's implementation and turn the output into readable strings
                HashMap<Integer, HashSet<Integer>> res = graph.getEgonet(i).exportGraph();
                BufferedReader br = new BufferedReader(new FileReader("data/ego_answers/ego_" + i + ".txt"));
                String next;
                int count = 0;
                boolean failed = false;
                while ((next = br.readLine()) != null) {
                    next = next.replaceAll("[:,]", " ");
                    Scanner sc = new Scanner(next);
                    int vertex = sc.nextInt();
                    HashSet<Integer> others = res.get(vertex);
                    if (others == null) {
                        feedback += "FAILED. Egonet does not include vertex " + vertex + ".";
                        failed = true;
                        break;
                    }

                    HashSet<Integer> check = new HashSet<Integer>();
                    while(sc.hasNextInt()) {
                        check.add(sc.nextInt());
                    }
                    
                    if (!check.equals(others)) {
                        feedback += "FAILED. Expected \\\"" + next + "\\\" for vertex #" + vertex + ", got \\\"" + others + "\\\".";
                        failed = true;
                        break;
                    }
                    count++;
                } 
                if (!failed) {
                    feedback += "PASSED.";
                    correct += 1;
                }
            }
        } catch (Exception e) {
            feedback = "An error occurred during runtime.\\n" + feedback + "\\nError during runtime: " + e;
            e.printStackTrace();
        }
    }
}
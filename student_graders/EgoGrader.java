/**
 * @author UCSD MOOC development team
 * 
 * Grader for the egonet assignment. Runs implementation against
 * ten nodes from the Facebook dataset. 
 *
 */

package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import util.GraphLoader;

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
        System.out.println(makeOutput((double)grader.correct / TESTS, grader.feedback));
    }

    @Override
    public void run() {
        try {
            Graph graph = new CapGraph();
            GraphLoader.loadGraph(graph, "data/facebook_ucsd.txt");
            feedback += "\nGRAPH: facebook_ucsd.txt\nFailed tests will display the first mismatched lines of the output.\n";
            for (int i = 0; i < 10; i++) {
                feedback += appendFeedback(i + 1, "Starting from node " + i);
                // Run user's implementation and turn the output into readable strings
                String[] res = graph.getEgonet(i).printGraph().split("\n");
                BufferedReader br = new BufferedReader(new FileReader("data/ego_answers/ego_" + i + ".txt"));
                String next;
                int count = 0;
                boolean failed = false;
                while ((next = br.readLine()) != null) {
                    // Compare answers line by line
                    if (!next.equals(res[count])) {
                        feedback += "FAILED. Expected \"" + next + "\", got \"" + res[count] + "\".";
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
            feedback = "An error occurred during runtime.\n" + feedback + "\nError during runtime: " + e;
        }
    }
}

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
            feedback += "\\nGRAPH: facebook_ucsd.txt";
            for (int i = 0; i < 10; i++) {
                feedback += appendFeedback(i + 1, "Starting from node " + i);
                String[] res = graph.getEgonet(i).printGraph().split("\n");
                BufferedReader br = new BufferedReader(new FileReader("data/answers/ego_" + i + ".txt"));
                String next;
                int count = 0;
                boolean failed = false;
                while ((next = br.readLine()) != null) {
                    // Compare answers line by line
                    if (!next.equals(res[count])) {
                        feedback += "FAILED. Expected \\\"" + next + "\\\", got \\\"" + res[count] + "\\\".";
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
        }
    }
}

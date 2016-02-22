package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import util.GraphLoader;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Scanner;

public class SCCGrader extends Grader {
    private static final int TESTS = 10;

    public static void main(String[] args) {
        Grader grader = new SCCGrader();
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
            
            for(int i = 0; i < 10; i++) {
                CapGraph g = new CapGraph();
                List<Vertex> vertices;

                String answerFile = "data/sccAnswers/answer" + (i + 1);
                GraphLoader.loadGraph(g, "data/scc/T" + (i +1));
                BufferedReader br = new BufferedReader(new FileReader(answerFile));
                feedback += appendFeedback(i + 1, "\\nGRAPH: T" + (i + 1));

                // build list from answer
                List<List<Vertex>> answer = new ArrayList<List<Vertex>>();
                String line;
                  
                while((line = br.readLine()) != null) {
                    Scanner sc = new Scanner(line);
                    vertices = new ArrayList<Vertex>();
                    while(sc.hasNextInt()) {
                        vertices.add(new Vertex(sc.nextInt()));
                    }
                    answer.add(vertices);


                    sc.close();
                }

                

                // get student SCC result
                List<Set<Vertex>> sccs = g.getSCCs();


                boolean testFailed = false;
                // loop over SCCs
                for(int j = 0; j < sccs.size(); j++) {

                    Set<Vertex> scc = sccs.get(j);
                    vertices = answer.get(j);
                    
                    /* QUESTION ::: how should credit be given? i
                     * partial, all or nothing?
                     */
                    if(!vertices.containsAll(scc)) {
                        testFailed = true;
                        feedback += "FAILED. Your result did not match line " 
                                     + (j+1) + " in \"" + answerFile + "\"";
                        break;
                    }

                }

                if(!testFailed) {
                    feedback += "PASSED.";
                    correct++;
                }

                br.close();
            }
        } catch (Exception e) {
            feedback = "An error occurred during runtime.\\n" + feedback + "\\nError during runtime: " + e;
        }
    }
}

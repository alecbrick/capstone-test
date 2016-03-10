/**
 * @author UCSD MOOC development team
 * 
 * Grader for the SCC assignment.
 *
 */

package graph.grader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import util.GraphLoader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Scanner;
import graph.Graph;
import graph.CapGraph;

public class SCCGrader extends Grader {
    private int totalTests;
    private int testsPassed;

    public SCCGrader() {
        totalTests = 0;
        testsPassed = 0;
    }
    public static void main(String[] args) {
        SCCGrader grader = new SCCGrader();
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
        System.out.println(makeOutput((double)grader.testsPassed/grader.totalTests, grader.feedback));
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < 10; i++) {
                CapGraph g = new CapGraph();
                Set<Integer> vertices;

                String answerFile = "data/scc_answers/answer" + (i + 1);
                GraphLoader.loadGraph(g, "data/scc/T" + (i +1));
                BufferedReader br = new BufferedReader(new FileReader(answerFile));
                feedback += appendFeedback(i + 1, "\nGRAPH: T" + (i + 1));

                // build list from answer
                List<Set<Integer>> answer = new ArrayList<Set<Integer>>();
                String line;
                  
                while((line = br.readLine()) != null) {
                    Scanner sc = new Scanner(line);
                    vertices = new TreeSet<Integer>();
                    while(sc.hasNextInt()) {

                        // DEBUG !!!!
                        if(!vertices.add(sc.nextInt())) {
                            System.out.print("error adding to solution TreeSet");
                        }
                    }
                    answer.add(vertices);


                    sc.close();
                }

                

                // get student SCC result
                List<Set<Integer>> sccs = g.getSCCs();


                boolean testFailed = false;
                totalTests += answer.size() + sccs.size(); 
                testsPassed += answer.size() + sccs.size(); 

                Set<Integer> answerSCC = null;
                Set<Integer> scc = null;

                // loop over SCCs
                int j = 0;
                for(; j < answer.size(); j++) {

                    answerSCC = answer.get(j);
                    scc = null;

                    if(j < sccs.size()) {
                        scc = sccs.get(j);
                    }
                        
                    // check if learner result constains SCC from answer file
                    if(!sccs.contains(answerSCC)) {
                        if(!testFailed) {
                            testFailed = true;
                            feedback += "FAILED. ";
                        }
                        feedback += "Your result did not contain the scc on line "
                                     + (j+1) + " in \"" + answerFile + "\"";
                        feedback += "\n";
                        testsPassed--;
                    }

                    // check if answer contains learners scc
                    if(scc != null && !answer.contains(scc)) {
                        if(!testFailed) {
                            testFailed = true;
                            feedback += "FAILED. ";
                        }
                        feedback += "Your result contained an extra SCC : ";
                        for(Integer id : scc) {
                            feedback += id + " ";
                        }
                        feedback += "\n";
                        testsPassed--;
                    }


                }

                while(j < sccs.size()) {
                    // check if answer contains learners scc
                    if(scc != null && !answer.contains(scc)) {
                        if(!testFailed) {
                            testFailed = true;
                            feedback += "FAILED. ";
                        }
                        feedback += "Your result contained an extra SCC : ";
                        for(Integer id : scc) {
                            feedback += id + " ";
                        }
                        feedback += "\n";
                        testsPassed--;
                    }

                    j++;
                }

                if(!testFailed) {
                    feedback += "PASSED.";
                }

                br.close();
            }
            System.out.println("total Tests : " + totalTests);
            System.out.println("testsPassed : " + testsPassed);
        } catch (Exception e) {
            feedback = "An error occurred during runtime.\n" + feedback + "\nError during runtime: " + e;
        }
    }
}

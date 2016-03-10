/**
 * @author UCSD MOOC development team
 * 
 * Abstract grader class that includes methods common to concrete graders.
 *
 */

package graph.grader;

import java.io.PrintWriter;

public abstract class Grader implements Runnable {
    public String feedback = "";
    public int correct = 0;
    protected static final int TESTS = 10;
    public PrintWriter out;

    /* Formats output to be readable by Coursera */
    public static String makeJson(double score, String feedback) {
        return "{\"fractionalScore\": " + score + ", \"feedback\": \"" + feedback + "\"}";
    }

    /* Print test descriptions neatly */
    public static String appendFeedback(int num, String test) {
        return "\\n**Test #" + num + ": " + test + "...";
    }

    
    /* Required for threads 
    public abstract void run(); */
}

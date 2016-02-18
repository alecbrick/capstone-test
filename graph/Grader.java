package graph;

import java.io.PrintWriter;

public abstract class Grader implements Runnable {
    public String feedback = "";
    public int correct = 0;
    protected static final int TESTS = 10;
    public PrintWriter out;

    public static String makeJson(double score, String feedback) {
        return "{\"fractionalScore\": " + score + ", \"feedback\": \"" + feedback + "\"}";
    }

    public static String appendFeedback(int num, String test) {
        return "\\n**Test #" + num + ": " + test + "...";
    }

    public abstract void run();
}

package quizgame.util;

/**
 * Super simple logger intended for students.
 * Flip ENABLED to false to silence logs.
 */
public class StudentLogger {
    public static boolean ENABLED = true;

    public static void enter(String where) {
        if (ENABLED) System.out.println("[ENTER] " + where);
    }

    public static void step(String message) {
        if (ENABLED) System.out.println("  [STEP] " + message);
    }

    public static void exit(String where) {
        if (ENABLED) System.out.println("[EXIT ] " + where);
    }

    public static void error(String where, String message) {
        if (ENABLED) System.out.println("[ERROR] " + where + " -> " + message);
    }
}
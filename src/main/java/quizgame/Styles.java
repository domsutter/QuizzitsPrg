/*
 * ===============================================================
 * File: Styles.java
 * Simple helper to attach stylesheets.
 * ===============================================================
 */
package quizgame;

import javafx.scene.Scene;

public final class Styles {
    private Styles() {}

    public static void apply(Scene scene) {
        var url = Styles.class.getResource("styles.css");
        if (url != null) {
            String css = url.toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
        } else {
            System.err.println("[Styles] styles.css not found on classpath under quizgame/");
        }
    }
}

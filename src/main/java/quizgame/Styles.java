/*
 * ===============================================================
 * File: Styles.java
 * ===============================================================
 */

package quizgame;
import quizgame.util.StudentLogger;  

import javafx.scene.Scene;

public class Styles {

    public static void apply(Scene scene) {
        StudentLogger.enter("names#apply");  
        var url = Styles.class.getResource("styles.css");
        if (url != null) {  
            scene.getStylesheets().add(url.toExternalForm());  
        }
    }
}